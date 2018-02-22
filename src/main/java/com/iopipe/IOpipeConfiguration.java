package com.iopipe;

import com.iopipe.http.NullConnectionFactory;
import com.iopipe.http.RemoteConnectionFactory;
import com.iopipe.http.ServiceConnectionFactory;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.TreeMap;
import okhttp3.HttpUrl;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This class contains the configuration for IOpipe and specifies the settings
 * which are to be used when the server is contacted.
 *
 * This class is mutable.
 *
 * @since 2017/12/12
 */
public final class IOpipeConfiguration
{
	/** Used for logging. */
	private static final Logger _LOGGER =
		LogManager.getLogger(IOpipeConfiguration.class);
	
	/** The prefix for plugin enabled in system properties. */
	private static final String _PROPERTY_PLUGIN_PREFIX =
		"com.iopipe.plugin.";
	
	/** Environment variable prefix for plugin state. */
	private static final String _ENVIRONMENT_PLUGIN_PREFIX =
		"IOPIPE_";
	
	/** Environment variable suffix for plugin state. */
	private static final String _ENVIRONMENT_PLUGIN_SUFFIX =
		"_ENABLE";
	
	/** The disabled configuration. */
	public static final IOpipeConfiguration DISABLED_CONFIG;
	
	/** Default configuration to use. */
	public static final IOpipeConfiguration DEFAULT_CONFIG;
	
	/** Should the service be enabled? */
	protected final boolean enabled;
	
	/** The project token to gather statistics for. */
	protected final String token;
	
	/** The factory used to initialize new HTTP connections. */
	protected final RemoteConnectionFactory connectionfactory;
	
	/** The timeout window in milliseconds. */
	protected final int timeoutwindow;
	
	/** Install method. */
	protected final String installmethod;
	
	/** The URL to the profiler. */
	protected final String profilerurl;
	
	/** The state of plugins. */
	private final Map<String, Boolean> _pluginstate =
		new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	
	/** String representation. */
	private volatile Reference<String> _string;
	
	/**
	 * Initializes the default configuration.
	 *
	 * @since 2017/12/19
	 */
	static
	{
		// Initialize disabled configuration
		IOpipeConfigurationBuilder cb = new IOpipeConfigurationBuilder();
		
		cb.setEnabled(false);
		cb.setProjectToken("Disabled");
		cb.setInstallMethod("Disabled");
		cb.setRemoteConnectionFactory(new NullConnectionFactory());
		cb.setTimeOutWindow(0);
		cb.setProfilerUrl(null);
		
		DISABLED_CONFIG = cb.build();
		
		// Try to initialize a default configuration, if the configuration
		// is not valid due to missing values then use the disabled one
		IOpipeConfiguration use = DISABLED_CONFIG;
		try
		{
			_LOGGER.debug("Initializing default configuration.");
			use = IOpipeConfiguration.byDefault();
		}
		catch (IllegalArgumentException e)
		{
			_LOGGER.error("Failed to initialize default configuration, " +
				"your method will still run however it will not report " +
				"anything to IOpipe.", e);
		}
		DEFAULT_CONFIG = use;
		
		// Debug the default config
		_LOGGER.debug("Default config: {}", use);
	}
	
