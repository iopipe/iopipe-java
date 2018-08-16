package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

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
						IOpipeWrappedException toss =
							new IOpipeWrappedException(e.getMessage(), e);
						throw toss;
					}
				}, __in);
		}
		catch (IOpipeWrappedException e)
		{
			throw (IOException)e.getCause();
		}
	}
}

