package com.iopipe.examples;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.iopipe.IOpipeService;

/**
 * This class wraps the simple request handler and just prefixes "Hello" to
 * any given input string. This manually initializes the service,
 *
 * @since 2017/12/18
 */
public class ManualHello
	implements RequestHandler<String, String>
{
	/**
	 * {@inheritDoc}
	 * @since 2017/12/13
	 */
	@Override
	public final String handleRequest(String __input, Context __context)
	{
		return IOpipeService.instance().<String>run(__context, (__exec) ->
			{
				return "Hello " + __input + "!";
			});
	}
}

