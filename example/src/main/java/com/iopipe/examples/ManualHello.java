package com.iopipe.examples;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.iopipe.IOpipeService;
import com.iopipe.plugin.trace.TraceMark;
import com.iopipe.plugin.trace.TraceMeasurement;
import com.iopipe.plugin.trace.TraceUtils;

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
				
				// Measure performance of this method via the trace plugin
				try (TraceMeasurement m = TraceUtils.measure(__exec, "math"))
				{
					// Add a bunch of numbers together
					TraceMark addstart = TraceUtils.mark(__exec, "addstart");
			
					long result = 0;
					for (int i = 1; i < 10000; i++)
						result += new Long(i);
			
					// Multiply a bunch of numbers into the result
					TraceMark mulstart = TraceUtils.mark(__exec, "mulstart");
			
					for (int i = 1; i < 10000; i++)
						result *= new Long(i);
			
					// How long did multiplication take?
					TraceUtils.measure(__exec, "multime", mulstart,
						TraceUtils.mark(__exec, "mulend"));
			
					// Store the result of the math
					__exec.customMetric("result", (long)result);
				}
				
				// Say hello to them!
				return "Hello " + __input + "!";
			});
	}
}

