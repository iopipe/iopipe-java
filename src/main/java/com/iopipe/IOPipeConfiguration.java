package com.iopipe;

import com.iopipe.http.NullConnectionFactory;
import com.iopipe.http.RemoteConnectionFactory;
import com.iopipe.http.ServiceConnectionFactory;
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
	/** The disabled configuration. */
	public static final IOPipeConfiguration DISABLED_CONFIG;
	
	/** Default configuration to use. */
	public static final IOPipeConfiguration DEFAULT_CONFIG;
	
	/** Debug output stream, is optional. */
	protected final PrintStream debug;
	
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
	
	/**
	 * Initializes the default configuration.
	 *
	 * @since 2017/12/19
	 */
	static
	{
		// Initialize disabled configuration
		IOPipeConfigurationBuilder cb = new IOPipeConfigurationBuilder();
		
		cb.setEnabled(false);
		cb.setProjectToken("Disabled");
		cb.setInstallMethod("Disabled");
		cb.setDebugStream(null);
		cb.setRemoteConnectionFactory(new NullConnectionFactory());
		cb.setTimeOutWindow(0);
		
		DISABLED_CONFIG = cb.build();
		
		// Try to initialize a default configuration, if the configuration
		// is not valid due to missing values then use the disabled one
		IOPipeConfiguration use = DISABLED_CONFIG;
		try
		{
			use = IOPipeConfiguration.byDefault();
		}
		catch (IllegalArgumentException e)
		{
		}
		DEFAULT_CONFIG = use;
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
	IOPipeConfiguration(IOPipeConfigurationBuilder __builder)
		throws IllegalArgumentException, NullPointerException
	{
		if (__builder == null)
			throw new NullPointerException();
		
		PrintStream debug = __builder._debug;
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
	public final RemoteConnectionFactory getRemoteConnectionFactory()
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
		boolean enabled;
		rv.setEnabled((enabled = Boolean.valueOf(Objects.toString(
			System.getProperty("com.iopipe.enabled",
			System.getenv("IOPIPE_ENABLED")), "true"))));
		if (enabled)
		{
			PrintStream debugstream = null;
			if (Boolean.valueOf(System.getProperty("com.iopipe.debug",
				System.getenv("IOPIPE_DEBUG"))))
				rv.setDebugStream((debugstream = System.err));
		
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
					System.getenv("IOPIPE_TIMEOUT_WINDOW")), "150")));
			}
			catch (NumberFormatException e)
			{
				rv.setTimeOutWindow(150);
			}
		
			// Determine the URI which is used to collect resources, use the
			// same region as the AWS service if it is supported.
			String awsregion = Objects.toString(System.getenv("AWS_REGION"),
				IOPipeConstants.DEFAULT_REGION);
			if (!IOPipeConstants.SUPPORTED_REGIONS.contains(awsregion))
				awsregion = IOPipeConstants.DEFAULT_REGION;
		
			// Build hostname from region
			String hostname = (awsregion.equals(
				IOPipeConstants.DEFAULT_REGION) ?
				"metrics-api.iopipe.com" :
				String.format("metrics-api.%s.iopipe.com", awsregion));
			String url = String.format("https://%s/v0/event", hostname);
			rv.setRemoteConnectionFactory(new ServiceConnectionFactory(url));
		
			if (debugstream != null)
				debugstream.printf("IOPipe: Remote URL `%s`%n", url);
		}
		
		// Fallback to disabled configuration
		else
			return IOPipeConfiguration.DISABLED_CONFIG;
		
		return rv.build();
	}
}

