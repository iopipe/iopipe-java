package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

/**
 * This class is used to call other request handlers for input and output
 * of basic Java types and Plain Old Java Objects.
 *
 * This class may be extended by a sub-class or initialized by another class
 * when it is needed to call an existing handler, it is recommended that it
 * is extended.
 *
 * @param <I> The input object type.
 * @param <O> The output object type.
 * @since 2017/12/13
 */
public class CallingWrapper<I, O>
	implements RequestHandler<I, O>
{
	/** The class to be initialized and called. */
	protected final Class<RequestHandler<I, O>> call;
	
	/**
	 * Initializes the calling wrapper which sends the request to an instance
	 * of the specified class.
	 *
	 * @param __call The class to call to handle the request.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/13
	 */
	public CallingWrapper(Class<RequestHandler<I, O>> __call)
		throws NullPointerException
	{
		if (__call == null)
			throw new NullPointerException();
		
		this.call = __call;
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

