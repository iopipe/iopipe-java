package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import com.iopipe.http.RemoteException;
import com.iopipe.http.RemoteRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
	
	/** The system properties to copy in the environment report. */
	private static final List<String> _COPY_PROPERTIES =
		Collections.<String>unmodifiableList(Arrays.<String>asList(
			"java.version", "java.vendor", "java.vendor.url",
			"java.vm.specification.version",
			"java.vm.specification.vendor", "java.vm.specification.name",
			"java.vm.version", "java.vm.vendor", "java.vm.name",
			"java.specification.version", "java.specification.vendor",
			"java.specification.name", "java.class.version",
			"java.compiler", "os.name", "os.arch", "os.version",
			"file.separator", "path.separator"));
	
	/** The configuration. */
	protected final IOpipeConfiguration config;
	
	/** The context this is taking the measurement for. */
	protected final Context context;
	
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
	public IOpipeMeasurement(IOpipeConfiguration __config, Context __context)
		throws NullPointerException
	{
		if (__config == null || __context == null)
			throw new NullPointerException();
		
		this.config = __config;
		this.context = __context;
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
			gen.write("timestampEnd", System.currentTimeMillis());
			
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
			
			// Java information
			gen.writeStartObject("java");
			
			for (String prop : IOpipeMeasurement._COPY_PROPERTIES)
				gen.write(prop, System.getProperty(prop, ""));
			
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

