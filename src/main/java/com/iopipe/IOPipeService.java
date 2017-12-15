package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.List;
import java.util.Objects;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * This class provides access to the IOPipe service and allows for sending
 * metrics to the server.
 *
 * @since 2017/12/13
 */
public final class IOPipeService
{
	/** The version for this agent. */
	public static final String IOPIPE_AGENT_VERSION =
		"1.0-SNAPSHOT";
	
	/** The time at which the class was initialized. */
	private static final long _LOAD_TIME =
		System.currentTimeMillis();
	
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
	
	/** The execution context. */
	protected final Context context;
	
	/** The configuration used to connect to the service. */
	protected final IOPipeConfiguration config;
	
	/** The connection to the remote service. */
	protected final IOPipeHTTPConnection connection;
	
	/** The thread which is used to detect timeouts. */
	protected final Thread timeoutthread;
	
	/**
	 * Initializes the IOPipe service using the default system configuration.
	 *
	 * @param __context The context in which the lambda is running in.
	 * @throws IllegalArgumentException If the default parameters are not
	 * valid.
	 * @since 2017/12/13
	 */
	public IOPipeService(Context __context)
		throws IllegalArgumentException
	{
		this(__context, IOPipeConfiguration.byDefault());
	}
	
	/**
	 * Initializes the IOPipe service using the specified configuration.
	 *
	 * @param __context The context in which the lambda is running in.
	 * @param __config The configuration to use.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/13
	 */
	public IOPipeService(Context __context, IOPipeConfiguration __config)
		throws NullPointerException
	{
		if (__context == null || __config == null)
			throw new NullPointerException();
		
		this.context = __context;
		this.config = __config;
		
		// Attempt opening a connection to the service first because the
		// timeout window needs to be handled. Additionally if the connection
		// fails then no reports ever can be made
		IOPipeHTTPConnection connection;
		Thread timeoutthread;
		try
		{
			connection = __config.getHTTPConnectionFactory().connect();
			
			// Setup the timeout thread which waits until the final moments
			// before the 
			timeoutthread = new Thread(() ->
				{
					PrintStream debug = __config.getDebugStream();
					if (debug == null)
						debug.println("IOPipe: Awaiting timeout...");
					
					throw new Error("TODO");
				}, "IOPipe-Timeout-Thread");
			
			// Make it so the thread will exit if the VM exits
			timeoutthread.setDaemon(true);
			timeoutthread.start();
		}
		
		// Failed to connect so no reports will be generated
		catch (IOException e)
		{
			connection = null;
			timeoutthread = null;
			
			// Report failure
			PrintStream debug = __config.getDebugStream();
			if (debug != null)
				debug.printf("IOPipe: Failed to connect to the service. " +
					"(Trace: %s)%n", Arrays.toString(e.getStackTrace()));
		}
		
		this.connection = connection;
		this.timeoutthread = timeoutthread;
	}
	
	/**
	 * Runs the specified function and gathers metrics during the operation.
	 *
	 * @param __func The function to execute.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/13
	 */
	public final void run(Runnable __func)
		throws NullPointerException
	{
		if (__func == null)
			throw new NullPointerException();
		
		this.<Object, Object, Object>run(null, null, (__a, __b) ->
			{
				__func.run();
				return null;
			});
	}
	
	/**
	 * Runs the specified function and gathers metrics during the operation.
	 *
	 * @param <R> The return value.
	 * @param __func The function to execute.
	 * @return The result of the function call.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/13
	 */
	public final <R> R run(Supplier<R> __func)
		throws NullPointerException
	{
		if (__func == null)
			throw new NullPointerException();
		
		return this.<Object, Object, R>run(null, null,
			(__y, __z) -> __func.get());
	}
	
	/**
	 * Runs the specified function and gathers metrics during the operation.
	 *
	 * @param <A> The input type.
	 * @param <R> The return value.
	 * @param __a The first parameter.
	 * @param __func The function to execute.
	 * @return The result of the function call.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/13
	 */
	public final <A, R> R run(A __a, Function<A, R> __func)
		throws NullPointerException
	{
		if (__func == null)
			throw new NullPointerException();
		
		return this.<A, Object, R>run(__a, null,
			(__y, __z) -> __func.apply(__a));
	}
	
