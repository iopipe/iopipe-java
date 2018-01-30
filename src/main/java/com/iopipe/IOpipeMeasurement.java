package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import com.iopipe.http.RemoteException;
import com.iopipe.http.RemoteRequest;
import com.iopipe.plugin.IOpipePlugin;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.stream.JsonGenerator;

/**
 * This class is used to keep track of measurements during execution.
 *
 * @since 2017/12/15
 */
public final class IOpipeMeasurement
{
	/** Is this a Linux system? */
	private static final boolean _IS_LINUX =
		"linux".compareToIgnoreCase(
			System.getProperty("os.name", "unknown")) == 0;

	/** The configuration. */
	protected final IOpipeConfiguration config;

	/** The context this is taking the measurement for. */
	protected final Context context;
	
	/** The service which initialized this class. */
	protected final IOpipeService service;
	
	/**
	 * Performance entries which have been added to the measurement, this
	 * field is locked since multiple threads may be adding entries.
	 */
	private final Set<TracePerformanceEntry> _perfentries =
		new TreeSet<>();
	
	/** Custom metrics that have been added, locked for thread safety. */
	private final Set<CustomMetric> _custmetrics =
		new TreeSet<>();
	
	/** The exception which may have been thrown. */
	private volatile Throwable _thrown;

	/** The duration of execution in nanoseconds. */
	private volatile long _duration =
		Long.MIN_VALUE;

	/** Is this execution one which is a cold start? */
	private volatile boolean _coldstart;

	/**
	 * Initializes the measurement holder.
	 *
	 * @param __config The configuration for the context.
	 * @param __context The context this holds measurements for.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/17
	 */
	IOpipeMeasurement(IOpipeConfiguration __config, Context __context,
		IOpipeService __sv)
		throws NullPointerException
	{
		if (__config == null || __context == null || __sv == null)
			throw new NullPointerException();

		this.config = __config;
		this.context = __context;
		this.service = __sv;
	}
	
	/**
	 * Adds a single custom metric to the report.
	 *
	 * @param __cm The custom metric to add.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/20
	 */
	public void addCustomMetric(CustomMetric __cm)
		throws NullPointerException
	{
		if (__cm == null)
			throw new NullPointerException();
		
		// Multiple threads can add metrics at one time
		Set<CustomMetric> custmetrics = this._custmetrics;
		synchronized (custmetrics)
		{
			custmetrics.add(__cm);
		}
	}
	
	/**
	 * Adds a single performance entry to the report.
	 *
	 * @param __e The entry to add to the report.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/19
	 */
	public void addPerformanceEntry(TracePerformanceEntry __e)
		throws NullPointerException
	{
		if (__e == null)
			throw new NullPointerException();
		
		// Multiple threads could be adding entries
		Set<TracePerformanceEntry> perfentries = this._perfentries;
		synchronized (perfentries)
		{
			perfentries.add(__e);
		}
	}
	
	/**
	 * Adds the specified custom metric with a string value.
	 *
	 * @param __name The matric name.
	 * @param __sv The string value.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/20
	 */
	public final void customMetric(String __name, String __sv)
		throws NullPointerException
	{
		if (__name == null || __sv == null)
			throw new NullPointerException();
		
		this.addCustomMetric(new CustomMetric(__name, __sv));
	}
	
	/**
	 * Adds the specified custom metric with a long value.
	 *
	 * @param __name The matric name.
	 * @param __lv The long value.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/20
	 */
	public final void customMetric(String __name, long __lv)
		throws NullPointerException
	{
		if (__name == null)
			throw new NullPointerException();
		
		this.addCustomMetric(new CustomMetric(__name, __lv));
	}

