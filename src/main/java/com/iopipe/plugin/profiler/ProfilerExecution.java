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
import java.io.StringWriter;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.Base64;
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
	
	/** The default sampling rate. */
	public static final int DEFAULT_SAMPLE_RATE =
		25_000_000;
	
	/** The number of nanoseconds between each polling period. */
	public static final int SAMPLE_RATE;
	
	/** The execution state. */
	protected final IOpipeExecution execution;
	
	/** Tracker state. */
	private final Tracker _tracker =
		new Tracker();
	
	/** Remote URL access lock. */
	private final Object _remotelock =
		new Object();
	
	/** Remote URL. */
	private volatile String _remote;
	
	/** Was the remote invalid? */
	private volatile boolean _remoteinvalid;
	
	/** The tread which is pollng for profiling. */
	private volatile Thread _pollthread;
	
	/** The poller for execution. */
	private volatile __Poller__ _poller;
	
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
		this._poller._stop = true;
		this._pollthread.interrupt();
		
		// Date prefix used for file export
		String prefix = DateTimeFormatter.BASIC_ISO_DATE.format(
			LocalDate.now());
		
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
			
			// Await remote URL to send to
			String remote = __awaitRemote();
			if (remote == null)
			{
				_LOGGER.error("Could not obtain the remote URL.");
				return;
			}
			
			// Build request to send to server
			RemoteRequest request = new RemoteRequest("application/zip",
				exported);
			
			// Send request
			RemoteResult result = conf.getRemoteConnectionFactory().connect(
				remote, conf.getProjectToken()).send(RequestType.POST,
				request);
			
			// Debug result
			_LOGGER.debug(() -> "Profiler recv: " + result);
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
		Object remotelock = this._remotelock;
		synchronized (remotelock)
		{
			for (;;)
			{
				String rv = this._remote;
				
				// Not specified?
				if (rv == null)
				{
					// Not valid
					if (this._remoteinvalid)
						return null;
					
					// Wait for it to be read
					else
						try
						{
							remotelock.wait();
						}
						catch (InterruptedException e)
						{
						}
				}
				
				// Use that URL
				else
					return rv;
			}
		}
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
		Object remotelock = this._remotelock;
		try
		{
			IOpipeExecution execution = this.execution;
			IOpipeConfiguration conf = execution.config();
			
			// Use URL from the profiler
			String desiredurl = conf.getProfilerUrl();
			if (desiredurl == null)
				throw new RuntimeException("No profiler URL specified.");
			
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
				gen.write("timestamp", IOpipeConstants.LOAD_TIME);
				
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
			
			// Get URL
			String url = ((JsonString)jv).getString();
			
			_LOGGER.debug(() -> "Got upload URL: " + url);
			
			// Return it
			synchronized (remotelock)
			{
				this._remote = url;
				remotelock.notifyAll();
			}
		}
		
		// Could not send to the remote end
		catch (RuntimeException|Error e)
		{
			_LOGGER.error("Could not determine the profiler upload URL.", e);
			
			// Mark invalid
			synchronized (remotelock)
			{
				this._remoteinvalid = true;
				remotelock.notifyAll();
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
		// Need to determine which server to send to, can be done in another
		// thread
		Thread getter = new Thread(this::__getRemote, "ProfilerGetURL");
		getter.setDaemon(true);
		getter.start();
		
		// Setup poller which will constantly read thread state
		__Poller__ poller = new __Poller__(this._tracker,
			this.execution.threadGroup());
		this._poller = poller;
		
		// Initialize the polling thread
		Thread pollthread = new Thread(poller);
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

