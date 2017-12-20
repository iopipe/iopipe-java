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
	 * @throws IOException On read/write errors.
	 * @since 2017/12/18
	 */
	protected abstract void wrappedHandleRequest(InputStream __in,
		OutputStream __out, Context __context)
		throws IOException;
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/13
	 */
	@Override
	public final void handleRequest(InputStream __in, OutputStream __out,
		Context __context)
		throws IOException
	{
		try
		{
			IOPipeService.instance().<Object>run(__context,
				() ->
				{
					try
					{
						this.wrappedHandleRequest(__in, __out, __context);
						return null;
					}
					catch (IOException e)
					{
						__IOException__ toss =
							new __IOException__(e.getMessage(), e);
						toss.setStackTrace(e.getStackTrace());
						throw toss;
					}
				});
		}
		catch (__IOException__ e)
		{
			throw (IOException)e.getCause();
		}
	}
	
	/**
	 * Used to propogate the exception to the outside.
	 *
	 * @since 2017/12/17
	 */
	private static final class __IOException__
		extends RuntimeException
	{
		/**
		 * Wraps the specified exception.
		 *
		 * @param __m The message used.
		 * @param __t The exception to wrap.
		 * @since 2017/12/18
		 */
		__IOException__(String __m, Throwable __t)
		{
			super(__m, __t);
		}
	}
}

