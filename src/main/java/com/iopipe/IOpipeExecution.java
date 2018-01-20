package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import com.iopipe.plugin.IOpipePlugin;
import com.iopipe.plugin.IOpipePluginExecution;
import com.iopipe.plugin.NoSuchPluginException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * This class provides access to information and functionality which is
 * specific to a single execution of a method.
 *
 * Each execution will have a unique instance of this object.
 *
 * @since 2018/01/19
 */
public final class IOpipeExecution
{
	/** The service which invoked the method. */
	protected final IOpipeService service;
	
	/** The configuration. */
	protected final IOpipeConfiguration config;
	
	/** The context. */
	protected final Context context;
	
	/** The measurement. */
	protected final IOpipeMeasurement measurement;
	
	/** Plugins which currently have an active exection state. */
	private final Map<Class<? extends IOpipePluginExecution>,
		IOpipePluginExecution> _active =
		new HashMap<>();
	
	/**
	 * Initializes the execution information.
	 *
	 * @param __sv The service which initialized this.
	 * @param __conf The configuration for this service.
	 * @param __context The context for the execution.
	 * @param __m Measurement which is used to provide access to tracing.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/19
	 */
	IOpipeExecution(IOpipeService __sv, IOpipeConfiguration __conf,
		Context __context, IOpipeMeasurement __m)
		throws NullPointerException
	{
		if (__sv == null || __conf == null || __context == null ||
			__m == null)
			throw new NullPointerException();
		
		this.service = __sv;
		this.config = __conf;
		this.context = __context;
		this.measurement = __m;
	}
	
	/**
	 * Returns the configuration used to initialize the service.
	 *
	 * @return The service configuration.
	 * @since 2018/01/19
	 */
	public final IOpipeConfiguration config()
	{
		return this.config;
	}
	
	/**
	 * Returns the AWS context.
	 *
	 * @return The AWS context.
	 * @since 2018/01/19
	 */
	public final Context context()
	{
		return this.context;
	}
	
	/**
	 * Returns the measurement recorder.
	 *
	 * @return The measurement recorder.
	 * @since 2018/01/19
	 */
	public final IOpipeMeasurement measurement()
	{
		return this.measurement;
	}
	
	/**
	 * This returns an instance of a plugin based on the class type of its
	 * interface.
	 *
	 * @param <C> The class type of the execution state.
	 * @param __cl The class object of the execution state.
	 * @return The instance of the plugin's execution state.
	 * @throws ClassCastException If the class type is not valid.
	 * @throws NoSuchPluginException If the plugin does not exist.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/20
	 */
	public final <C extends IOpipePluginExecution> C plugin(Class<C> __cl)
		throws ClassCastException, NoSuchPluginException, NullPointerException
	{
		if (__cl == null)
			throw new NullPointerException();
		
		// Lock due to multiple threads
		Map<Class<? extends IOpipePluginExecution>, IOpipePluginExecution>
			active = this._active;
		synchronized (active)
		{
			// Need to create the plugin if it does not exist
			IOpipePluginExecution rv = active.get(__cl);
			if (rv == null)
			{
				// Was pre-cached to not exist
				if (active.containsKey(__cl))
					throw new NoSuchPluginException(String.format(
						"No plugin exists for %s.", __cl));
				
				// Cache no plugin
				IOpipePlugin plugin = this.service.__plugin(__cl);
				if (plugin == null)
				{
					active.put(__cl, null);
					throw new NoSuchPluginException(String.format(
						"No plugin exists for %s.", __cl));
				}
				
				// Initialize it
				rv = plugin.execute(new WeakReference<>(this));
				if (rv == null)
					throw new NoSuchPluginException(String.format(
						"Could create execution instance for plugin.", __cl));
				active.put(__cl, rv);
			}
			
			return  __cl.cast(rv);
		}
	}
	
	/**
	 * Returns the service which ran this execution.
	 *
	 * @return The service which ran this execution.
	 * @since 2018/01/19
	 */
	public final IOpipeService service()
	{
		return this.service;
	}
}

