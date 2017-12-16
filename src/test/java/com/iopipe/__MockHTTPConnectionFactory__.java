package com.iopipe;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * This is the connection factory which is only meant to be used for testing.
 *
 * @since 2017/12/13
 */
final class __MockHTTPConnectionFactory__
	implements IOPipeHTTPConnectionFactory
{
	/** When a request is made this function will be called. */
	protected final Consumer<IOPipeHTTPRequest> function;
	
	/**
	 * Initializes the factory where requests are passed to the given consumer
	 * for testing.
	 *
	 * @param __func The function which receives requests.
	 * @since 2017/12/16
	 */
	__MockHTTPConnectionFactory__(Consumer<IOPipeHTTPRequest> __func)
	{
		this.function = __func;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/13
	 */
	@Override
	public IOPipeHTTPConnection connect()
		throws IOException
	{
		return new __MockHTTPConnection__(this.function);
	}
}