	/**
	 * Initializes the configuration from the specified builder.
	 *
	 * @param __builder The builder to initialize from.
	 * @throws IllegalArgumentException If the input parameters are not
	 * correct.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/13
	 */
	IOpipeConfiguration(IOpipeConfigurationBuilder __builder)
		throws IllegalArgumentException, NullPointerException
	{
		if (__builder == null)
			throw new NullPointerException();
		
		boolean enabled = __builder._enabled;
		String token = __builder._token;
		RemoteConnectionFactory connectionfactory =
			__builder._connectionfactory;
		int timeoutwindow = __builder._timeoutwindow;
		String installmethod = __builder._installmethod;
		
		if (token == null)
			throw new IllegalArgumentException("A project token must be " +
				"specified.");
		
		if (connectionfactory == null)
			throw new IllegalArgumentException("No connection factory " +
				"was specified.");
		
		if (timeoutwindow < 0)
			throw new IllegalArgumentException("The timeout window cannot " +
				"be negative.");
		
		this.enabled = enabled;
		this.token = token;
		this.connectionfactory = connectionfactory;
		this.timeoutwindow = timeoutwindow;
		this.installmethod = installmethod;
		
		// This may be null
		this.profilerurl = __builder._profilerurl;
		
		this._pluginstate.putAll(__builder._pluginstate);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/12
	 */
	@Override
	public final boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		if (!(__o instanceof IOpipeConfiguration))
			return false;
		
		IOpipeConfiguration o = (IOpipeConfiguration)__o;
		return this.enabled == o.enabled &&
			Objects.equals(this.token, o.token) &&
			Objects.equals(this.connectionfactory, o.connectionfactory) &&
			this.timeoutwindow == o.timeoutwindow &&
			Objects.equals(this.installmethod, o.installmethod) &&
			this._pluginstate.equals(o._pluginstate);
	}
	
	/**
	 * Returns the factory which is used to connect to the IOpipe service.
	 *
	 * @return The factory used to connect to the IOpipe service.
	 * @since 2017/12/13
	 */
	public final RemoteConnectionFactory getRemoteConnectionFactory()
	{
		return this.connectionfactory;
	}
	
	/**
	 * Returns the install method.
	 *
	 * @return The install method.
	 * @since 2017/12/13
	 */
	public final String getInstallMethod()
	{
		return this.installmethod;
	}
	
	/**
	 * Returns the URL to use for the profiler.
	 *
	 * @return The profiler URL.
	 * @since 2018/02/22
	 */
	public final String getProfilerUrl()
	{
		return this.profilerurl;
	}
	
	/**
	 * Returns the token for the project to write statistics for.
	 *
	 * @return The project's token.
	 * @since 2017/12/13
	 */
	public final String getProjectToken()
	{
		return this.token;
	}
	
