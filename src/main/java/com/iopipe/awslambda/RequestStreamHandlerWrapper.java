package com.iopipe.awslambda;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.iopipe.IOPipeConfiguration;

/**
 * This wraps an existing request stream handler and provides logging to the
 * IOPipe service.
 *
 * @since 2017/12/13
 */
public final class RequestStreamHandlerWrapper
	implements RequestStreamHandler
{
	/** The configuration to use. */
	protected final IOPipeConfiguration config;
	
	/** The handler being wrapped. */
	protected final RequestStreamHandler handler;
	
	/**
	 * Initializes the wrapper.
	 *
	 * @param __config The configuration for the IOPipe service.
	 * @param __handler The handler to wrap.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/13
	 */
	public RequestStreamHandlerWrapper(IOPipeConfiguration __config,
		RequestStreamHandler __handler)
		throws NullPointerException
	{
		if (__config == null)
			throw new NullPointerException("NARG");
		
		this.config = __config;
		this.handler = __handler;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/13
	 */
	@Override
	public final void handleRequest(InputStream __in, OutputStream __out,
		Context __c)
		throws IOException
	{
		throw new Error("TODO");
	}
}