	/**
	 * Builds the request which is sent to the remote service.
	 *
	 * @return The remote request to send to the service.
	 * @throws RemoteException If the request could not be built.
	 * @since 2017/12/17
	 */
	public RemoteRequest buildRequest()
		throws RemoteException
	{
		Context aws = this.context;
		IOpipeConfiguration config = this.config;

		// Snapshot system information
		SystemMeasurement sysinfo = new SystemMeasurement();
		
		// The current timestamp
		long nowtimestamp = System.currentTimeMillis();
		
		StringWriter out = new StringWriter();
		try (JsonGenerator gen = Json.createGenerator(out))
		{
			gen.writeStartObject();

			gen.write("client_id", config.getProjectToken());
			// UNUSED: "projectId": "s"
			gen.write("installMethod",
				Objects.toString(config.getInstallMethod(), "unknown"));

			long duration = this._duration;
			if (duration >= 0)
				gen.write("duration", duration);

			gen.write("processId", sysinfo.pid);
			gen.write("timestamp", IOpipeConstants.LOAD_TIME);
			gen.write("timestampEnd", nowtimestamp);
			
			// AWS Context information
			gen.writeStartObject("aws");

			gen.write("functionName", aws.getFunctionName());
			gen.write("functionVersion", aws.getFunctionVersion());
			gen.write("awsRequestId", aws.getAwsRequestId());
			gen.write("invokedFunctionArn", aws.getInvokedFunctionArn());
			gen.write("logGroupName", aws.getLogGroupName());
			gen.write("logStreamName", aws.getLogStreamName());
			gen.write("memoryLimitInMB", aws.getMemoryLimitInMB());
			gen.write("getRemainingTimeInMillis",
				aws.getRemainingTimeInMillis());
			gen.write("traceId", Objects.toString(
				System.getenv("_X_AMZN_TRACE_ID"), "unknown"));

			gen.writeEnd();

			// Memory Usage -- UNUSED
			/*gen.writeStartObject("memory");

			gen.write("rssMiB", );
			gen.write("totalMiB", );
			gen.write("rssTotalPercentage", );

			gen.writeEnd();*/

			// Environment start
			gen.writeStartObject("environment");

			// Agent
			gen.writeStartObject("agent");
			gen.write("runtime", "java");
			gen.write("version", IOpipeConstants.AGENT_VERSION);
			gen.write("load_time", IOpipeConstants.LOAD_TIME);
			gen.writeEnd();

			// Runtime information
			gen.writeStartObject("runtime");
			gen.write("name", "java");
			gen.write("version", System.getProperty("java.version", ""));
			gen.write("vendor", System.getProperty("java.vendor", ""));
			gen.write("vmVendor", System.getProperty("java.vm.vendor", ""));
			gen.write("vmVersion", System.getProperty("java.vm.version", ""));
			gen.writeEnd();

			// Unique operating system boot identifier
			gen.writeStartObject("host");

			gen.write("boot_id", SystemMeasurement.BOOTID);

			gen.writeEnd();

			// Operating System Start
			gen.writeStartObject("os");

			long totalmem, freemem;
			gen.write("hostname", SystemMeasurement.HOSTNAME);
			gen.write("totalmem", (totalmem = sysinfo.memorytotalkib));
			gen.write("freemem", (freemem = sysinfo.memoryfreekib));
			gen.write("usedmem", totalmem - freemem);

			// Start CPUs
			gen.writeStartArray("cpus");

			List<SystemMeasurement.Cpu> cpus = sysinfo.cpus;
			for (int i = 0, n = cpus.size(); i < n; i++)
			{
				SystemMeasurement.Cpu cpu = cpus.get(i);

				gen.writeStartObject();
				gen.writeStartObject("times");

				gen.write("idle", cpu.idle);
				gen.write("irq", cpu.irq);
				gen.write("sys", cpu.sys);
				gen.write("user", cpu.user);
				gen.write("nice", cpu.nice);

				gen.writeEnd();
				gen.writeEnd();
			}

			// End CPUs
			gen.writeEnd();

			// Linux information
			if (_IS_LINUX)
			{
				// Start Linux
				gen.writeStartObject("linux");

				// Start PID
				gen.writeStartObject("pid");

				// Start self
				gen.writeStartObject("self");

				gen.writeStartObject("stat");

				SystemMeasurement.Times times = new SystemMeasurement.Times();
				gen.write("utime", times.utime);
				gen.write("stime", times.stime);
				gen.write("cutime", times.cutime);
				gen.write("cstime", times.cstime);

				gen.writeEnd();

				gen.writeStartObject("stat_start");

				times = IOpipeService._STAT_START;
				gen.write("utime", times.utime);
				gen.write("stime", times.stime);
				gen.write("cutime", times.cutime);
				gen.write("cstime", times.cstime);

				gen.writeEnd();

				gen.writeStartObject("status");

				gen.write("VmRSS", sysinfo.vmrsskib);
				gen.write("Threads", sysinfo.threads);
				gen.write("FDSize", sysinfo.fdsize);

				gen.writeEnd();

      			// End self
      			gen.writeEnd();

				// End PID
				gen.writeEnd();

				// End Linux
				gen.writeEnd();
			}

			// Operating System end
			gen.writeEnd();

			// Environment end
			gen.writeEnd();

			Throwable thrown = this._thrown;
			if (thrown != null)
			{
				gen.writeStartObject("errors");

				// Write the stack as if it were normally output on the console
				StringWriter trace = new StringWriter();
				try (PrintWriter pw = new PrintWriter(trace))
				{
					thrown.printStackTrace(pw);

					pw.flush();
				}

				gen.write("stack", trace.toString());
				gen.write("name", thrown.getClass().getName());
				gen.write("message",
					Objects.toString(thrown.getMessage(), ""));
				// UNUSED: "stackHash": "s",
				// UNUSED: "count": "n"

				gen.writeEnd();
			}

			gen.write("coldstart", this._coldstart);
			
			// Add custom metrics, which multiple threads could be adding at
			// once
			Set<CustomMetric> custmetrics = this._custmetrics;
			synchronized (custmetrics)
			{
				if (!custmetrics.isEmpty())
				{
					gen.writeStartArray("custom_metrics");
					
					for (CustomMetric cm : custmetrics)
					{
						gen.writeStartObject();
						
						gen.write("name", cm.name());
						
						if (cm.hasString())
							gen.write("s", cm.stringValue());
						if (cm.hasLong())
							gen.write("n", cm.longValue());
						
						gen.writeEnd();
					}
					
					gen.writeEnd();
				}
			}
			
			// Multiple threads may have stored performance entries, so it
			// is possible that the list may be in a state where it is
			// inconsistent due to cache differences
			Set<TracePerformanceEntry> perfentries = this._perfentries;
			synchronized (perfentries)
			{
				if (!perfentries.isEmpty())
				{
					// Entries are stored in an array
					gen.writeStartArray("performanceEntries");
					
					// Write each entry
					for (TracePerformanceEntry e : perfentries)
					{
						gen.writeStartObject();
						
						gen.write("name",
							Objects.toString(e.name(), "unknown"));
						gen.write("startTime", e.startTimeMillis());
						gen.write("duration",
							e.durationNanoTime() / 1_000_000L);
						gen.write("entryType",
							Objects.toString(e.type(), "unknown"));
						gen.write("timestamp", nowtimestamp);
						
						gen.writeEnd();
					}
					
					// End of array
					gen.writeEnd();
				}
			}
			
			// Record plugins which are being used
			IOpipePlugin[] plugins = this.service.__plugins();
			if (plugins.length > 0)
			{
				gen.writeStartArray("plugins");
				
				for (IOpipePlugin p : plugins)
				{
					gen.writeStartObject();
					
					gen.write("name", Objects.toString(p.name(), ""));
					gen.write("version", Objects.toString(p.version(), ""));
					gen.write("homepage", Objects.toString(p.homepage(), ""));
					gen.write("enabled", true);
					
					gen.writeEnd();
				}
				
				gen.writeEnd();
			}
			
			// Finished
			gen.writeEnd();
			gen.flush();
		}
		catch (JsonException e)
		{
			throw new RemoteException("Could not build request", e);
		}

		return new RemoteRequest(out.toString());
	}

