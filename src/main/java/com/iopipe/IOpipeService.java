package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import com.iopipe.http.NullConnection;
import com.iopipe.http.RemoteBody;
import com.iopipe.http.RemoteConnection;
import com.iopipe.http.RemoteConnectionFactory;
import com.iopipe.http.RemoteException;
import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import com.iopipe.http.RequestType;
import com.iopipe.plugin.IOpipePlugin;
import com.iopipe.plugin.IOpipePluginExecution;
import com.iopipe.plugin.IOpipePluginPostExecutable;
import com.iopipe.plugin.IOpipePluginPreExecutable;
import java.io.Closeable;
import java.io.PrintStream;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This class provides a single connection to the IOpipe service which may then
 * be used to send multiple requests to as methods are ran. It is permittable
 * for this class to be used a singleton (and recommended for optimization
 * purposes) in which case you can call {@link IOpipeService#instance()} to
 * return a single instance of this class.
 *
 * It is recommended that code use the instance provided by
 * {@link IOpipeService#instance()}, then once an instance is obtained the
 * method {@link IOpipeService#run(Context, Function)} may be called.
 *
 * @since 2017/12/13
 */
public final class IOpipeService
{
	/** Logging. */
	private static final Logger _LOGGER =
		LogManager.getLogger(IOpipeService.class);
	
	/** This is used to detect cold starts. */
	static final AtomicBoolean _THAWED =
		new AtomicBoolean();
	
	/** The time this class was loaded. */
	static final long _LOAD_TIME =
		IOpipeConstants.LOAD_TIME;
	
	/** The process stat when the process started. */
	static final SystemMeasurement.Times _STAT_START =
		SystemMeasurement.measureTimes(SystemMeasurement.SELF_PROCESS);
	
	/** If an instance was created then this will be that one instance. */
	private static volatile IOpipeService _INSTANCE;
	
	/** The configuration used to connect to the service. */
	protected final IOpipeConfiguration config;
	
	/** The connection to the server. */
	protected final RemoteConnection connection;
	
	/** Is the service enabled and working? */
	protected final boolean enabled;
	
	/** The coldstart flag indicator to use. */
	private final AtomicBoolean _coldstartflag;
	
	/** Plugin state. */
	final __Plugins__ _plugins;
	
	/** The number of times this context has been executed. */
	private final AtomicInteger _execcount =
		new AtomicInteger();
	
	/** The number of times the server replied with a code other than 2xx. */
	private final AtomicInteger _badresultcount =
		new AtomicInteger();
	
	/**
	 * Initializes the service using the default configuration.
	 *
	 * @since 2017/12/14
	 */
	public IOpipeService()
	{
		this(IOpipeConfiguration.DEFAULT_CONFIG);
	}
	
	/**
	 * Initializes the service using the specified configuration.
	 *
	 * @param __config The configuration to use.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/24
	 */
	public IOpipeService(IOpipeConfiguration __config)
		throws NullPointerException
	{
		if (__config == null)
			throw new NullPointerException();
		
		// Try to open a connection to the IOpipe service, if that fails
		// then fall back to a disabled connection
		RemoteConnection connection = null;
		boolean enabled = false;
		if (__config.isEnabled())
			try
			{
				connection = __config.getRemoteConnectionFactory().connect(
					__config.getServiceUrl(), __config.getProjectToken());
				enabled = true;
			}
			
			// Cannot report error to IOpipe so print to the console
			catch (RemoteException e)
			{
				_LOGGER.error("Could not connect to the remote server.", e);
			}
		
		// If the connection failed, use one which does nothing
		if (!enabled || connection == null)
			connection = new NullConnection();
		
		this.enabled = enabled;
		this.connection = connection;
		this.config = __config;
		
		// Detect all available plugins
		this._plugins = new __Plugins__(enabled, __config);
		
		// Cold starts can either use the default global instance or they
		// can use a per-instance indicator. This is mostly used for testing.
		this._coldstartflag = (__config.getUseLocalColdStart() ?
			new AtomicBoolean() : IOpipeService._THAWED);
	}
	
	/**
	 * Returns the configuration which is used.
	 *
	 * @return The used configuration.
	 * @since 2017/12/20
	 */
	public final IOpipeConfiguration config()
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
		return this._badresultcount.get();
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
	 * @param __func The lambda function to execute, measure, and generate a
	 * report for.
	 * @return The returned value.
	 * @throws Error If the called function threw an error.
	 * @throws NullPointerException If no function was specified.
	 * @throws RuntimeException If the called function threw an exception.
	 * @since 2017/12/14
	 */
	public final <R> R run(Context __context,
		Function<IOpipeExecution, R> __func)
		throws Error, NullPointerException, RuntimeException
	{
		return this.<R>run(__context, __func, null);
	}
	
	/**
	 * Runs the specified function and generates a report.
	 *
	 * @param <R> The value to return.
	 * @param __context The context provided by the AWS service.
	 * @param __func The lambda function to execute, measure, and generate a
	 * report for.
	 * @param __input An object which should specify the object which was
	 * input for the executed method, may be {@code null}.
	 * @return The returned value.
	 * @throws Error If the called function threw an error.
	 * @throws NullPointerException If no function was specified.
	 * @throws RuntimeException If the called function threw an exception.
	 * @since 2018/05/16
	 */
	public final <R> R run(Context __context,
		Function<IOpipeExecution, R> __func, Object __input)
		throws Error, NullPointerException, RuntimeException
	{
		if (__context == null || __func == null)
			throw new NullPointerException();
		
		int execcount = this._execcount.incrementAndGet();
		
		// Create thread group so it is known which threads are part of this
		// execution and not other executions
		boolean enabled = config.isEnabled();
		ThreadGroup threadgroup = (!enabled ?
			Thread.currentThread().getThreadGroup() :
			new ThreadGroup(String.format("IOpipe-execution-%08d",
				System.identityHashCode(__context))));
				
		// Is this coldstarted?
		boolean coldstarted = !this._coldstartflag.getAndSet(true);
		
		// Setup execution information
		long nowtime = System.currentTimeMillis();
		IOpipeMeasurement measurement = new IOpipeMeasurement(coldstarted);
		IOpipeExecution exec = new IOpipeExecution(this, config, __context,
			measurement, threadgroup, nowtime, __input);
		
		// If disabled, just run the function
		IOpipeConfiguration config = this.config;
		if (!enabled)
		{
			// Disabled lambdas could still rely on measurements, despite them
			// not doing anything useful at all
			this._badresultcount.incrementAndGet();
			return __func.apply(exec);
		}
		
		_LOGGER.debug(() -> String.format("Invoking context %08x",
			System.identityHashCode(__context)));
		
		// Add auto-label for coldstart
		if (coldstarted)
			exec.label("@iopipe/coldstart");
		
		// Run pre-execution plugins
		__Plugins__.__Info__[] plugins = this._plugins.__info();
		for (__Plugins__.__Info__ i : plugins)
		{
			IOpipePluginPreExecutable l = i.getPreExecutable();
			if (l != null)
				try
				{
					exec.plugin(i.executionClass(), l::preExecute);
				}
				catch (RuntimeException e)
				{
					_LOGGER.error("Could not run pre-executable plugin.", e);
				}
		}
		
		// Run the function in another thread so that it becomes part of the
		// given group, this is needed by the profiler plugin
		__Runner__<R> runner = new __Runner__<R>(exec, __func);
		Thread runnerthread = new Thread(threadgroup, runner, "main");
		
		// Register timeout with this execution number so if execution takes
		// longer than expected a timeout is generated
		// Timeouts can be disabled if the timeout window is zero, but they
		// may also be unsupported if the time remaining in the context is zero
		__TimeOutWatchDog__ watchdog = null;
		int windowtime;
		if ((windowtime = config.getTimeOutWindow()) > 0 &&
			__context.getRemainingTimeInMillis() > 0)
			watchdog = new __TimeOutWatchDog__(this, __context,
				runnerthread, windowtime, coldstarted, exec);
		
		// Start the thread and wait until it dies
		runnerthread.start();
		for (;;)
			try
			{
				runnerthread.join();
				break;
			}
			catch (InterruptedException e)
			{
				// Ignore
			}
		
		// It died, so stop the watchdog
		if (watchdog != null)
			watchdog.__finished();
		
		// Run post-execution plugins
		for (__Plugins__.__Info__ i : plugins)
		{
			IOpipePluginPostExecutable l = i.getPostExecutable();
			if (l != null)
				try
				{
					exec.plugin(i.executionClass(), l::postExecute);
				}
				catch (RuntimeException e)
				{
					_LOGGER.error("Could not run post-executable plugin.", e);
				}
		}
		
		// Generate and send result to server
		if (watchdog == null || !watchdog._generated.getAndSet(true))
			this.__sendRequest(exec.__buildRequest());
		
		// Throw the called exception as if the wrapper did not have any
		// trouble
		__Result__<R> result = runner._result;
		Throwable rt = result.thrown;
		if (rt != null)
			if (rt instanceof Error)
				throw (Error)rt;
			else
				throw (RuntimeException)rt;
		return result.value;
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
			_LOGGER.debug(() -> "Send: " + __r + " " + __debugBody(__r));
			
			RemoteResult result = this.connection.send(RequestType.POST, __r);
			
			// Only the 200 range is valid for okay responses
			int code = result.code();
			if (!(code >= 200 && code < 300))
			{
				this._badresultcount.incrementAndGet();
				
				// Emit errors for failed requests
				_LOGGER.error(() -> "Recv: " + result + " " +
					__debugBody(result));
			}
			
			// Debug log successful requests
			else
				_LOGGER.debug(() -> "Recv: " + result + " " +
					__debugBody(result));
			
			return result;
		}
		
		// Failed to write to the server
		catch (RemoteException e)
		{
			_LOGGER.error("Could not sent request to server.", e);
			
			this._badresultcount.incrementAndGet();
			return new RemoteResult(503, RemoteBody.MIMETYPE_JSON, "");
		}
	}
	
	/**
	 * Returns a single instance of the IOpipe service.
	 *
	 * @return The single instance of the service.
	 * @since 2017/12/20
	 */
	public static final IOpipeService instance()
	{
		IOpipeService rv = _INSTANCE;
		if (rv == null)
		{
			_LOGGER.debug("Initializing new service instance.");
			
			_INSTANCE = (rv = new IOpipeService());
		}
		return rv;
	}
	
	/**
	 * Shows string representation of the body.
	 *
	 * @param __b The body to decode.
	 * @return The string result.
	 * @since 2018/02/24
	 */
	private static final String __debugBody(RemoteBody __b)
	{
		try
		{
			String rv = __b.bodyAsString();
			if (rv.indexOf('\0') >= 0)
				return "BINARY DATA";
			return rv;
		}
		catch (Throwable t)
		{
			return "Could not decode!";
		}
	}
	
	/**
	 * The result of an execution.
	 *
	 * @param <R> The type of value to return.
	 * @since 2018/07/27
	 */
	private static final class __Result__<R>
	{
		/** Exception that was thrown. */
		public final Throwable thrown;
		
		/** The return value. */
		public final R value;
		
		/**
		 * Initializes the result.
		 *
		 * @param __v The value.
		 * @since 2018/07/27
		 */
		__Result__(R __v)
		{
			this.thrown = null;
			this.value = __v;
		}
		
		/**
		 * Initializes the exception.
		 *
		 * @param __t The thrown exception
		 * @since 2018/07/27
		 */
		__Result__(Throwable __t)
		{
			this.thrown = __t;
			this.value = null;
		}
	}
	
	/**
	 * Runs the thread and logs execution time and any exceptions.
	 *
	 * @param <R> The type of value to return.
	 * @since 2018/02/09
	 */
	private static final class __Runner__<R>
		implements Runnable
	{
		/** The execution state. */
		protected final IOpipeExecution execution;
		
		/** The function to execute. */
		protected final Function<IOpipeExecution, R> function;
		
		/** The result of the call. */
		volatile __Result__<R> _result;
		
		/**
		 * Initializes the runner.
		 *
		 * @param __exec The execution.
		 * @param __func The function to invoke.
		 * @throws NullPointerException On null arguments.
		 * @since 2018/02/09
		 */
		__Runner__(IOpipeExecution __exec, Function<IOpipeExecution, R> __func)
			throws NullPointerException
		{
			if (__exec == null || __func == null)
				throw new NullPointerException();
			
			this.execution = __exec;
			this.function = __func;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2018/02/09
		 */
		@Override
		public void run()
		{
			IOpipeExecution exec = this.execution;
			IOpipeMeasurement measurement = exec.measurement();
			
			// Keep track of execution time
			long ticker = System.nanoTime();
			__Result__ result = null;
			try
			{
				result = new __Result__(this.function.apply(exec));
			}
			
			// An exception or error was thrown, so that will be reported
			// Error is very fatal, but still report that it occured
			catch (RuntimeException|Error e)
			{
				result = new __Result__(e);
				
				measurement.__setThrown(e);
				measurement.addLabel("@iopipe/error");
			}
			
			// Count how long execution has taken
			finally
			{
				measurement.__setDuration(System.nanoTime() - ticker);
			}
			
			// Store the result
			this._result = result;
		}
	}
}

