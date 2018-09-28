package com.iopipe;

import com.iopipe.plugin.eventinfo.EventInfoPlugin;
import com.iopipe.plugin.IOpipePlugin;
import com.iopipe.plugin.IOpipePluginExecution;
import com.iopipe.plugin.IOpipePluginPostExecutable;
import com.iopipe.plugin.IOpipePluginPreExecutable;
import com.iopipe.plugin.logger.LoggerPlugin;
import com.iopipe.plugin.profiler.ProfilerPlugin;
import com.iopipe.plugin.trace.TracePlugin;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import org.pmw.tinylog.Logger;

/**
 * This represents plugins which are available to the service with the
 * potential of being enabled or disabled.
 *
 * @since 2018/01/30
 */
final class __Plugins__
{
	/** Plugin information per execution class. */
	private final Map<Class<? extends IOpipePluginExecution>, __Info__> _info =
		new LinkedHashMap<>();
	
	/**
	 * Searches for and initializes the state of plugins.
	 *
	 * @param __enable Global service enabled state.
	 * @param __conf The configuration for the service.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/30
	 */
	__Plugins__(boolean __enable, IOpipeConfiguration __conf)
		throws NullPointerException
	{
		if (__conf == null)
			throw new NullPointerException();
		
		// Load plugins from services
		Map<Class<? extends IOpipePluginExecution>, __Info__> info =
			this._info;
		for (IOpipePlugin p : __Plugins__.__searchPlugins())
			try
			{
				__Info__ i = new __Info__(__enable, p, __conf);
				
				Class<? extends IOpipePluginExecution> xcl =
					i.executionClass();
				if (!info.containsKey(xcl))
					info.put(xcl, i);
			}
			
			// Do not let plugin initailization fail
			catch (RuntimeException e)
			{
				Logger.error(e, "Failed to initialize plugin {}.",
					p.getClass().getName());
			}
	}
	
	/**
	 * Gets the information for the given execution state type.
	 *
	 * @param __c The execution state class to get the info for.
	 * @return The information for the given plugin or {@code null} if it does
	 * not exist.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/30
	 */
	final __Info__ __get(Class<? extends IOpipePluginExecution> __c)
		throws NullPointerException
	{
		if (__c == null)
			throw new NullPointerException();
		
		return this._info.get(__c);
	}
	
	/**
	 * Returns the plugin information.
	 *
	 * @return The plugin information.
	 * @since 2018/01/30
	 */
	final __Info__[] __info()
	{
		Collection<__Info__> rv = this._info.values();
		return rv.<__Info__>toArray(new __Info__[rv.size()]);
	}
	
	/**
	 * This searches for plugins which are available being built-in and
	 * made available by the service load.
	 *
	 * @return An iterable over plugins used.
	 * @since 2018/02/28
	 */
	private static final Iterable<IOpipePlugin> __searchPlugins()
	{
		Collection<IOpipePlugin> rv = new LinkedList<>();
		
		// Provide some built-in plugins so that way if the service loader
		// has issues or a user failed to merge the service files correctly
		// then this will ensure that these plugins are available no matter
		// what happens
		rv.add(new TracePlugin());
		rv.add(new ProfilerPlugin());
		rv.add(new EventInfoPlugin());
		rv.add(new LoggerPlugin());
		
		// Use plugins provided by the service loader
		try
		{
			for (IOpipePlugin p : ServiceLoader.<IOpipePlugin>load(
				IOpipePlugin.class))
				rv.add(p);
		}
		
		// There is a bad service configuration
		catch (ServiceConfigurationError e)
		{
			Logger.error(e, "There is a service configuration error, this " +
				"means that most and usually all plugins will be disabled." +
				"The usual cause of this is META-INF/services which is" +
				"missing a class or that class fails to load.");
		}
		
		return rv;
	}
	
	/**
	 * Represents the information for a single plugin.
	 *
	 * @since 2018/01/30
	 */
	static final class __Info__
	{
		/** The plugin reference. */
		protected final IOpipePlugin plugin;
		
		/** The name. */
		protected final String name;
		
