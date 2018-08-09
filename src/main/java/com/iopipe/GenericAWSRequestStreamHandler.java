package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This class uses a generic request handler for AWS to forward and wrap
 * another method without writing a wrapper.
 *
 * @since 2018/08/09
 */
public final class GenericAWSRequestStreamHandler
	implements RequestStreamHandler
{
	/**
	 * {@inheritDoc}
	 * @since 2018/08/09
	 */
	@Override
	public final void handleRequest(InputStream __in, OutputStream __out,
		Context __context)
		throws IOException
	{
		throw new Error("TODO");
	}
}

