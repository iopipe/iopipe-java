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
import com.iopipe.IOpipeSigner;
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
import org.pmw.tinylog.Logger;

/**
 * This contains the execution state for the profile plugin. This class is not
 * intended to be used by the user.
 *
 * @since 2018/02/07
 */
public class ProfilerExecution
	implements IOpipePluginExecution
{
	/** The default sampling rate (in nanoseconds). */
	public static final int DEFAULT_SAMPLE_RATE =
		1_000_000;
	
	/** The number of nanoseconds between each polling period. */
	public static final int SAMPLE_RATE;
	
	/** Debug: The path to dump a local copy of the profiler information to. */
	public static final Path LOCAL_SNAPSHOT_DUMP_PATH;
	
	/** Debug: Prefix to use for filenames in the snapshot. */
	public static final String ALTERNATIVE_PREFIX;
	
	/** The service group the profiler belongs in. */
	private static final ThreadGroup _SERVICE_GROUP;
	
	/** The execution state. */
	protected final IOpipeExecution execution;
	
	/** Tracker state. */
	private final Tracker _tracker =
		new Tracker();
	
	/** The signer. */
	private final IOpipeSigner _signer;
	
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
		// Initialize the service group
		ThreadGroup sg = null;
		try
		{
			sg = new ThreadGroup("IOpipe-ServiceThreads-Profiler");
		}
		catch (SecurityException e)
		{
			sg = Thread.currentThread().getThreadGroup();
		}
		
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
		
		_SERVICE_GROUP = sg;
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
		
		// Setup signer to upload a ZIP
		this._signer = __e.signer(".zip");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/03/15
	 */
	@Override
	public JsonObject extraReport()
	{
		// Signer was not used
		IOpipeSigner signer = this._signer;
		if (signer == null)
			return null;
		
		// If no access token was specified then just ignore it
		String jwtaccesstoken = signer.accessToken();
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
			Logger.error(e, "Failed to export profiler snapshot data.");
			
			// Ignore
			exported = null;
		}
		
		// Snapshots were generated
		if (exported != null)
		{
			// Debug exported bytes to UUEncoded file data
			final byte[] fexported = exported;
			Logger.debug("\nbegin-base64 644 {}.zip\n{}\n====\n",
				() -> prefix,
				() -> Base64.getMimeEncoder().encodeToString(fexported));
			
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
			
			// Signer was not used?
			IOpipeSigner signer = this._signer;
			if (signer == null)
				return;
			
			// Upload
			try
			{
				RemoteResult result = signer.put(exported);
				
				// Debug result
				Logger.debug("Profiler upload returned result {}.", result);
				
				// Add auto-label
				execution.label("@iopipe/plugin-profiler");
			}
			catch (RemoteException e)
			{
				Logger.debug(e, "Could not upload prorifler data.");
			}
		}
	}
	
	/**
	 * Pre execution.
	 *
	 * @since 2018/02/09
	 */
	final void __pre()
	{
		// Statistics at the start of method execution
		this._beginstats = ManagementStatistics.snapshot(0);
		
		// Setup poller which will constantly read thread state
		__Poller__ poller = new __Poller__(this._tracker,
			this.execution.threadGroup());
		this._poller = poller;
		
		// Initialize the polling thread
		Thread pollthread = new Thread(_SERVICE_GROUP, poller,
			"IOpipe-ProfilerWorker");
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

