package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import java.util.function.Supplier;

/**
 * This class provides a means of wrapping the IOPipe service and then
 * generating a report for each operation. This class may be called multiple
 * times as needed if the context remains the same throughout multiple
 * invocations.
 *
 * @since 2017/12/14
 */
public final class IOPipeContext
{
	/** The context in which this runs under. */
	protected final Context context;
	
	/** The service configuration. */
	protected final IOPipeConfiguration config;
	
	/** Used to track call timeouts. */
	protected final IOPipeTimeoutManager timeout;
	
	/** Connection to the server. */
	protected final IOPipeHTTPConnection connection;
	
	/** The number of times this context has been executed. */
	private volatile int _execcount;
	
	/**
	 * Initializes this class and wraps the given execution context.
	 *
	 * @param __context The context to manage.
	 * @param __config The configuration for the service.
	 * @param __timeout The timeout manager.
	 * @param __connection The connection to the IOPipe service.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/14
	 */
	IOPipeContext(Context __context, IOPipeConfiguration __config,
		IOPipeTimeoutManager __timeout, IOPipeHTTPConnection __connection)
		throws NullPointerException
	{
		if (__context == null || __config == null || __connection == null)
			throw new NullPointerException();
		
		this.context = __context;
		this.config = __config;
		this.timeout = __timeout;
		this.connection = __connection;
	}
	
	/**
	 * Returns the context configuration.
	 *
	 * @return The configuration for the context.
	 * @since 2017/12/15
	 */
	public final IOPipeConfiguration config()
	{
		return this.config;
	}
	
	/**
	 * Returns the context this is executing for.
	 *
	 * @return The executing context.
	 * @since 2017/12/15
	 */
	public final Context context()
	{
		return this.context;
	}
	
	/**
	 * Runs the specified function and generates a report.
	 *
	 * @param <R> The value to return.
	 * @param __func The function to call which will get a generated report.
	 * @return The returned value.
	 * @throws Error If the called function threw an error.
	 * @throws NullPointerException On null arguments.
	 * @throws RuntimeException If the called function threw an exception.
	 * @since 2017/12/14
	 */
	public final <R> R run(Supplier<R> __func)
		throws Error, NullPointerException, RuntimeException
	{
		if (__func == null)
			throw new NullPointerException();
		
		IOPipeConfiguration config = this.config;
		IOPipeTimeoutManager timeout = this.timeout;
		boolean usewindow = (config.getTimeOutWindow() > 0);
		
		// Register timeout with this execution number so if execution takes
		// longer than expected a timeout is generated
		int execcount = this._execcount++;
		if (usewindow)
			timeout.register(this, execcount);
		
		// This method either returns a value or throwsn
		R rv = null;
		Throwable rt = null;
		
		// Regardless of any error, timeouts must be handled
		long starttime = System.nanoTime(),
			duration;
		boolean timedout = false;
		try
		{
			rv = __func.get();
		}
		
		// An exception or error was thrown, so that will be reported
		// Error is very fatal, but still report that it occured
		catch (RuntimeException|Error e)
		{
			rt = e;
		}
		
		// Indicate that execution has finished to the timeout manager
		// so that it no longer reports timeouts
		finally
		{
			duration = starttime = System.nanoTime() - starttime;
			if (usewindow)
				timedout = timeout.finished(this, execcount);
		}
		
		// Generate report
		// TODO
		
		// Throw the called exception as if the wrapper did not have any
		// trouble
		if (rt != null)
			if (rt instanceof Error)
				throw (Error)rt;
			else
				throw (RuntimeException)rt;
		return rv;
	}
}

