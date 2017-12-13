package com.iopipe;

import java.io.IOException;

/**
 * This is the connection factory which is only meant to be used for testing.
 *
 * @since 2017/12/13
 */
public class TestingHTTPConnectionFactory
	implements IOPipeHTTPConnectionFactory
{
	/**
	 * {@inheritDoc}
	 * @since 2017/12/13
	 */
	@Override
	public IOPipeHTTPConnection connect()
		throws IOException
	{
		throw new Error("TODO");
	}
}