	/**
	 * Returns the execution duration.
	 *
	 * @return The execution duration, if this is negative then it is not
	 * valid.
	 * @since 2017/12/15
	 */
	public long getDuration()
	{
		return this._duration;
	}

	/**
	 * Returns the thrown throwable.
	 *
	 * @return The throwable which was thrown or {@code null} if none was
	 * thrown.
	 * @since 2017/12/15
	 */
	public Throwable getThrown()
	{
		return this._thrown;
	}

	/**
	 * Sets whether or not the execution was a cold start. A cold start
	 * indicates that the JVM was started fresh and a previous instance is not
	 * being reused.
	 *
	 * @param __cold If {@code true} then the execution follows a cold start.
	 * @since 2017/12/20
	 */
	void __setColdStart(boolean __cold)
	{
		this._coldstart = __cold;
	}

	/**
	 * Sets the duration of execution.
	 *
	 * @param __ns The execution duration in nanoseconds.
	 * @since 2017/12/15
	 */
	void __setDuration(long __ns)
	{
		this._duration = __ns;
	}

	/**
	 * Sets the throwable generated during execution.
	 *
	 * @param __t The generated throwable.
	 * @since 2017/12/15
	 */
	void __setThrown(Throwable __t)
	{
		this._thrown = __t;
	}
}
