package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
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
		RuntimeMXBean runtimemx = ManagementFactory.getRuntimeMXBean();
		OperatingSystemMXBean osbean =
			ManagementFactory.getOperatingSystemMXBean();
		
		// User provided information
		rv.add("client_id", config.getProjectToken());
		rv.add("installMethod", config.getInstallMethod());
		
		// System provided information
		rv.add("processId", runtimemx.getName());
		rv.add("timestamp", runtimemx.getStartTime() / 1000);
		
		// If the process has been cold started
		//rv.add("coldstart", ???);
		
		// Memory
		JsonObjectBuilder memory = Json.createObjectBuilder();
		//rv.add("rssMiB", ???);
		//rv.add("totalMiB", ???);
		//rv.add("rssTotalPercentage", ???);
		rv.add("memory", memory); 
		
		// Information provided by Amazon
		JsonObjectBuilder aws = Json.createObjectBuilder();
		aws.add("functionName", context.getFunctionName());
		aws.add("functionVersion", context.getFunctionVersion());
		aws.add("awsRequestId", context.getAwsRequestId());
		aws.add("invokedFunctionArn", context.getInvokedFunctionArn());
		aws.add("logGroupName", context.getLogGroupName());
		aws.add("logStreamName", context.getLogStreamName());
		aws.add("memoryLimitInMB", context.getMemoryLimitInMB());
		aws.add("getRemainingTimeInMillis",
			context.getRemainingTimeInMillis());
		aws.add("traceId", Objects.toString(
			System.getenv("_X_AMZN_TRACE_ID"), "unknown"));
		rv.add("aws", aws);
		
		// IOPipe Agent
		JsonObjectBuilder agent = Json.createObjectBuilder();
		agent.add("runtime", "java");
		agent.add("version", IOPIPE_AGENT_VERSION);
		//agent.add("load_time", ???);
		
		// Operating System information
		JsonObjectBuilder os = Json.createObjectBuilder();
		//os.add("hostname", ???);
		//os.add("totalmem", ???);
		//os.add("freemem", ???);
		//os.add("usedmem", ???);
		//os.add("cpus", ???);
		
		// JVM information
		JsonObjectBuilder java = Json.createObjectBuilder();
		java.add("java.vm.name", System.getProperty("java.vm.name"));
		java.add("java.vm.vendor", System.getProperty("java.vm.vendor"));
		java.add("java.vm.version", System.getProperty("java.vm.version"));
		
		// Place these together in one object
		JsonObjectBuilder environment = Json.createObjectBuilder();
		environment.add("agent", agent);
		environment.add("java", java);
		environment.add("os", os);
		rv.add("environment", environment);
		
		return rv;
	}
}

