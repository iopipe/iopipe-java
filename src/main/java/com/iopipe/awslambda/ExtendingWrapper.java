package com.iopipe.awslambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.iopipe.IOPipeConfiguration;

/**
 * This class is extended and is used as the entry point for a request to
 * be handled with basic Java types or Plain Java Objects.
 *
 * To see more information on how this class is to be used, see the package
 * information.
 *
 * @param <I> The input object type.
 * @param <O> The output object type.
 * @since 2017/12/13
 */
public abstract class ExtendingWrapper<I, O>
	implements RequestHandler<I, O>
{
	/**
	 * This is implemented by sub-classes and is the wrapped method to be
	 * called when a lambda is to be executed.
	 *
	 * @param __input The input argument.
	 * @param __c The lambda context.
	 * @return The result of the request.
	 * @since 2017/12/13
	 */
	protected abstract O actualHandleRequest(I __input, Context __c);
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/13
	 */
	@Override
	public final O handleRequest(I __input, Context __c)
	{
		throw new Error("TODO");
	}
}
