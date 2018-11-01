package com.iopipe;

import com.iopipe.http.NullConnectionFactory;
import com.iopipe.http.RemoteConnectionFactory;
import com.iopipe.http.ServiceConnectionFactory;
import java.io.InputStream;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.TreeMap;
import org.pmw.tinylog.Logger;

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
	/** Used to compare plugin names. */
	private static final Comparator<String> _PLUGIN_COMPARATOR =
		new Comparator<String>()
		{
			/**
			 * {@inheritDoc}
			 * @since 2018/05/03
			 */
			@Override
			public int compare(String __a, String __b)
			{
				// null A is before B
				if ((__a == null) != (__b == null))
					return (__a == null ? -1 : 1);
				
				// Both are null
				else if (__a == null)
					return 0;
				
				// Differing length
				int lena = __a.length(),
					lenb = __b.length();
				int rv = (lena - lenb);
				if (rv != 0)
					return rv;
				
				// Compare, ignore case
				for (int i = 0; i < lena; i++)
				{
					char a = Character.toLowerCase(__a.charAt(i)),
						b = Character.toLowerCase(__b.charAt(i));
					
					// Map hyphens to underscores
					if (a == '-')
						a = '_';
					if (b == '-')
						b = '_';
					
					// Different character?
					rv = (a - b);
					if (rv != 0)
						return rv;
				}
				
				// Same
				return 0;
			}
		};
	
	/** The prefix for plugin enabled in system properties. */
	private static final String _PROPERTY_PLUGIN_PREFIX =
		"com.iopipe.plugin.";
	
	/** Environment variable prefix for plugin state. */
	private static final String _ENVIRONMENT_PLUGIN_PREFIX =
		"IOPIPE_";
	
	/** Environment variable suffix for plugin state. */
	private static final String _ENVIRONMENT_PLUGIN_SUFFIX =
		"_ENABLED";
	
	/** System properties file. */
	private static final Properties _PROPERTIES;
	
	/** The disabled configuration. */
	public static final IOpipeConfiguration DISABLED_CONFIG;
	
	/** Default configuration to use. */
	@Deprecated
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
	
	/** The URL to the service. */
	protected final String serviceurl;
	
	/** The URL to the signer. */
	protected final String signerurl;
	
	/** Use local coldstarts. */
	protected final boolean localcoldstart;
	
	/** The state of plugins. */
	private final Map<String, Boolean> _pluginstate =
		new TreeMap<>(_PLUGIN_COMPARATOR);
	
	/** String representation. */
	private Reference<String> _string;
	
	/**
	 * Initializes the default configuration.
	 *
	 * @since 2017/12/19
	 */
	static
	{
		// Load default properties
		Properties props = new Properties();
		try (InputStream in = ClassLoader.getSystemClassLoader().
			getResourceAsStream("/iopipe.properties"))
		{
			if (in != null)
				props.load(in);
			else
				try (InputStream intwo = IOpipeConfiguration.class.
					getResourceAsStream("/iopipe.properties"))
				{
					props.load(intwo);
				}
		}
		catch (IOException|SecurityException e)
		{
		}
		_PROPERTIES = props;
		
		// Initialize disabled configuration
		IOpipeConfigurationBuilder cb = new IOpipeConfigurationBuilder();
		
		cb.setEnabled(false);
		cb.setProjectToken("Disabled");
		cb.setInstallMethod("Disabled");
		cb.setRemoteConnectionFactory(new NullConnectionFactory());
		cb.setTimeOutWindow(0);
		cb.setServiceUrl(IOpipeConstants.DEFAULT_SERVICE_URL);
		cb.setSignerUrl(IOpipeConstants.DEFAULT_SIGNER_URL);
		
		DISABLED_CONFIG = cb.build();
		
		// Try to initialize a default configuration, if the configuration
		// is not valid due to missing values then use the disabled one
		IOpipeConfiguration use = DISABLED_CONFIG;
		try
		{
			Logger.debug("Initializing default configuration.");
			use = IOpipeConfiguration.byDefault();
		}
		catch (IllegalArgumentException|SecurityException e)
		{
			Logger.error(e, "Failed to initialize default configuration, " +
				"your method will still run however it will not report " +
				"anything to IOpipe.");
		}
		DEFAULT_CONFIG = use;
		
		// Debug the default config
		Logger.debug("Default config: {}", use);
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
		String serviceurl = __builder._serviceurl;
		
		if (token == null)
			throw new IllegalArgumentException("A project token must be " +
				"specified.");
		
		if (connectionfactory == null)
			throw new IllegalArgumentException("No connection factory " +
				"was specified.");
		
		if (timeoutwindow < 0)
			throw new IllegalArgumentException("The timeout window cannot " +
				"be negative.");
		
		// If no custom URL was specified then fallback to the default
		if (serviceurl == null)
			this.serviceurl = IOpipeConstants.DEFAULT_SERVICE_URL;
		else
			this.serviceurl = serviceurl;
		
		this.enabled = enabled;
		this.token = token;
		this.connectionfactory = connectionfactory;
		this.timeoutwindow = timeoutwindow;
		this.installmethod = installmethod;
		
		// Optional
		String signerurl = __builder._signerurl;
		if (signerurl == null)
			this.signerurl = IOpipeConstants.DEFAULT_SIGNER_URL;
		else
			this.signerurl = signerurl;
		
		this.localcoldstart = __builder._localcoldstart;
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
			this._pluginstate.equals(o._pluginstate) &&
			Objects.equals(this.serviceurl, o.serviceurl) &&
			Objects.equals(this.signerurl, o.signerurl);
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
	 * Returns the URL to use for service events.
	 *
	 * @return The URL for service events.
	 * @since 2018/02/24
	 */
	public final String getServiceUrl()
	{
		return this.serviceurl;
	}
	
	/**
	 * Returns the URL to use for sending signer requests.
	 *
	 * @return The URL to use for signer requests.
	 * @since 2018/08/24
	 */
	public final String getSignerUrl()
	{
		return this.signerurl;
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
			this._pluginstate.hashCode() ^
			Objects.hashCode(this.serviceurl) ^
			Objects.hashCode(this.signerurl);
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
					"pluginstate=%s, serviceurl=%s, signerurl=%s, " +
					"localcoldstart=%b}",
					this.enabled,
					this.token, this.connectionfactory, this.timeoutwindow,
					this.installmethod,
					this._pluginstate, this.serviceurl, this.signerurl,
					this.localcoldstart)));
		
		return rv;
	}
	
	/**
	 * Returns {@code true} if cold start detection is managed per individual
	 * instance of {@link IOpipeService}, this will result in the first
	 * execution under that instance being treated as a cold start.
	 *
	 * Otherwise {@code false} will use cold start detection on a per process
	 * basis.
	 *
	 * @return Returns {@code true} if cold start detection is per instance of
	 * {@link IOpipeService} instead of per process.
	 * @since 2018/07/17
	 */
	public final boolean getUseLocalColdStart()
	{
		return this.localcoldstart;
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
		// Derive settings from environment variables
		try
		{
			IOpipeConfigurationBuilder rv = new IOpipeConfigurationBuilder();
			
			// Enabled if not specified is "true" by default
			boolean enabled;
			rv.setEnabled((enabled = Boolean.valueOf(Objects.toString(
				System.getProperty("com.iopipe.enabled",
				System.getenv("IOPIPE_ENABLED")), "true"))));
			
			// If the configuration is not enabled, then use the disabled one
			if (!enabled)
				return IOpipeConfiguration.DISABLED_CONFIG;
			
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
					k.endsWith(_ENVIRONMENT_PLUGIN_SUFFIX) &&
					k.length() > (_ENVIRONMENT_PLUGIN_PREFIX.length() +
						_ENVIRONMENT_PLUGIN_SUFFIX.length()))
					rv.setPluginEnabled(k.substring(
						_ENVIRONMENT_PLUGIN_PREFIX.length(),
						k.length() - _ENVIRONMENT_PLUGIN_SUFFIX.length()),
						Boolean.valueOf(v));
			}
			
			// Just use the standard service connction
			rv.setRemoteConnectionFactory(new ServiceConnectionFactory());
			
			// Setup service URL
			String surl = System.getProperty("com.iopipe.collectorurl",
				Objects.toString(System.getenv("IOPIPE_COLLECTOR_URL"),
					IOpipeConstants.DEFAULT_SERVICE_URL));
			rv.setServiceUrl(surl);
			Logger.debug("Remote URL: {}", surl);
			
			// And the profiler URL
			rv.setSignerUrl(IOpipeConstants.DEFAULT_SIGNER_URL);
			
			return rv.build();
		}
		
		// Prevent configuration code issues from taking down the lambda
		catch (RuntimeException e)
		{
			Logger.error(e, "Failure building default configuration, disabling IOpipe.");
			
			// Use disabled configuration
			return IOpipeConfiguration.DISABLED_CONFIG;
		}
	}
	
	/**
	 * Gets the given variable.
	 *
	 * @param __prop The property to read from.
	 * @param __env The environment variable to check.
	 * @param __def The default value.
	 * @return The value from the system property, the environment, or the
	 * configuration.
	 * @since 2018/10/31
	 */
	public static String getVariable(String __prop, String __env, String __def)
	{
		// Check property first
		String rv;
		if (__prop != null)
		{
			rv = System.getProperty(__prop);
			if (rv != null)
				return rv;
		}
		
		// Check environment
		if (__env != null)
		{
			rv = System.getenv(__env);
			if (rv != null)
				return rv;
		}
		
		// Check files
		if (__prop != null)
		{
			rv = _PROPERTIES.getProperty(__prop);
			if (rv != null)
				return rv;
		}
		
		// Default value
		return __def;
	}
}

