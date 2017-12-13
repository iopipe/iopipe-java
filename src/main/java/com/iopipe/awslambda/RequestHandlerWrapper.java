package com.iopipe.awslambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.iopipe.IOPipeConfiguration;

/**
 * This wraps an existing request handler wrapper and provides logging to
 * the IOPipe service.
 *
 * @param <I> The input object type.
 * @param <O> The output object type.
 * @since 2017/12/13
 */
public final class RequestHandlerWrapper<I, O>
	implements RequestHandler<I, O>
{
	/** The configuration to use. */
	protected final IOPipeConfiguration config;
	
	/** The handler being wrapped. */
	protected final RequestHandler<I, O> handler;
	
	/**
	 * Initializes the wrapper.
	 *
	 * @param __config The configuration for the IOPipe service.
	 * @param __handler The handler to wrap.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/13
	 */
	public RequestHandlerWrapper(IOPipeConfiguration __config,
		RequestHandler<I, O> __handler)
		throws NullPointerException
	{
		if (__config == null || __handler == null)
			throw new NullPointerException("NARG");
		
		this.config = __config;
		this.handler = __handler;
	}
	
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

