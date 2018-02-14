package com.iopipe.examples;

import java.util.Collections;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

/**
 * This is a plain Hello response which does not use the IOpipe service and
 * is called directly by AWS, given an input object containing { "name": "foo"}.
 *
 * @since 2017/12/19
 */
public class PlainHello
	implements RequestHandler<Map<String,String>, String>
{
	/**
	 * {@inheritDoc}
	 * @since 2017/12/19
	 */
	@Override
	public final String handleRequest(Map<String,String> request, Context __c)
	{
		String name = request.containsKey("name") ? request.get("name") : null;
		if (name == null) {
			throw new RuntimeException("Invoked with no name!");
		}
		return "Hello " + name + "!";
	}
}

