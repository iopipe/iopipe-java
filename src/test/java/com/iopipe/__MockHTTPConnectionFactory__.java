package com.iopipe;

import java.io.IOException;

/**
 * This is the connection factory which is only meant to be used for testing.
 *
 * @since 2017/12/13
 */
final class __MockHTTPConnectionFactory__
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
		return new __MockHTTPConnection__();
	}
}

