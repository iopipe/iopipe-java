package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class provides access to the IOPipe service and allows for sending
 * metrics to the server.
 *
 * @since 2017/12/13
 */
public final class IOPipeService
{
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
		
		if (true)
			throw new Error("TODO");
		
		// The exception will need to be thrown so that this appears to be
		// transparent
		if (e != null)
			throw e;
		return rv;
	}
}

