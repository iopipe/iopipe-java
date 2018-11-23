package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
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
import com.iopipe.plugin.NoSuchPluginException;
import java.io.Closeable;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import org.pmw.tinylog.Logger;

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
	/** This is used to detect cold starts. */
	static final AtomicBoolean _THAWED =
		new AtomicBoolean();
	
	/** The time this class was loaded. */
	static final long _LOAD_TIME =
		IOpipeConstants.LOAD_TIME;
	
	/** The process stat when the process started. */
	static final SystemMeasurement.Times _STAT_START =
		SystemMeasurement.measureTimes(SystemMeasurement.SELF_PROCESS);
	
	/** Stores the execution for the current thread, inherited by child threads. */
	private static final ThreadLocal<Reference<IOpipeExecution>> _EXECUTIONS =
		new InheritableThreadLocal<>();
	
	/** Reference to the last execution that has occurred, just in case. */
	private static final AtomicReference<Reference<IOpipeExecution>> _LAST =
		new AtomicReference<>();
	
	/** If an instance was created then this will be that one instance. */
	private static volatile IOpipeService _INSTANCE;
	
	/** The configuration used to connect to the service. */
	protected final IOpipeConfiguration config;
	
	/** Is the service enabled and working? */
	protected final boolean enabled;
	
	/** The coldstart flag indicator to use. */
	private final AtomicBoolean _coldstartflag;
	
	/** The sender where requests go. */
	final __RequestSender__ _rsender;
	
	/** The manager for timeouts. */
	final __TimeOutTracker__ _timeout;
	
	/** Plugin state. */
	final __Plugins__ _plugins;
	
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
				Logger.error(e, "Could not connect to the remote server.");
			}
		
		// If the connection failed, use one which does nothing
		if (!enabled || connection == null)
			connection = new NullConnection();
		
		// This class manages sending all our requests
		__RequestSender__ rsender;
		this._rsender = (rsender = new __RequestSender__(connection));
		
		// Setup timeout tracker
		this._timeout = new __TimeOutTracker__(rsender,
			__config.getTimeOutWindow());
		
		// Store config and such
		this.enabled = enabled;
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
	 * @param <I> The input type.
	 * @param <O> The output type.
	 * @param __context The context provided by the AWS service.
	 * @param __func The lambda function to execute, measure, and generate a
	 * report for.
	 * @param __input The input value for the lambda.
	 * @return The result of the function.
	 * @throws Error If the called function threw an error.
	 * @throws NullPointerException If no function was specified.
	 * @throws RuntimeException If the called function threw an exception.
	 * @since 2018/08/09
	 */
	public final <I, O> O run(Context __context, RequestHandler<I, O> __func,
		I __input)
		throws Error, NullPointerException, RuntimeException
	{
		if (__func == null)
			throw new NullPointerException();
		
		// Use the context derived from the execution in the event that it is
		// changed
		return this.<O>run(__context, (__exec) -> __func.handleRequest(
			__input, __exec.context()), __input);
	}
	
	/**
	 * Runs the specified function and generates a report.
	 *
	 * @param __context The context provided by the AWS service.
	 * @param __func The lambda function to execute, measure, and generate a
	 * report for.
	 * @param __in The input stream for data.
	 * @param __out The output stream for data.
	 * @throws Error If the called function threw an error.
	 * @throws IOException On read/write errors.
	 * @throws NullPointerException If no function was specified.
	 * @throws RuntimeException If the called function threw an exception.
	 * @since 2018/08/09
	 */
	public final void run(Context __context, RequestStreamHandler __func,
		InputStream __in, OutputStream __out)
		throws Error, IOException, NullPointerException, RuntimeException
	{
		if (__func == null)
			throw new NullPointerException();
		
		// Use the context derived from the execution in the event that it is
		// changed
		try
		{
			this.<Object>run(__context, (__exec) ->
				{
					try
					{
						__func.handleRequest(__in, __out, __exec.context());
					}
					catch (IOException e)
					{
						throw new IOpipeWrappedException(
							e.getMessage(), e);
					}
					
					return null;
				}, __in);
		}
		
		// Forward IOExceptions
		catch (IOpipeWrappedException e)
		{
			throw (IOException)e.getCause();
		}
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
	 * @param __context The context provided by the AWS service, if one is
	 * not provided then one will be generated.
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
		if (__func == null)
			throw new NullPointerException();
		
		// If no context is specified, generate one
		if (__context == null)
			__context = new __PseudoContext__(__input);
		
		// If an execution is already running, just ignore wrapping and
		// generating events and just call it directly
		{
			IOpipeExecution exec = IOpipeService.__execution();
			if (exec != null)
				return __func.apply(exec);
		}
		
		// Earliest start time for method entry
		long nowtime = System.currentTimeMillis(),
			nowmono = System.nanoTime();
		
		// Is this enabled?
		boolean enabled = config.isEnabled();
				
		// Is this coldstarted?
		boolean coldstarted = !this._coldstartflag.getAndSet(true);
		
		// Setup execution information
		__Plugins__ plugins = this._plugins;
		__Plugins__.__Info__[] pinfos = plugins.__info();
		__ActiveExecution__ exec = new __ActiveExecution__(this, config,
			__context, nowtime, __input, nowmono, coldstarted, plugins);
		
		// Use a reference to allow the execution to be garbage collected if
		// it is no longer referred to or is in the stack of any method.
		// Otherwise execution references will just sit around in memory and
		// might not get freed ever.
		ThreadLocal<Reference<IOpipeExecution>> executions = _EXECUTIONS;
		Reference<IOpipeExecution> refexec = new WeakReference<>(exec);
		executions.set(refexec);
		
		// Just in case there was no way to get the current execution in the
		// event that the thread local could not be obtained
		AtomicReference<Reference<IOpipeExecution>> lastexec = _LAST;
		lastexec.compareAndSet(null, refexec);
		
		// If disabled, just run the function
		IOpipeConfiguration config = this.config;
		if (!enabled)
		{
			// Disabled lambdas could still rely on measurements, despite them
			// not doing anything useful at all
			try
			{
				return __func.apply(exec);
			}
			finally
			{
				// Clear the last execution because it is no longer occuring
				executions.set(null);
				lastexec.compareAndSet(refexec, null);
			}
		}
		
		// Keep track of this execution and make sure that timeouts trigger
		// if they occur, the atomic is so that only a single event is sent
		AtomicBoolean execsent = new AtomicBoolean();
		this._timeout.__track(__context, exec, execsent,
			Thread.currentThread());
		
		// Add auto-label for coldstart
		if (coldstarted)
			exec.label("@iopipe/coldstart");
		
		// Run pre-execution plugins
		for (__Plugins__.__Info__ i : pinfos)
			if (i.isEnabled())
				try
				{
					IOpipePluginPreExecutable l = i.getPreExecutable();
					if (l != null)
						l.preExecute(exec.plugin(i.executionClass()));
				}
				catch (RuntimeException|NoSuchPluginException e)
				{
					Logger.error(e, "Could not run pre-executable plugin {}.",
						i);
				}
		
		// Run the function
		R value = null;
		Throwable exception = null;
		try
		{
			value = __func.apply(exec);
		}
		
		// An exception or error was thrown, so that will be reported
		// Error is very fatal, but still report that it occured
		catch (RuntimeException|Error e)
		{
			exception = e;
			
			if (exec instanceof __ActiveExecution__)
				((__ActiveExecution__)exec).__setThrown(e);
			exec.label("@iopipe/error");
		}
		
		// Run post-execution plugins
		for (__Plugins__.__Info__ i : pinfos)
			if (i.isEnabled())
				try
				{
					IOpipePluginPostExecutable l = i.getPostExecutable();
					if (l != null)
						l.postExecute(exec.plugin(i.executionClass()));
				}
				catch (RuntimeException|NoSuchPluginException e)
				{
					Logger.error(e, "Could not run post-executable plugin {}.",
						i);
				}
		
		// Only send the request if the watchdog did not
		if (execsent.compareAndSet(false, true))
			if (exec instanceof __ActiveExecution__)
				this._rsender.__send(((__ActiveExecution__)exec).__buildRequest());
		
		// Clear the last execution that is occuring, but only if ours was
		// still associated with it
		executions.set(null);
		lastexec.compareAndSet(refexec, null);
		
		// Throw the called exception as if the wrapper did not have any
		// trouble
		if (exception != null)
			if (exception instanceof Error)
				throw (Error)exception;
			else
				throw (RuntimeException)exception;
		return value;
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
			Logger.debug("Initializing new service instance.");
			
			_INSTANCE = (rv = new IOpipeService());
		}
		return rv;
	}
	
	/**
	 * Returns the current execution of the current thread.
	 *
	 * @return The current execution or {@code null} if it could not obtained.
	 * @since 2018/07/30
	 */
	static final IOpipeExecution __execution()
	{
		Reference<IOpipeExecution> ref = _EXECUTIONS.get();
		IOpipeExecution rv;
		
		// If there is no thread local then use the last instance
		if (ref == null || null == (rv = ref.get()))
		{
			ref = _LAST.get();
			
			// No last execution exists either
			if (ref == null || null == (rv = ref.get()))
				return null;
		}
		
		// There was a thread local or last execution
		return rv;
	}
}

