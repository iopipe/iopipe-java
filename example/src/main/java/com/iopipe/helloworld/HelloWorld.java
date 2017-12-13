package com.iopipe.helloworld;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

/**
 * Sample hello world request.
 *
 * @since 2017/12/13
 */
public class HelloWorld
	implements RequestHandler<String, String>
{
	/**
	 * {@inheritDoc}
	 * @since 2017/12/13
	 */
	@Override
	public final String handleRequest(String __input, Context __c)
	{
		__c.getLogger().log("Input: " + __input);
		return "Hello " + __input + "!";
	}
}

