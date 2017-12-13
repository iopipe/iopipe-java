package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

/**
 * This class provides access to the IOPipe service and allows for sending
 * metrics to the server.
 *
 * @since 2017/12/13
 */
public final class IOPipeService
{
	/** The configuration used to connect to the service. */
	protected final IOPipeConfiguration config;
	
	/**
	 * Initializes the IOPipe service using the default system configuration.
	 *
	 * @throws IllegalArgumentException If the default parameters are not
	 * valid.
	 * @since 2017/12/13
	 */
	public IOPipeService()
		throws IllegalArgumentException
	{
		this(IOPipeConfiguration.byDefault());
	}
	
	/**
	 * Initializes the IOPipe service using the specified configuration.
	 *
	 * @param __config The configuration to use.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/13
	 */
	public IOPipeService(IOPipeConfiguration __config)
		throws NullPointerException
	{
		if (__config == null)
			throw new NullPointerException("NARG");
		
		this.config = __config;
	}
	
	/**
	 * Runs the specified request handler and performs all of the required
	 * metric handling which is then sent to the IOPipe servers.
	 *
	 * @param <I> The input object type.
	 * @param <O> The output object type.
	 * @param __handler The handler to be called with the given input and
	 * output.
	 * @param __i The input object for the handler.
	 * @param __c The context for the handler.
	 * @return The result of execution.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/13
	 */
	public <I, O> O run(RequestHandler<I, O> __handler, I __i, Context __c)
		throws NullPointerException
	{
		if (__handler == null || __c == null)
			throw new NullPointerException("NARG");
		
		throw new Error("TODO");
	}
}

