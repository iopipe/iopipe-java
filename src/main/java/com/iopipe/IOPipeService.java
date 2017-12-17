package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class provides a single connection to the IOPipe service which may then
 * be used to send multiple requests to as methods are ran. This class is
 * permitted to be used as a singleton if desired but it can be used as a cache
 * according to the input configuration.
 *
 * It is recommended that when this class is no longer needed that it is closed
 * so that the connection is freed rather than letting it remain open
 * potentially during future executions.
 *
 * @since 2017/12/13
 */
public final class IOPipeService
	implements AutoCloseable
{
	/** The version for this agent. */
	public static final String AGENT_VERSION =
		"1.0-SNAPSHOT";
	
	/** This is used to detect cold starts. */
	static final AtomicBoolean _THAWED =
		new AtomicBoolean();
	
	/** The time this class was initialized, used for load time. */
	static final long _LOAD_TIME =
		System.currentTimeMillis();
	
	/** The configuration used to connect to the service. */
	protected final IOPipeConfiguration config;
	
	/** The connection to the server. */
	protected final IOPipeHTTPConnection connection;
	
	/** Used to report timeouts. */
	protected final IOPipeTimeoutManager timeouts;
	
	/** Is the service enabled and working? */
	protected final boolean enabled;
	
	/** Has this been closed? */
	private volatile boolean _closed;
	
	/**
	 * Initializes the service using the default configuration.
	 *
	 * @since 2017/12/14
	 */
	public IOPipeService()
	{
		this(IOPipeConfiguration.byDefault());
	}
	
	/**
	 * Initializes the service using the specified configuration.
	 *
	 * @param __config The configuration to use.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/24
	 */
	public IOPipeService(IOPipeConfiguration __config)
		throws NullPointerException
	{
		if (__config == null)
			throw new NullPointerException();
		
		// Try to open a connection to the IOPipe service, if that fails
		// then fall back to a disabled connection
		IOPipeHTTPConnection connection = null;
		boolean enabled = false;
		if (__config.isEnabled())
			try
			{
				connection = __config.getHTTPConnectionFactory().connect();
				enabled = true;
			}
			
			// Cannot report error to IOPipe so print to the console
			catch (IOException e)
			{
				e.printStackTrace(__config.getFatalErrorStream());
			}
		
		// If the connection failed, use one which does nothing
		if (connection == null)
			connection = new IOPipeNullHTTPConnection();
		
		this.enabled = enabled;
		this.connection = connection;
		this.config = __config;
		this.timeouts = new IOPipeTimeoutManager(connection);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/14
	 */
	@Override
	public final void close()
	{
		boolean closed = this._closed;
		if (!closed)
			try
			{
				this.connection.close();
			}
			
			// The connection is probably not valid so it cannot be reported
			// to IOPipe
			catch (IOException e)
			{
				e.printStackTrace(this.config.getFatalErrorStream());
			}
	}
	
	/**
	 * Creates a new service context for the given lambda context.
	 *
	 * @param __c The context to execution under.
	 * @return The context to use for execution, which may only occur once.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/24
	 */
	public final IOPipeContext createContext(Context __c)
		throws NullPointerException
	{
		if (__c == null)
			throw new NullPointerException();
		
		IOPipeConfiguration config = this.config;
		
		PrintStream debug = config.getDebugStream();
		if (debug != null)
			debug.printf("IOPipe: createContext(%s)%n", __c);
		
		// Contexts may timeout after a given amount of time
		return new IOPipeContext(__c, config, this.timeouts, this.connection);
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
}

