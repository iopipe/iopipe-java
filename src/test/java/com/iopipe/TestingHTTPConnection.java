package com.iopipe;

import java.io.IOException;

/**
 * This implements a basic testing connection which verifies the input request
 * and the JSON then always returns success if it is valid.
 *
 * @since 2017/12/13
 */
public class TestingHTTPConnection
	implements IOPipeHTTPConnection
{
	/**
	 * {@inheritDoc}
	 * @since 2017/12/13
	 */
	@Override
	public void close()
		throws IOException
	{
		throw new Error("TODO");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/13
	 */
	@Override
	public IOPipeHTTPResult sendRequest(IOPipeHTTPRequest __r)
		throws IOException, NullPointerException
	{
		if (__r == null)
			throw new NullPointerException();
	
		throw new Error("TODO");
	}
}

