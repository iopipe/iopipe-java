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
				// Send a message to the example plugin
				__exec.<ExampleExecution>plugin(ExampleExecution.class,
					(__s) ->
					{
						__s.message("I shall say hello!");
						__s.message(__input);
					});
				
				// Custom metrics which could convey important information
				__exec.customMetric("hello", "world");
				__exec.customMetric("numbers", 12346789);
				
				// Say hello to them!
				return "Hello " + __input + "!";
			});
	}
}

