package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import com.iopipe.http.RemoteConnection;
import com.iopipe.http.RemoteException;
import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import java.io.PrintStream;
import java.util.function.Supplier;
import javax.json.JsonObject;

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
	protected final IOPipeTimeOutManager timeout;
	
	/** Connection to the server. */
	protected final RemoteConnection connection;
	
	/** Was this context cold started? */
	final boolean _coldstarted;
	
	/** The number of times this context has been executed. */
	private volatile int _execcount;
	
	/** The number of times the server replied with a code other than 2xx. */
	private volatile int _badresultcount;
	
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
		IOPipeTimeOutManager __timeout, RemoteConnection __connection)
		throws NullPointerException
	{
		if (__context == null || __config == null || __connection == null)
			throw new NullPointerException();
		
		this.context = __context;
		this.config = __config;
		this.timeout = __timeout;
		this.connection = __connection;
		this._coldstarted = !IOPipeService._THAWED.getAndSet(true);
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
	 * Returns the number of requests which would have been accepted by the
	 * service if the configuration was correct and the service was enabled.
	 *
	 * @return The number of requests which would have been accepted by the
	 * server.
	 * @since 2017/12/18
	 */
	public final int getBadResultCount()
	{
		return this._badresultcount;
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
		
		// If disabled, just run the function
		IOPipeConfiguration config = this.config;
		if (!config.isEnabled())
		{
			this._badresultcount++;
			return __func.get();
		}
		
		// Force the initial stat entry to be initialized
		IOPipeService.__statStart();
		
		Thread thread = Thread.currentThread();
		IOPipeTimeOutManager timeout = this.timeout;
		boolean usewindow = (config.getTimeOutWindow() > 0);
		
		// Register timeout with this execution number so if execution takes
		// longer than expected a timeout is generated
		int execcount = this._execcount++;
		IOPipeTimeOutManager.Active active = null; 
		if (usewindow)
			active = timeout.register(this, execcount, thread);
		
		// This method either returns a value or throwsn
		R rv = null;
		Throwable rt = null;
		
		// Regardless of any error, timeouts must be handled
		long starttime = System.nanoTime(),
			duration;
		boolean timedout = false;
		IOPipeMeasurement measurement = new IOPipeMeasurement(this);
		try
		{
			rv = __func.get();
		}
		
		// An exception or error was thrown, so that will be reported
		// Error is very fatal, but still report that it occured
		catch (RuntimeException|Error e)
		{
			rt = e;
			
			measurement.setThrown(e);
		}
		
		// Indicate that execution has finished to the timeout manager
		// so that it no longer reports timeouts
		finally
		{
			duration = starttime = System.nanoTime() - starttime;
			if (usewindow)
				timedout = timeout.finished(this, execcount);
			
			measurement.setDuration(duration);
		}
		
		// Generate and send result to server
		if (active == null || !active._generated.getAndSet(true))
			this.__sendRequest(measurement.buildRequest());
		
		// Throw the called exception as if the wrapper did not have any
		// trouble
		if (rt != null)
			if (rt instanceof Error)
				throw (Error)rt;
			else
				throw (RuntimeException)rt;
		return rv;
	}
	
	/**
	 * Sends the specified request to the server.
	 *
	 * @param __r The request to send to the server.
	 * @return The result of the report.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/15
	 */
	final RemoteResult __sendRequest(RemoteRequest __r)
		throws NullPointerException
	{
		if (__r == null)
			throw new NullPointerException();
		
		// Generate report
		try
		{
			// Report what is to be sent
			PrintStream debug = config.getDebugStream();
			if (debug != null)
				debug.printf("IOPipe: Send: %s%n", __r);
			
			RemoteResult result = this.connection.send(__r);
			
			// Report what was received
			if (debug != null)
				debug.printf("IOPipe: Result %d: %s%n", result.code(),
					result.body());
			
			// Only the 200 range is valid for okay responses
			if ((result.code() / 100) != 2)
				this._badresultcount++;
			
			return result;
		}
		
		// Failed to write to the server
		catch (RemoteException e)
		{
			e.printStackTrace(this.config.getFatalErrorStream());
			
			this._badresultcount++;
			return new RemoteResult(503, "");
		}
	}
}

