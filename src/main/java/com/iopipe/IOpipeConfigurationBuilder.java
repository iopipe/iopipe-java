package com.iopipe;

import com.iopipe.http.RemoteConnectionFactory;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to initialize instances of {@link IOpipeConfiguration}
 *
 * This class is not thread safe.
 *
 * @since 2017/12/13
 */
public class IOpipeConfigurationBuilder
{
	/** Specific plugin states. */
	final Map<String, Boolean> _pluginstate =
		new HashMap<>();
	
	/** Is the service enabled? */
	volatile boolean _enabled;
	
	/** The project token. */
	volatile String _token;
	
	/** The factory to use for connections. */
	volatile RemoteConnectionFactory _connectionfactory;
	
	/** The timeout window in milliseconds. */
	volatile int _timeoutwindow;
	
	/** Install method. */
	volatile String _installmethod;
	
	/** The URL to send service requests to. */
	volatile String _serviceurl;
	
	/** The URL to send signer requests to. */
	volatile String _signerurl;
	
	/** Use local coldstarts per service. */
	volatile boolean _localcoldstart;
	
	/**
	 * Initializes the builder with uninitialized values.
	 *
	 * @since 2017/12/18
	 */
	public IOpipeConfigurationBuilder()
	{
	}
	
	/**
	 * This initializes the builder with values copied from the specified
	 * configuration.
	 *
	 * @param __c The configuration with values to copy.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/18
	 */
	public IOpipeConfigurationBuilder(IOpipeConfiguration __c)
		throws NullPointerException
	{
		if (__c == null)
			throw new NullPointerException();
		
		this._enabled = __c.isEnabled();
		this._token = __c.getProjectToken();
		this._connectionfactory = __c.getRemoteConnectionFactory();
		this._timeoutwindow = __c.getTimeOutWindow();
		this._installmethod = __c.getInstallMethod();
		this._serviceurl = __c.getServiceUrl();
		this._signerurl = __c.getSignerUrl();
		this._localcoldstart = __c.getUseLocalColdStart();
	}
	
	/**
	 * This constructs an instance of the configuration settings from the
	 * requested configuration values.
	 *
	 * @return An immutable instance of this configuration.
	 * @throws IllegalArgumentException If a specified configuration setting
	 * is not valid.
	 * @since 2017/12/13
	 */
	public final IOpipeConfiguration build()
		throws IllegalArgumentException
	{
		return new IOpipeConfiguration(this);
	}
	
	/**
	 * Returns the factory which is used to connect to the IOpipe service.
	 *
	 * @return The factory used to connect to the IOpipe service or
	 * {@code null} if it has not been set.
	 * @since 2018/01/23
	 */
	public final RemoteConnectionFactory getRemoteConnectionFactory()
	{
		return this._connectionfactory;
	}
	
	/**
	 * Sets whether the IOpipe service to to be enabled.
	 *
	 * @param __enabled If {@code true} then the IOpipe service is to be used,
	 * otherwise any requests will NOT use the service.
	 * @since 2017/12/13
	 */
	public final void setEnabled(boolean __enabled)
	{
		this._enabled = __enabled;
	}
	
	/**
	 * Sets the install method.
	 *
	 * @param __im The install method.
	 * @since 2017/12/13
	 */
	public final void setInstallMethod(String __im)
	{
		this._installmethod = __im;
	}
	
	/**
	 * This sets the specific state of a plugin.
	 *
	 * @param __p The plugin to modify.
	 * @param __e Whether the plugin is to be enabled or disabled.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/23
	 */
	public final void setPluginEnabled(String __p, boolean __e)
		throws NullPointerException
	{
		if (__p == null)
			throw new NullPointerException();
		
		this._pluginstate.put(__p, __e);
	}
	
	/**
	 * Sets the project token.
	 *
	 * @param __token The token which specifies the project to measure
	 * the statistics for.
	 * @since 2017/12/13
	 */
	public final void setProjectToken(String __token)
	{
		this._token = __token;
	}
	
	/**
	 * Sets the factory to be used to make HTTP connections to the sevrice.
	 *
	 * @param __cf The factory to use for creating new HTTP connections.
	 * @since 2017/12/13
	 */
	public final void setRemoteConnectionFactory(
		RemoteConnectionFactory __cf)
	{
		this._connectionfactory = __cf;
	}
	
	/**
	 * Sets the URL to use when connecting to the IOpipe service.
	 *
	 * @param __u The URL to use to send requests to, using {@code null} will
	 * initialize the configuration with the default URL.
	 * @since 2018/02/24
	 */
	public final void setServiceUrl(String __u)
	{
		this._serviceurl = __u;
	}
	
	/**
	 * Sets the URL to use when connecting to the signer service.
	 *
	 * @param __u The URL to use to send signer requests to.
	 * @since 2018/09/24
	 */
	public final void setSignerUrl(String __u)
	{
		this._signerurl = __u;
	}
	
	/**
	 * Sets the timeout window in milliseconds.
	 *
	 * @param __ms The length of the timeout window, zero disables this
	 * feature.
	 * @throws IllegalArgumentException If the length is negative.
	 * @since 2017/12/13
	 */
	public final void setTimeOutWindow(int __ms)
		throws IllegalArgumentException
	{
		if (__ms < 0)
			throw new IllegalArgumentException("The timeout window " +
				"cannot be negative.");
		
		this._timeoutwindow = __ms;
	}
	
	/**
	 * Set to true {@code true} if cold start detection is to be managed per
	 * individual instance of {@link IOpipeService}, this will result in the
	 * first execution under that instance being treated as a cold start.
	 *
	 * Otherwise {@code false} will use cold start detection on a per process
	 * basis.
	 *
	 * This generally is not needed and is not recommended as warm starts will
	 * be flagged as coldstarts if a new instance is created. This is only for
	 * advanced service usage.
	 *
	 * This defaults to {@code false}.
	 *
	 * @param __yes If {@code true} then coldstarts are to be managed per
	 * instance of {@code IOpipeService}.
	 * @since 2018/07/17
	 */
	public final void setUseLocalColdStart(boolean __yes)
	{
		this._localcoldstart = __yes;
	}
}

