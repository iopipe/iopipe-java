package com.iopipe;

import java.io.PrintStream;
import java.util.Objects;

/**
 * This class contains the configuration for IOPipe and specifies the settings
 * which are to be used when the server is contacted.
 *
 * This class is mutable.
 *
 * @since 2017/12/12
 */
public final class IOPipeConfiguration
{
	/** Debug output stream, is optional. */
	protected final PrintStream debug;
	
	/** Should the service be enabled? */
	protected final boolean enabled;
	
	/** The project token to gather statistics for. */
	protected final String token;
	
	/** The factory used to initialize new HTTP connections. */
	protected final IOPipeHTTPConnectionFactory connectionfactory;
	
	/** The timeout window in milliseconds. */
	protected final int timeoutwindow;
	
	/** Install method. */
	protected final String installmethod;
	
	/**
	 * Initializes the configuration from the specified builder.
	 *
	 * @param __builder The builder to initialize from.
	 * @throws IllegalArgumentException If the input parameters are not
	 * correct.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/13
	 */
	IOPipeConfiguration(IOPipeConfigurationBuilder __builder)
		throws IllegalArgumentException, NullPointerException
	{
		if (__builder == null)
			throw new NullPointerException();
		
		PrintStream debug = __builder._debug;
		boolean enabled = __builder._enabled;
		String token = __builder._token;
		IOPipeHTTPConnectionFactory connectionfactory =
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
		
		this.debug = debug;
		this.enabled = enabled;
		this.token = token;
		this.connectionfactory = connectionfactory;
		this.timeoutwindow = timeoutwindow;
		this.installmethod = installmethod;
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
		
		if (!(__o instanceof IOPipeConfiguration))
			return false;
		
		throw new Error("TODO");
	}
	
	/**
	 * Returns the factory which is used to connect to the IOPipe service.
	 *
	 * @return The factory used to connect to the IOPipe service.
	 * @since 2017/12/13
	 */
	public final IOPipeHTTPConnectionFactory getHTTPConnectionFactory()
	{
		return this.connectionfactory;
	}
	
	/**
	 * Returns the debug stream where debugging information is printed to.
	 *
	 * @return The stream used for debugging.
	 * @since 2017/12/13
	 */
	public final PrintStream getDebugStream()
	{
		return this.debug;
	}
	
	/**
	 * Returns a stream which can be used to report fatal errors, if a debug
	 * stream was not specified then standard error is used.
	 *
	 * @return The debug stream or standard error.
	 * @since 2017/12/15
	 */
	public final PrintStream getFatalErrorStream()
	{
		PrintStream rv = this.debug;
		if (rv != null)
			return rv;
		return System.err;
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
		throw new Error("TODO");
	}
	
	/**
	 * Returns {@code true} if IOPipe logging is to be enabled, this allows
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
	 * {@inheritDoc}
	 * @since 2017/12/12
	 */
	@Override
	public final String toString()
	{
		throw new Error("TODO");
	}
	
	/**
	 * This returns a configuration which is initialized by values using the
	 * default means of obtaining them via system properties and then
	 * environment variables.
	 *
	 * @return The default configuration to use.
	 * @since 2017/12/13
	 */
	public static final IOPipeConfiguration byDefault()
	{
		IOPipeConfigurationBuilder rv = new IOPipeConfigurationBuilder();
		
		// Enabled if not specified is "true" by default
		rv.setEnabled(Boolean.valueOf(Objects.toString(
			System.getProperty("com.iopipe.enabled",
			System.getenv("IOPIPE_ENABLED")), "true")));
		
		if (Boolean.valueOf(System.getProperty("com.iopipe.debug",
			System.getenv("IOPIPE_DEBUG"))))
			rv.setDebugStream(System.err);
		
		rv.setProjectToken(System.getProperty("com.iopipe.token",
			Objects.toString(System.getenv("IOPIPE_TOKEN"),
				System.getenv("IOPIPE_CLIENTID"))));
		
		rv.setInstallMethod(System.getProperty("com.iopipe.installmethod",
			Objects.toString(System.getenv("IOPIPE_INSTALL_METHOD"),
			"manual")));
		
		try
		{
			rv.setTimeOutWindow(Integer.valueOf(Objects.toString(
				System.getProperty("com.iopipe.timeoutwindow",
				System.getenv("IOPIPE_TIMEOUT_WINDOW")), "true")));
		}
		catch (NumberFormatException e)
		{
			rv.setTimeOutWindow(150);
		}
		
		return rv.build();
	}
}

