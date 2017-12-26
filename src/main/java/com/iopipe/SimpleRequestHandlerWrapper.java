package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

/**
 * This class is used as a base to create simple instances of wrapped lambdas
 * which will provide measurements to the IOpipe service.
 *
 * This class may be used as a base to wrap requests.
 *
 * @param <I> The input object type.
 * @param <O> The output object type.
 * @since 2017/12/13
 */
public abstract class SimpleRequestHandlerWrapper<I, O>
	implements RequestHandler<I, O>
{
	/**
	 * This method is implemented by sub-classes and is used as the actual
	 * entry point for lambdas.
	 *
	 * @param __input The input value.
	 * @param __context The lambda context.
	 * @return The return value.
	 * @since 2017/12/18
	 */
	protected abstract O wrappedHandleRequest(I __input, Context __context);
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/13
	 */
	@Override
	public final O handleRequest(I __input, Context __context)
	{
		return IOPipeService.instance().<O>run(__context,
			() -> this.wrappedHandleRequest(__input, __context));
	}
}

