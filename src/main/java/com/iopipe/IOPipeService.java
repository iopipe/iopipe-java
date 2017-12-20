package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import com.iopipe.http.NullConnection;
import com.iopipe.http.RemoteConnection;
import com.iopipe.http.RemoteConnectionFactory;
import com.iopipe.http.RemoteException;
import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import java.io.Closeable;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * This class provides a single connection to the IOPipe service which may then
 * be used to send multiple requests to as methods are ran. It is permittable
 * for this class to be used a singleton (and recommended for optimization
 * purposes) in which case you can call {@link IOPipeService#instance()} to
 * return a single instance of this class.
 *
 * @since 2017/12/13
 */
public final class IOPipeService
{
	/** This is used to detect cold starts. */
	static final AtomicBoolean _THAWED =
		new AtomicBoolean();
	
	/** The time this class was loaded. */
	static final long _LOAD_TIME =
		IOPipeConstants.LOAD_TIME;
	
	/** The process stat when the process started. */
	static final SystemMeasurement.Times _STAT_START =
		new SystemMeasurement.Times();
	
	/** If an instance was created then this will be that one instance. */
	private static volatile IOPipeService _INSTANCE;
	
	/** The configuration used to connect to the service. */
	protected final IOPipeConfiguration config;
	
	/** The connection to the server. */
	protected final RemoteConnection connection;
	
	/** Is the service enabled and working? */
	protected final boolean enabled;
	
	/** The number of times this context has been executed. */
	private volatile int _execcount;
	
	/** The number of times the server replied with a code other than 2xx. */
	private volatile int _badresultcount;
	
	/**
	 * Initializes the service using the default configuration.
	 *
	 * @since 2017/12/14
	 */
	public IOPipeService()
	{
		this(IOPipeConfiguration.DEFAULT_CONFIG);
	}
	
	/**
	 * Initializes the service using the specified configuration.
	 *
	 * @param __config The configuration to use.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/24
	 */
	public IOPipeService(IOPipeConfiguration __config)
		throws NullPointerException
	{
		if (__config == null)
			throw new NullPointerException();
		
		// Try to open a connection to the IOPipe service, if that fails
		// then fall back to a disabled connection
		RemoteConnection connection = null;
		boolean enabled = false;
		if (__config.isEnabled())
			try
			{
				connection = __config.getRemoteConnectionFactory().connect();
				enabled = true;
			}
			
			// Cannot report error to IOPipe so print to the console
			catch (RemoteException e)
			{
				e.printStackTrace(__config.getFatalErrorStream());
			}
		
		// If the connection failed, use one which does nothing
		if (connection == null)
			connection = new NullConnection();
		
		this.enabled = enabled;
		this.connection = connection;
		this.config = __config;
	}
	
	/**
	 * Returns the configuration which is used.
	 *
	 * @return The used configuration.
	 * @since 2017/12/20
	 */
	public final IOPipeConfiguration config()
	{
		return this.config;
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
	 * Is this service actually enabled?
	 *
	 * @return {@code true} if the service is truly enabled.
	 * @since 2017/12/17
	 */
	public final boolean isEnabled()
	{
		return this.enabled;
	}
	
	/**
	 * Runs the specified function and generates a report.
	 *
	 * @param <R> The value to return.
	 * @param __context The context provided by the AWS service.
	 * @param __func The function to call which will get a generated report.
	 * @return The returned value.
	 * @throws Error If the called function threw an error.
	 * @throws NullPointerException If no function was specified.
	 * @throws RuntimeException If the called function threw an exception.
	 * @since 2017/12/14
	 */
	public final <R> R run(Context __context, Supplier<R> __func)
		throws Error, NullPointerException, RuntimeException
	{
		if (__context == null || __func == null)
			throw new NullPointerException();
		
		int execcount = ++this._execcount;
		
		// If disabled, just run the function
		IOPipeConfiguration config = this.config;
		if (!config.isEnabled())
		{
			this._badresultcount++;
			return __func.get();
		}
		
		PrintStream debug = config.getDebugStream();
		if (debug != null)
			debug.printf("IOPipe: Invoking function %08x%n",
				System.identityHashCode(__context));
		
		// Is this coldstarted?
		boolean coldstarted = !IOPipeService._THAWED.getAndSet(true);
		
		// Register timeout with this execution number so if execution takes
		// longer than expected a timeout is generated
		// Timeouts can be disabled if the timeout window is zero, but they
		// may also be unsupported if the time remaining in the context is zero
		__TimeOutWatchDog__ watchdog = null;
		int windowtime;
		if ((windowtime = config.getTimeOutWindow()) > 0 &&
			__context.getRemainingTimeInMillis() > 0)
			watchdog = new __TimeOutWatchDog__(this, __context,
				Thread.currentThread(), windowtime, coldstarted);
		
		// This method either returns a value or throwsn
		R rv = null;
		Throwable rt = null;
		
		// Keep track of how long execution takes
		long ticker = System.nanoTime();
		boolean timedout = false;
		IOPipeMeasurement measurement = new IOPipeMeasurement(config,
			__context);
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
			ticker = System.nanoTime() - ticker;
			if (watchdog != null)
				watchdog.__finished();
		}
		
		measurement.setDuration(ticker);
		measurement.setColdStart(coldstarted);
		
		// Generate and send result to server
		if (watchdog == null || !watchdog._generated.getAndSet(true))
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
	
	/**
	 * Returns a single instance of the IOPipe service.
	 *
	 * @return The single instance of the service.
	 * @since 2017/12/20
	 */
	public static final IOPipeService instance()
	{
		IOPipeService rv = _INSTANCE;
		if (rv == null)
			_INSTANCE = (rv = new IOPipeService());
		return rv;
	}
}

