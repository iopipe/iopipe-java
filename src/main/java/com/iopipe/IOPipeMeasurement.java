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
import java.util.List;
import java.util.Objects;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.stream.JsonGenerator;

/**
 * This class is used to keep track of measurements during execution.
 *
 * @since 2017/12/15
 */
public final class IOPipeMeasurement
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
	
	/** The context this is taking the measurement for. */
	protected final IOPipeContext context;
	
	/** The exception which may have been thrown. */
	private volatile Throwable _thrown;
	
	/** The duration of execution in nanoseconds. */
	private volatile long _duration =
		Long.MIN_VALUE;
	
	/**
	 * Initializes the measurement holder.
	 *
	 * @param __c The context this holds measurements for.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/17
	 */
	public IOPipeMeasurement(IOPipeContext __c)
		throws NullPointerException
	{
		if (__c == null)
			throw new NullPointerException();
		
		this.context = __c;
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
		IOPipeContext context = this.context;
		IOPipeConfiguration config = context.config();
		Context aws = context.context();
		
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
			
			if (true)
				throw new Error("TODO");
			/*gen.write("processId", "" + mypid.getProcessID());*/
			gen.write("timestamp", IOPipeConstants.LOAD_TIME);
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
			gen.write("version", IOPipeConstants.AGENT_VERSION);
			gen.write("load_time", IOPipeConstants.LOAD_TIME);
			
			gen.writeEnd();
			
			// Java information
			gen.writeStartObject("java");
			
			for (String prop : IOPipeMeasurement._COPY_PROPERTIES)
				gen.write(prop, System.getProperty(prop, ""));
			
			gen.writeEnd();
			
			// Unique operating system boot identifier
			gen.writeStartObject("host");
			
			if (_IS_LINUX)
			{
				String bootid = IOPipeMeasurement.__readFirstLine(
					Paths.get("/proc/sys/kernel/random/boot_id"));
				if (bootid != null)
					gen.write("boot_id", bootid);
			}
			
			gen.writeEnd();
			
			// Operating System Start
			gen.writeStartObject("os");
			
			long totalmem, freemem;
			if (true)
				throw new Error("TODO");
			/*
			gen.write("hostname", network.getHostName());
			gen.write("totalmem", (totalmem = memory.getTotal()));
			gen.write("freemem", (freemem = memory.getAvailable()));
			gen.write("usedmem", totalmem - freemem);
			*/
			
			// Start CPUs
			gen.writeStartArray("cpus");
			
			if (true)
				throw new Error("TODO");
			/*
			long[][] loadticks = cpu.getProcessorCpuLoadTicks();
			for (int i = 0, n = cpu.getLogicalProcessorCount(); i < n; i++)
			{
				gen.writeStartObject();
				gen.writeStartObject("times");
				
				gen.write("idle", loadticks[i][TickType.IDLE.ordinal()]);
				gen.write("irq", loadticks[i][TickType.IRQ.ordinal()]);
				gen.write("sys", loadticks[i][TickType.SYSTEM.ordinal()]);
				gen.write("user", loadticks[i][TickType.USER.ordinal()]);
				gen.write("nice", loadticks[i][TickType.NICE.ordinal()]);
				
				gen.writeEnd();
				gen.writeEnd();
			}
			*/
			
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
				
				if (true)
					throw new Error("TODO");
				/*gen.write("utime", mypid.getUserTime());
				gen.write("stime", mypid.getKernelTime());
				gen.write("cutime", mypid.getUserTime());
				gen.write("cstime", mypid.getKernelTime());*/
				
				gen.writeEnd();
				
				gen.write("stat_start", IOPipeService._STAT_START);
				
				gen.writeStartObject("status");
				
				if (true)
					throw new Error("TODO");
				/*gen.write("VmRSS", mypid.getResidentSetSize());
				gen.write("Threads", mypid.getThreadCount());
				gen.write("FDSize", os.getFileSystem().
					getOpenFileDescriptors());*/
				
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
			
			gen.write("coldstart", context._coldstarted);
			
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
	 * Sets the duration of execution.
	 *
	 * @param __d The execution duration in nanoseconds.
	 * @since 2017/12/15
	 */
	public void setDuration(long __ns)
	{
		this._duration = __ns;
	}
	
	/**
	 * Sets the throwable generated during execution.
	 *
	 * @param __t The generated throwable.
	 * @since 2017/12/15
	 */
	public void setThrown(Throwable __t)
	{
		this._thrown = __t;
	}
	
	/**
	 * Reads the first non-empty line for the given path.
	 *
	 * @param __p The path to read.
	 * @return The first non-empty line or {@code null} if the file could not
	 * be read or has only empty lines.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/17
	 */
	private static String __readFirstLine(Path __p)
		throws NullPointerException
	{
		if (__p == null)
			throw new NullPointerException();
		
		try
		{
			for (String l : Files.readAllLines(__p))
			{
				l = l.trim();
				if (!l.isEmpty())
					return l;
			}
			
			return null;
		}
		
		catch (IOException e)
		{
			return null;
		}
	}
}

