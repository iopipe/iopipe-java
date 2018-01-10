package com.iopipe;

import com.iopipe.http.RemoteConnectionFactory;
import java.io.PrintStream;
import java.net.URI;
import okhttp3.HttpUrl;

/**
 * This class is used to initialize instances of {@link IOpipeConfiguration}
 *
 * This class is not thread safe.
 *
 * @since 2017/12/13
 */
public class IOpipeConfigurationBuilder
{
	/** Is the service enabled? */
	volatile boolean _enabled;
	
	/** The project token. */
	volatile String _token;
	
	/** Debug stream, this is optional. */
	volatile PrintStream _debug;
	
	/** The factory to use for connections. */
	volatile RemoteConnectionFactory _connectionfactory;
	
	/** The timeout window in milliseconds. */
	volatile int _timeoutwindow;
	
	/** Install method. */
	volatile String _installmethod;
	
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
		this._debug = __c.getDebugStream();
		this._connectionfactory = __c.getRemoteConnectionFactory();
		this._timeoutwindow = __c.getTimeOutWindow();
		this._installmethod = __c.getInstallMethod();
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
	 * Specifies that debugging should be enabled and that all debugging output
	 * is to be written to the given stream.
	 *
	 * @param __ps The stream to print debugging messages to.
	 * @since 2017/12/13
	 */
	public final void setDebugStream(PrintStream __ps)
	{
		this._debug = __ps;
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
}

