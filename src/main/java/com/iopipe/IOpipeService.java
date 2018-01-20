package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import com.iopipe.http.NullConnection;
import com.iopipe.http.RemoteConnection;
import com.iopipe.http.RemoteConnectionFactory;
import com.iopipe.http.RemoteException;
import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import com.iopipe.plugin.IOpipePlugin;
import com.iopipe.plugin.IOpipePluginExecution;
import com.iopipe.plugin.IOpipePluginPostExecutable;
import com.iopipe.plugin.IOpipePluginPreExecutable;
import java.io.Closeable;
import java.io.PrintStream;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
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
 * method {@link IOpipeService#run(Context, Supplier)} may be called.
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
		new SystemMeasurement.Times();
	
	/** If an instance was created then this will be that one instance. */
	private static volatile IOpipeService _INSTANCE;
	
	/** The configuration used to connect to the service. */
	protected final IOpipeConfiguration config;
	
	/** The connection to the server. */
	protected final RemoteConnection connection;
	
	/** Is the service enabled and working? */
	protected final boolean enabled;
	
	/** All plugins which are known to exist and are actually enabled. */
	private final Map<Class<? extends IOpipePluginExecution>,
		IOpipePlugin> _plugins =
		new HashMap<>();
	
	/** Plugins which are pre-exection. */
	private final Set<IOpipePluginPreExecutable> _pluginspre =
		new LinkedHashSet<>();
	
	/** Plugins which are post-exection. */
	private final Set<IOpipePluginPostExecutable> _pluginspost =
		new LinkedHashSet<>();
	
	/** The number of times this context has been executed. */
	private volatile int _execcount;
	
	/** The number of times the server replied with a code other than 2xx. */
	private volatile int _badresultcount;
	
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
				connection = __config.getRemoteConnectionFactory().connect();
				enabled = true;
			}
			
			// Cannot report error to IOpipe so print to the console
			catch (RemoteException e)
			{
				_LOGGER.error("Could not connect to the remote server.", e);
			}
		
		// If the connection failed, use one which does nothing
		if (connection == null)
			connection = new NullConnection();
		
		this.enabled = enabled;
		this.connection = connection;
		this.config = __config;
		
		// Load in plugins and initialize ones which are pre execution and
		// post execution
		Map<Class<? extends IOpipePluginExecution>, IOpipePlugin> plugins =
			this._plugins;
		Set<IOpipePluginPreExecutable> pluginspre = this._pluginspre;
		Set<IOpipePluginPostExecutable> pluginspost = this._pluginspost;
		for (IOpipePlugin p : ServiceLoader.<IOpipePlugin>load(
			IOpipePlugin.class))
		{
			try
			{
				plugins.put(Objects.<Class<? extends IOpipePluginExecution>>
					requireNonNull(p.executionClass()), p);
			}
			catch (NullPointerException e)
			{
				_LOGGER.error("Plugin did not return an execution class.", e);
				continue;
			}
			
			if (p instanceof IOpipePluginPreExecutable)
				pluginspre.add((IOpipePluginPreExecutable)p);
				
			if (p instanceof IOpipePluginPostExecutable)
				pluginspost.add((IOpipePluginPostExecutable)p);
		}
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
		if (__context == null || __func == null)
			throw new NullPointerException();
		
		int execcount = ++this._execcount;
		
		// Setup execution information
		IOpipeMeasurement measurement = new IOpipeMeasurement(config,
			__context, this);
		IOpipeExecution exec = new IOpipeExecution(this, config, __context,
			measurement);
		
		// If disabled, just run the function
		IOpipeConfiguration config = this.config;
		if (!config.isEnabled())
		{
			// Disabled lambdas could still rely on measurements, despite them
			// not doing anything useful at all
			this._badresultcount++;
			return __func.apply(exec);
		}
		
		_LOGGER.debug(() -> String.format("Invoking context %08x",
			System.identityHashCode(__context)));
		
		// Is this coldstarted?
		boolean coldstarted = !IOpipeService._THAWED.getAndSet(true);
		
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
		
		// Run pre-execution plugins
		for (IOpipePluginPreExecutable p : this._pluginspre)
			try
			{
				p.preExecute(exec.<IOpipePluginExecution>plugin(
					IOpipePluginExecution.class));
			}
			catch (RuntimeException e)
			{
				_LOGGER.error("Could not run pre-executable plugin.", e);
			}
		
		// Keep track of how long execution takes
		long ticker = System.nanoTime();
		try
		{
			rv = __func.apply(exec);
		}
		
		// An exception or error was thrown, so that will be reported
		// Error is very fatal, but still report that it occured
		catch (RuntimeException|Error e)
		{
			rt = e;
			
			measurement.__setThrown(e);
		}
		
		// Indicate that execution has finished to the timeout manager
		// so that it no longer reports timeouts
		finally
		{
			ticker = System.nanoTime() - ticker;
			if (watchdog != null)
				watchdog.__finished();
		}
		
		measurement.__setDuration(ticker);
		measurement.__setColdStart(coldstarted);
		
		// Run post-execution plugins
		for (IOpipePluginPostExecutable p : this._pluginspost)
			try
			{
				p.postExecute(exec.<IOpipePluginExecution>plugin(
					IOpipePluginExecution.class));
			}
			catch (RuntimeException e)
			{
				_LOGGER.error("Could not run post-executable plugin.", e);
			}
		
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
	 * Obtains the plugin.
	 *
	 * @param __cl The execution class.
	 * @return The plugin instance or {@code null} if it is not enabled or
	 * does not exist.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/20
	 */
	final IOpipePlugin __plugin(Class<? extends IOpipePluginExecution> __cl)
		throws NullPointerException
	{
		if (__cl == null)
			throw new NullPointerException();
		
		return this._plugins.get(__cl);
	}
	
	/**
	 * Returns all of the available plugins.
	 *
	 * @return All of the plugins.
	 * @since 2018/01/20
	 */
	final IOpipePlugin[] __plugins()
	{
		Collection<IOpipePlugin> plugins = this._plugins.values();
		return plugins.<IOpipePlugin>toArray(new IOpipePlugin[plugins.size()]);
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
			_LOGGER.debug(() -> "Send: " + __r);
			
			RemoteResult result = this.connection.send(__r);
			
			// Report what was received
			_LOGGER.debug(() -> "Recv (" + result.code() + "): " +
				result.body());
			
			// Only the 200 range is valid for okay responses
			if ((result.code() / 100) != 2)
				this._badresultcount++;
			
			return result;
		}
		
		// Failed to write to the server
		catch (RemoteException e)
		{
			_LOGGER.error("Could not sent request to server.", e);
			
			this._badresultcount++;
			return new RemoteResult(503, "");
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
}

