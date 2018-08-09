package com.iopipe;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

/**
 * This class is used as a base to create simple instances of wrapped lambdas
 * which will provide measurements to the IOpipe service.
 *
 * This class may be used as a base to wrap requests.
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
	 * @param __exec The instance for the current lambda execution. The
	 * {@link com.amazonaws.services.lambda.runtime.Context} object can be
	 * obtained by invoking the {@link IOpipeExecution#context()} method on
	 * the {@code __exec} parameter.
	 * @param __in The input stream.
	 * @param __out The output stream.
	 * @throws IOException On read/write errors.
	 * @since 2017/12/18
	 */
	protected abstract void wrappedHandleRequest(IOpipeExecution __exec,
		InputStream __in, OutputStream __out)
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
			IOpipeService.instance().run(__context,
				(__exec) ->
				{
					try
					{
						this.wrappedHandleRequest(__exec, __in, __out);
						return null;
					}
					catch (IOException e)
					{
						__IOException__ toss =
							new __IOException__(e.getMessage(), e);
						toss.setStackTrace(e.getStackTrace());
						throw toss;
					}
				}, __in);
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
	static final class __IOException__
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

