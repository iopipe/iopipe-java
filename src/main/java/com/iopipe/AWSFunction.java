package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;

/**
 * This functional interface is used to wrap AWS request handlers and is used
 * by {@link IOpipeService#run(Context, AWSFunction, Object)}.
 *
 * @since 2018/08/09
 */
@FunctionalInterface
public interface AWSFunction<I, O>
{
	/**
	 * Handles the specified request.
	 *
	 * @param __v The input value.
	 * @param __context The AWS Context.
	 * @return The output value of the request.
	 * @since 2018/08/09
	 */
	public abstract O handleRequest(I __v, Context __context);
}