	/**
	 * Runs the specified function and gathers metrics during the operation.
	 *
	 * @param <A> The first parameter type.
	 * @param <B> The second parameter type.
	 * @param <R> The return value.
	 * @param __a The first parameter.
	 * @param __b The second parameter.
	 * @param __func The function to execute.
	 * @return The result of the function call.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/13
	 */
	public final <A, B, R> R run(A __a, B __b,
		BiFunction<A, B, R> __func)
		throws NullPointerException
	{
		if (__func == null)
			throw new NullPointerException();
		
		Context context = this.context;
		IOPipeConfiguration config = this.config;
		IOPipeHTTPConnection connection = this.connection;
		PrintStream debug = config.getDebugStream();
		
		// When disabled do not bother generating reports because they will
		// not go anywhere anyway
		// Also if the connection to the server could not be made in the
		// constructor then always treat as being disabled
		if (connection == null || !config.isEnabled())
		{
			if (debug != null)
				debug.println("IOPipe: Disabled, not wrapping function.");
			
			return __func.apply(__a, __b);
		}
		
		// Need to wrap exceptions AND time the method
		R rv = null;
		RuntimeException e = null;
		long monostart = System.nanoTime(),
			monodur;
		try
		{
			rv = __func.apply(__a, __b);
		}
		
		// Exception was thrown so a stack trace will need to be generated
		catch (RuntimeException x)
		{
			e = x;
		}
		
		// Used to determine the time
		finally
		{
			monodur = System.nanoTime() - monostart;
		}
		
		// "duration": "i"
		if (true)
			throw new Error("TODO");
		
		// The exception will need to be thrown so that this appears to be
		// transparent
		if (e != null)
			throw e;
		return rv;
	}
	
	/**
	 * Builds the AWS information object to be placed in the request.
	 *
	 * @param __context The context to source information from.
	 * @return The built object.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/14
	 */
	private JsonObject __buildJsonAwsObject(Context __context)
		throws NullPointerException
	{
		if (__context == null)
			throw new NullPointerException();
		
		JsonObjectBuilder rv = Json.createObjectBuilder();
		
		rv.add("functionName", __context.getFunctionName());
		rv.add("functionVersion", __context.getFunctionVersion());
		rv.add("awsRequestId", __context.getAwsRequestId());
		rv.add("invokedFunctionArn", __context.getInvokedFunctionArn());
		rv.add("logGroupName", __context.getLogGroupName());
		rv.add("logStreamName", __context.getLogStreamName());
		rv.add("memoryLimitInMB", __context.getMemoryLimitInMB());
		rv.add("getRemainingTimeInMillis",
			__context.getRemainingTimeInMillis());
		rv.add("traceId", Objects.toString(
			System.getenv("_X_AMZN_TRACE_ID"), "unknown"));
		
		return rv.build();
	}
	
	/**
	 * Builds the host environment information object.
	 *
	 * @return The host environment information.
	 * @since 2017/12/24
	 */
	private JsonObject __buildJsonEnvironmentObject()
	{
		JsonObjectBuilder rv = Json.createObjectBuilder();
		
		// The IOPipe Agent
		JsonObjectBuilder agent = Json.createObjectBuilder();
		
		agent.add("runtime", "java");
		agent.add("version", IOPIPE_AGENT_VERSION);
		agent.add("load_time", _LOAD_TIME);
		
		rv.add("agent", agent);
		
		// Java VM info, just copy from system properties
		JsonObjectBuilder java = Json.createObjectBuilder();
		
		for (String p : IOPipeService._COPY_PROPERTIES)
			java.add(p, System.getProperty(p, ""));
		
		rv.add("java", java);
		
		// Operating System information
		JsonObjectBuilder os = Json.createObjectBuilder();
		
		//os.add("hostname", ???);
		//os.add("totalmem", ???);
		//os.add("freemem", ???);
		//os.add("usedmem", ???);
		//os.add("cpus", ???);
		
		rv.add("os", os);
		
		return rv.build();
	}
	
	/**
	 * Builds the memory information object.
	 *
	 * @return Thec urrent state of memory.
	 * @since 2017/12/24
	 */
	private JsonObject __buildJsonMemoryObject()
	{
		JsonObjectBuilder rv = Json.createObjectBuilder();
		
		if ("linux".compareToIgnoreCase(
			System.getProperty("os.name", "unknown")) == 0)
		{
			//rv.add("rssMiB", ???);
			//rv.add("totalMiB", ???);
			//rv.add("rssTotalPercentage", ???);
		}
		
		return rv.build();
	}
	
	/**
	 * This initializes a base Json object and fills it with the common
	 * and well known information that is common between all requests.
	 *
	 * @return The builder for the report containing the shared information.
	 * @since 2017/12/13
	 */
	private JsonObjectBuilder __initializeJsonObject()
	{
		JsonObjectBuilder rv = Json.createObjectBuilder();
		
		Context context = this.context;
		IOPipeConfiguration config = this.config;
		
		// User provided information
		rv.add("client_id", config.getProjectToken());
		rv.add("installMethod", config.getInstallMethod());
		
		// System provided information
		RuntimeMXBean runtimemx = ManagementFactory.getRuntimeMXBean();
		rv.add("processId", runtimemx.getName());
		rv.add("timestamp", runtimemx.getStartTime() / 1000);
		
		// Sub-structures
		rv.add("aws", __buildJsonAwsObject(context));
		rv.add("environment", __buildJsonEnvironmentObject());
		rv.add("memory", __buildJsonMemoryObject()); 
		
		// If the process has been cold started
		//rv.add("coldstart", ???);
		
		return rv;
	}
}

