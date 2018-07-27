package com.iopipe.plugin.profiler;

import com.amazonaws.services.lambda.runtime.Context;
import com.iopipe.http.RemoteBody;
import com.iopipe.http.RemoteConnection;
import com.iopipe.http.RemoteConnectionFactory;
import com.iopipe.http.RemoteException;
import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import com.iopipe.http.RequestType;
import com.iopipe.IOpipeConfiguration;
import com.iopipe.IOpipeConstants;
import com.iopipe.IOpipeExecution;
import com.iopipe.plugin.IOpipePluginExecution;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This contains the execution state for the profile plugin. This class is not
 * intended to be used by the user.
 *
 * @since 2018/02/07
 */
public class ProfilerExecution
	implements IOpipePluginExecution
{
	/** Logging. */
	private static final Logger _LOGGER =
		LogManager.getLogger(ProfilerExecution.class);
	
	/** The default sampling rate (in nanoseconds). */
	public static final int DEFAULT_SAMPLE_RATE =
		1_000_000;
	
	/** The number of nanoseconds between each polling period. */
	public static final int SAMPLE_RATE;
	
	/** Debug: The path to dump a local copy of the profiler information to. */
	public static final Path LOCAL_SNAPSHOT_DUMP_PATH;
	
	/** Debug: Prefix to use for filenames in the snapshot. */
	public static final String ALTERNATIVE_PREFIX;
	
	/** The execution state. */
	protected final IOpipeExecution execution;
	
	/** Tracker state. */
	private final Tracker _tracker =
		new Tracker();
	
	/** The remote to access. */
	private final AtomicReference<__Remote__> _remote =
		new AtomicReference<>();
	
	/** The tread which is pollng for profiling (only in lambda thread). */
	private Thread _pollthread;
	
	/** The poller for execution (only in lambda thread). */
	private __Poller__ _poller;
	
	/** Initial statistics when the plugin is initialized. */
	private ManagementStatistics _beginstats;
	
	/**
	 * Determine the sample rate.
	 *
	 * @since 2018/02/12
	 */
	static
	{
		// Use system properties then default to the environment
		long sr;
		try
		{
			sr = Integer.parseInt(System.getProperty(
				"com.iopipe.plugin.profiler.samplerate",
				System.getenv("IOPIPE_PROFILER_SAMPLERATE")), 10) * 1000L;
		}
		
		// Could not parse a valid number
		catch (NumberFormatException e)
		{
			sr = DEFAULT_SAMPLE_RATE;
		}
		
		SAMPLE_RATE = Math.max(1,
			(int)Math.min(Integer.MAX_VALUE, sr));
		
		// Path where snapshots will be stored, optional
		String lsndp = System.getenv("IOPIPE_PROFILER_LOCAL_DUMP_PATH");
		Path pathlsndp;
		try
		{
			pathlsndp = (lsndp != null ? Paths.get(lsndp) : null);
		}
		catch (InvalidPathException e)
		{
			pathlsndp = null;
		}
		LOCAL_SNAPSHOT_DUMP_PATH = pathlsndp;
		
		// Alternative prefix for ZIP entries
		ALTERNATIVE_PREFIX = System.getenv("IOPIPE_PROFILER_ALTERNATIVE_PREFIX");
	}
	
	/**
	 * Initializes the profiler state.
	 *
	 * @param __e The execution state.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/02/03
	 */
	public ProfilerExecution(IOpipeExecution __e)
		throws NullPointerException
	{
		if (__e == null)
			throw new NullPointerException();
		
		this.execution = __e;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/03/15
	 */
	@Override
	public JsonObject extraReport()
	{
		// No remote, ignore
		__Remote__ remote = this._remote.get();
		if (remote == null)
			return null;
		
		// If no access token was specified then just ignore it
		String jwtaccesstoken = remote.jwtaccesstoken;
		if (jwtaccesstoken == null)
			return null;
		
		// Otherwise build object
		return Json.createObjectBuilder().add("uploads",
			Json.createArrayBuilder().
				add(jwtaccesstoken).
			build()).build();
	}
	
	/**
	 * Post execution.
	 *
	 * @since 2018/02/09
	 */
	final void __post()
	{
		IOpipeExecution execution = this.execution;
		IOpipeConfiguration conf = execution.config();
		
		// Tell the poller to stop and interrupt it so it wakes up from any
		// sleep state
		this._poller._stop.set(true);
		this._pollthread.interrupt();
		
		// Get statistics at the end of execution after the method has ended
		// so that way it can be seen how much they changed
		ManagementStatistics beginstats = this._beginstats,
			endstats = ManagementStatistics.snapshot(System.nanoTime() -
				beginstats.abstime);
		
		// Date prefix used for file export
		LocalDateTime now = LocalDateTime.ofInstant(Instant.ofEpochMilli(
			execution.startTimestamp()), ZoneId.of("UTC"));
		String prefix = (ALTERNATIVE_PREFIX != null ? ALTERNATIVE_PREFIX :
			DateTimeFormatter.BASIC_ISO_DATE.format(
			now.toLocalDate()) + '_' + DateTimeFormatter.ISO_LOCAL_TIME.
			format(now.toLocalTime()).replaceAll(Pattern.quote(":"), "").
			replaceAll(Pattern.quote("."), "_"));
		
		// Export tracker data to a ZIP file
		byte[] exported = null;
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			Tracker tracker = this._tracker;
			
			try (ZipOutputStream zos = new ZipOutputStream(baos))
			{
				// Do not bother compressing because the snapshot data is
				// compressed anyway. The header and footer could be compressed
				// but that would probably save only a dozen bytes so the
				// loss of speed compressing compressed data is pointless
				zos.setMethod(ZipOutputStream.DEFLATED);
				zos.setLevel(0);
				
				// Export CPU data
				zos.putNextEntry(new ZipEntry(prefix + "_cpu.nps"));
				new __CPUExport__(tracker, execution, SAMPLE_RATE).run(zos);
				zos.closeEntry();
				
				// Any entry after this point should be compressed and should
				// easily be compressed using a fast compression algorithm.
				// This is so the size of the ZIP is reduced which will
				// additionally reduce the bandwidth cost of uploading it to
				// the server.
				zos.setLevel(3);
				
				// Export statistics
				zos.putNextEntry(new ZipEntry(prefix + "_stat.csv"));
				new __StatExport__(beginstats, endstats).run(zos);
				zos.closeEntry();
				
				// Finish the ZIP
				zos.finish();
				zos.flush();
			}
			
			// Export the ZUIP
			exported = baos.toByteArray();
		}
		catch (IOException e)
		{
			_LOGGER.debug("Failed to export snapshot data.", e);
			
			// Ignore
			exported = null;
		}
		
		// Snapshots were generated
		if (exported != null)
		{
			// Debug exported bytes to UUEncoded file data
			final byte[] fexported = exported;
			_LOGGER.debug(() -> "\nbegin-base64 644 " + prefix + ".zip\n" +
				Base64.getMimeEncoder().encodeToString(fexported) +
				"\n====\n");
			
			// This is optional but when the debugging environment variable is
			// set then this will write the file which is to be sent to IOpipe
			// to the specified path.
			Path localdump = LOCAL_SNAPSHOT_DUMP_PATH;
			if (localdump != null)
				try (OutputStream os = Files.newOutputStream(localdump,
					StandardOpenOption.WRITE,
					StandardOpenOption.TRUNCATE_EXISTING,
					StandardOpenOption.CREATE))
				{
					os.write(fexported);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			
			// Await remote URL to send to
			String remote = __awaitRemote();
			if (remote == null)
			{
				_LOGGER.error("Could not obtain the remote URL.");
				return;
			}
			
			// Build request to send to server
			RemoteRequest request = new RemoteRequest("", exported);
			
			// Send request
			RemoteResult result = conf.getRemoteConnectionFactory().connect(
				remote, null).send(RequestType.PUT,
				request);
			
			// Debug result
			_LOGGER.debug(() -> "Profiler recv: " + result + " " +
				result.bodyAsString());
			
			// Add auto-label
			execution.label("@iopipe/plugin-profiler");
		}
	}
	
	/**
	 * Awaits the remote URL.
	 *
	 * @return The remote URL.
	 * @since 2018/02/22
	 */
	private final String __awaitRemote()
	{
		AtomicReference<__Remote__> atom = this._remote;
		
		// Burn CPU for a bit waiting for the remote
		__Remote__ rv;
		while ((rv = atom.get()) == null)
			continue;
		
		return rv.url;
	}
	
	/**
	 * Obtains the remote URL to send a report to.
	 *
	 * @since 2018/02/22
	 */
	private final void __getRemote()
	{
		// Use a connection to an alternative URL using the same connection
		// type as the other.
		try
		{
			IOpipeExecution execution = this.execution;
			IOpipeConfiguration conf = execution.config();
			
			// Use URL from the profiler
			String desiredurl = conf.getProfilerUrl();
			if (desiredurl == null)
				throw new RuntimeException("No profiler URL specified.");
			
			// Indicate where the profiler is uploading to
			_LOGGER.debug(() -> "Profiler URL: " + desiredurl);
			
			// Setup connection to the signed service to determine which
			// URL we upload to
			Context context = execution.context();
			RemoteConnectionFactory fact = conf.getRemoteConnectionFactory();
			RemoteConnection con = fact.connect(desiredurl,
				conf.getProjectToken());
			
			// Build request to remote end
			StringWriter out = new StringWriter();
			try (JsonGenerator gen = Json.createGenerator(out))
			{
				gen.writeStartObject();
				
				gen.write("arn", context.getInvokedFunctionArn());
				gen.write("requestId", context.getAwsRequestId());
				gen.write("timestamp", execution.startTimestamp());
				gen.write("extension", ".zip");
				
				// Finished
				gen.writeEnd();
				gen.flush();
			}
			
			// Ask which URL to send to
			RemoteResult resp = con.send(RequestType.POST,
				new RemoteRequest(RemoteBody.MIMETYPE_JSON, out.toString()));
			
			// Decode response
			JsonObject jo = (JsonObject)resp.bodyAsJsonStructure();
			JsonValue jv = jo.get("signedRequest");
			if (jv == null)
				throw new RuntimeException("Server did not respond with URL.");
			String url = ((JsonString)jv).getString();
			
			// Need access token to tell the dashboard where to find the
			// uploaded file
			JsonValue atv = jo.get("jwtAccess");
			if (atv == null)
				throw new RuntimeException("Server did not access token.");
			String jwtaccesstoken = ((JsonString)atv).getString();
			
			_LOGGER.debug(() -> "Got upload URL: " + url);
			_LOGGER.debug(() -> "Got access token: " + jwtaccesstoken);
			_LOGGER.debug(() -> "Signer sent: " + resp + " " +
				resp.bodyAsString());
			
			// Return it
			this._remote.set(new __Remote__(true, url, jwtaccesstoken));
		}
		
		// Could not send to the remote end
		catch (Throwable e)
		{
			_LOGGER.error("Could not determine the profiler upload URL.", e);
			
			// Mark invalid
			this._remote.set(new __Remote__(false, null, null));
		}
	}
	
	/**
	 * Pre execution.
	 *
	 * @since 2018/02/09
	 */
	final void __pre()
	{
		// Need to determine which server to send to, can be done in another
		// thread
		Thread getter = new Thread(this::__getRemote, "IOpipe-ProfilerGetURL");
		getter.setDaemon(true);
		getter.start();
		
		// Statistics at the start of method execution
		this._beginstats = ManagementStatistics.snapshot(0);
		
		// Setup poller which will constantly read thread state
		__Poller__ poller = new __Poller__(this._tracker,
			this.execution.threadGroup());
		this._poller = poller;
		
		// Initialize the polling thread
		Thread pollthread = new Thread(poller, "IOpipe-ProfilerWorker");
		pollthread.setDaemon(true);
		
		// Set a higher priority if that is possible so that way the traces
		// run as soon as they can, but this might not be permitted
		try
		{
			pollthread.setPriority(Thread.MAX_PRIORITY);
		}
		catch (SecurityException e)
		{
		}
		
		// Start it
		pollthread.start();
		this._pollthread = pollthread;
	}
}

