package com.iopipe.generic;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This class uses a generic request handler for AWS to forward and wrap
 * another method without writing a wrapper.
 *
 * @since 2018/08/09
 */
public final class GenericAWSRequestStreamHandler
	implements RequestStreamHandler
{
	/**
	 * Initializes the entry point for the generic stream handler using the
	 * default system provided entry point.
	 *
	 * @since 2018/08/13
	 */
	public GenericAWSRequestStreamHandler()
	{
		this(EntryPoint.defaultEntryPoint());
	}
	
	/**
	 * Initializes the stream handler with the given entry point.
	 *
	 * @param __e The entry point to use.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/08/13
	 */
	public GenericAWSRequestStreamHandler(EntryPoint __e)
		throws NullPointerException
	{
		if (__e == null)
			throw new NullPointerException();
		
		throw new Error("TODO");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/08/09
	 */
	@Override
	public final void handleRequest(InputStream __in, OutputStream __out,
		Context __context)
		throws IOException
	{
		throw new Error("TODO");
	}
}

