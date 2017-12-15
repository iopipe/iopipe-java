package com.iopipe;

import javax.json.JsonValue;

/**
 * This is a connection which has no effect and always returns the 503
 * Service Unavailable code.
 *
 * @since 2017/12/15
 */
public final class IOPipeNullHTTPConnection
	implements IOPipeHTTPConnection
{
	/**
	 * {@inheritDoc}
	 * @since 2017/12/15
	 */
	@Override
	public void close()
	{
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/15
	 */
	@Override
	public IOPipeHTTPResult sendRequest(IOPipeHTTPRequest __r)
		throws NullPointerException
	{
		if (__r == null)
			throw new NullPointerException();
		
		// Report service not available
		return new IOPipeHTTPResult(503, JsonValue.NULL);
	}
}

