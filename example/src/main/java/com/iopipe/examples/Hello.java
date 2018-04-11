package com.iopipe.examples;

import java.util.Collections;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.iopipe.IOpipeExecution;
import com.iopipe.plugin.trace.TraceMeasurement;
import com.iopipe.plugin.trace.TraceUtils;
import com.iopipe.SimpleRequestHandlerWrapper;

/**
 * This class wraps the simple request handler and prefixes "Hello" to
 * an input object containing {name:"Foo"}
 *
 * @since 2017/12/18
 */

public class Hello
	extends SimpleRequestHandlerWrapper<Map<String,String>, String>
{
	/**
	 * {@inheritDoc}
	 * @since 2017/12/13
	 */
	@Override
	protected final String wrappedHandleRequest(IOpipeExecution __exec,
		Map<String,String> request)
	{
		String name = request.containsKey("name") ? request.get("name") : null;

		if (name == null) {
			throw new RuntimeException("Invoked with no name!");
		}

		// Send a message to the example plugin
		__exec.<ExampleExecution>plugin(ExampleExecution.class, (__s) ->
			{
				__s.message("I shall say hello!");
				__s.message(name);
			});

		// Custom metrics which could convey important information
		__exec.customMetric("hello", "world");

		// Measure performance of this method via the trace plugin
		try (TraceMeasurement m = TraceUtils.measure(__exec, "math"))
		{
			long result = 0;
			for (int i = 1; i < 10000; i++)
				result += new Long(i);
			
			for (int i = 1; i < 10000; i++)
				result *= new Long(i);
			
			// Store the result of the math
			__exec.customMetric("result", (long)result);
		}
		
		// Test label
		__exec.label("test");

		// Say hello to them!
		return "Hello " + name + "!";
	}
}
