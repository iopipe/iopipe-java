package com.iopipe.examples;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

/**
 * This is a plain Hello response which does not use the IOPipe service and
 * is called directly by AWS.
 *
 * @since 2017/12/19
 */
public class PlainHello
	implements RequestHandler<String, String>
{
	/**
	 * {@inheritDoc}
	 * @since 2017/12/19
	 */
	@Override
	public final String handleRequest(String __input, Context __c)
	{
		return "Hello " + __input + "!";
	}
}