		/** The version. */
		protected final String version;
		
		/** The homepage. */
		protected final String homepage;
		
		/** Is this plugin enabled? */
		protected final boolean enabled;
		
		/** The execution state class of the plugin. */
		protected final Class<? extends IOpipePluginExecution> executionclass;
		
		/** Is this pre-executable? */
		protected final boolean preexecutable;
		
		/** Is this post-executable. */
		protected final boolean postexecutable;
		
		/** String representation. */
		private Reference<String> _string;
		
		/**
		 * Initializes the plugin.
		 *
		 * @param __e Global enabled state.
		 * @param __p The plugin interface.
		 * @param __conf The service configuration.
		 * @throws NullPointerException On null arguments.
		 * @since 2018/01/30
		 */
		private __Info__(boolean __ge, IOpipePlugin __p,
			IOpipeConfiguration __conf)
			throws NullPointerException
		{
			if (__p == null || __conf == null)
				throw new NullPointerException();
			
			this.plugin = __p;
			
			// If no name was specified, always fallback
			String name = Objects.toString(__p.name(),
				__p.getClass().getName());
			this.name = name;
			
			// These are optional
			this.version = __p.version();
			this.homepage = __p.homepage();
			
			// Is this plugin enabled?
			this.enabled = __ge &&
				__conf.isPluginEnabled(name, __p.enabledByDefault());
			
			// This is required and is used to store the state of a plugin for
			// a single execution
			this.executionclass = Objects.
				<Class<? extends IOpipePluginExecution>>requireNonNull(
				__p.executionClass(), "Plugin has no execution state type.");
			
			// Are these pre/post executable?
			this.preexecutable = (__p instanceof IOpipePluginPreExecutable);
			this.postexecutable = (__p instanceof IOpipePluginPostExecutable);
		}
		
		/**
		 * Returns the execution type of the plugin.
		 *
		 * @return The plugin execution type.
		 * @since 2018/01/30
		 */
		public final Class<? extends IOpipePluginExecution> executionClass()
		{
			return this.executionclass;
		}
		
		/**
		 * Returns the post-executable instance if this is one.
		 *
		 * @return The post executable or {@code null} if this is not one.
		 * @since 2018/01/30
		 */
		public final IOpipePluginPostExecutable getPostExecutable()
		{
			if (this.enabled && this.postexecutable)
				return (IOpipePluginPostExecutable)this.plugin;
			return null;
		}
		
		/**
		 * Returns the pre-executable instance if this is one.
		 *
		 * @return The pre executable or {@code null} if this is not one.
		 * @since 2018/01/30
		 */
		public final IOpipePluginPreExecutable getPreExecutable()
		{
			if (this.enabled && this.preexecutable)
				return (IOpipePluginPreExecutable)this.plugin;
			return null;
		}
		
		/**
		 * Returns the plugin homepage.
		 *
		 * @return The plugin homepage.
		 * @since 2018/01/30
		 */
		public final String homepage()
		{
			return this.homepage;
		}
		
		/**
		 * Is this plugin enabled?
		 *
		 * @return {@code true} if the plugin is enabled.
		 * @since 2018/01/30
		 */
		public final boolean isEnabled()
		{
			return this.enabled;
		}
		
		/**
		 * Returns the plugin name.
		 *
		 * @return The plugin name.
		 * @since 2018/01/30
		 */
		public final String name()
		{
			return this.name;
		}
		
		/**
		 * Returns the plugin class.
		 *
		 * @return The plugin class.
		 * @since 2018/01/30
		 */
		public final IOpipePlugin plugin()
		{
			return this.plugin;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2018/07/30
		 */
		@Override
		public final String toString()
		{
			Reference<String> ref = this._string;
			String rv;
			
			if (ref == null || null == (rv = ref.get()))
				this._string = new WeakReference<>(
					(rv = this.name + "@" + this.version));
			
			return rv;
		}
		
		/**
		 * Returns the plugin version.
		 *
		 * @return The plugin version.
		 * @since 2018/01/30
		 */
		public final String version()
		{
			return this.version;
		}
	}
}