	/**
	 * Returns the timeout window in milliseconds.
	 *
	 * @return The timeout window in milliseconds, if the return value is
	 * zero the timeouts will not be reported.
	 * @since 2017/12/13
	 */
	public final int getTimeOutWindow()
	{
		return this.timeoutwindow;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/12
	 */
	@Override
	public final int hashCode()
	{
		return Boolean.hashCode(this.enabled) ^
			Objects.hashCode(this.token) ^
			Objects.hashCode(this.connectionfactory) ^
			this.timeoutwindow ^
			Objects.hashCode(this.installmethod) ^
			this._pluginstate.hashCode();
	}
	
	/**
	 * Returns {@code true} if IOpipe logging is to be enabled, this allows
	 * the service to be disabled for testing.
	 *
	 * @return {@code true} if logging is enabled.
	 * @since 2017/12/13
	 */
	public final boolean isEnabled()
	{
		return this.enabled;
	}
	
	/**
	 * Returns whether the specified plugin is enabled or not. If it is
	 * unspecified in the configuration then the specified default value is
	 * used instead.
	 *
	 * @param __n The plugin to get the state for.
	 * @param __def If the initial state of the plugin was not specified then
	 * @return {@code true} if the plugin is enabled.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/23
	 */
	public final boolean isPluginEnabled(String __n, boolean __def)
		throws NullPointerException
	{
		if (__n == null)
			throw new NullPointerException();
		
		Boolean rv = this._pluginstate.get(__n);
		if (rv == null)
			return __def;
		return rv;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/12
	 */
	@Override
	public final String toString()
	{
		Reference<String> ref = this._string;
		String rv;
		
		if (ref == null || null == (rv = ref.get()))
			this._string = new WeakReference<>((rv =
				String.format("{enabled=%s, token=%s, " +
					"connectionfactory=%s, timeoutwindow=%d, " +
					"installmethod=%s, " +
					"pluginstate=%s}", this.enabled,
					this.token, this.connectionfactory, this.timeoutwindow,
					this.installmethod,
					this._pluginstate)));
		
		return rv;
	}
	
	/**
	 * This returns a configuration which is initialized by values using the
	 * default means of obtaining them via system properties and then
	 * environment variables.
	 *
	 * @return The default configuration to use.
	 * @since 2017/12/13
	 */
	public static final IOpipeConfiguration byDefault()
	{
		IOpipeConfigurationBuilder rv = new IOpipeConfigurationBuilder();
		
		// Enabled if not specified is "true" by default
		boolean enabled;
		rv.setEnabled((enabled = Boolean.valueOf(Objects.toString(
			System.getProperty("com.iopipe.enabled",
			System.getenv("IOPIPE_ENABLED")), "true"))));
		if (enabled)
		{
			// Token
			rv.setProjectToken(System.getProperty("com.iopipe.token",
				Objects.toString(System.getenv("IOPIPE_TOKEN"),
					System.getenv("IOPIPE_CLIENTID"))));
			
			// Installation method
			rv.setInstallMethod(System.getProperty("com.iopipe.installmethod",
				Objects.toString(System.getenv("IOPIPE_INSTALL_METHOD"),
				"manual")));
			
			// Timeout window
			try
			{
				rv.setTimeOutWindow(Integer.valueOf(Objects.toString(
					System.getProperty("com.iopipe.timeoutwindow",
					System.getenv("IOPIPE_TIMEOUT_WINDOW")), "150")));
			}
			catch (NumberFormatException e)
			{
				rv.setTimeOutWindow(150);
			}
			
			// Go through system properties to get the enabled state of
			// plugins
			for (Map.Entry<Object, Object> e : System.getProperties().
				entrySet())
			{
				String k = Objects.toString(e.getKey(), ""),
					v = Objects.toString(e.getValue(), "");
				
				if (k.startsWith(_PROPERTY_PLUGIN_PREFIX))
					rv.setPluginEnabled(
						k.substring(_PROPERTY_PLUGIN_PREFIX.length()),
						Boolean.valueOf(v));
			}
			
			// Go through environment variables to find plugins which are
			// enabled
			for (Map.Entry<String, String> e : System.getenv().entrySet())
			{
				String k = Objects.toString(e.getKey(), ""),
					v = Objects.toString(e.getValue(), "");
				
				if (k.startsWith(_ENVIRONMENT_PLUGIN_PREFIX) &&
					k.endsWith(_ENVIRONMENT_PLUGIN_SUFFIX))
					rv.setPluginEnabled(k.substring(
						_ENVIRONMENT_PLUGIN_PREFIX.length(),
						k.length() - _ENVIRONMENT_PLUGIN_SUFFIX.length()),
						Boolean.valueOf(v));
			}
			
			// Determine the URI which is used to collect resources, use the
			// same region as the AWS service if it is supported.
			String awsregion = Objects.toString(System.getenv("AWS_REGION"),
				IOpipeConstants.DEFAULT_REGION);
			if (!IOpipeConstants.SUPPORTED_REGIONS.contains(awsregion))
				awsregion = IOpipeConstants.DEFAULT_REGION;
		
			// Build hostname from region
			String hostname = (awsregion.equals(
				IOpipeConstants.DEFAULT_REGION) ?
				"metrics-api.iopipe.com" :
				String.format("metrics-api.%s.iopipe.com", awsregion));
			HttpUrl url;
			rv.setRemoteConnectionFactory(new ServiceConnectionFactory(
				(url = new HttpUrl.Builder().
					scheme("https").
					host(hostname).
					addPathSegment("v0").
					addPathSegment("event").
					build())));
			
			_LOGGER.debug(() -> "Remote URL: " + url);
		}
		
		// Fallback to disabled configuration
		else
			return IOpipeConfiguration.DISABLED_CONFIG;
		
		return rv.build();
	}
}

