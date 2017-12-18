package com.iopipe;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

/**
 * This class is used as a base to create simple instances of wrapped lambdas
 * which will provide measurements to the IOPipe service.
 *
 * @since 2017/12/13
 */
public abstract class SimpleRequestStreamHandlerWrapper
	implements RequestStreamHandler
{
	/**
	 * This method is implemented by sub-classes and is used as the actual
	 * entry point for lambdas.
	 *
	 * @param __in The input stream.
	 * @param __out The output stream.
	 * @param __context The lambda context.
	 * @return The return value.
	 * @since 2017/12/18
	 */
	protected abstract void wrappedHandleRequest(InputStream __in,
		OutputStream __out, Context __context);
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/13
	 */
	@Override
	public final void handleRequest(InputStream __in, OutputStream __out,
		Context __context)
		throws IOException
	{
		try (IOPipeService sv = new IOPipeService())
		{
			sv.createContext(__context).<Object>run(
				() ->
				{
					this.wrappedHandleRequest(__in, __out, __context);
					return null;
				});
		}
	}
}

