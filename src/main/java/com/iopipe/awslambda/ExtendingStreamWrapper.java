package com.iopipe.awslambda;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.iopipe.IOPipeConfiguration;

/**
 * This class provides a stream handler
 *
 * To see more information on how this class is to be used, see the package
 * information.
 *
 * @since 2017/12/13
 */
public abstract class ExtendingStreamWrapper
	implements RequestStreamHandler
{
	/**
	 * This is implemented by sub-classes and is the wrapped method to be
	 * called when a lambda is to be executed.
	 *
	 * @param __in The request input stream.
	 * @param __out The request output stream.
	 * @since 2017/12/13
	 */
	protected abstract void actualHandleRequest(InputStream __in,
		OutputStream __out, Context __c);
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/13
	 */
	@Override
	public final void handleRequest(InputStream __in, OutputStream __out,
		Context __c)
		throws IOException
	{
		throw new Error("TODO");
	}
}

