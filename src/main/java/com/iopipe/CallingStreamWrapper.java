package com.iopipe;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

/**
 * This class is used to call other stream handlers, it dynamically looks up
 * the other class and initializes the IOPipe interface.
 *
 * This class may be extended by a sub-class or initialized by another class
 * when it is needed to call an existing handler, it is recommended that it
 * is extended.
 *
 * @since 2017/12/13
 */
public class CallingStreamWrapper
	implements RequestStreamHandler
{
	/** The class to be initialized and called. */
	protected final Class<RequestStreamHandler> call;
	
	/**
	 * Initializes the calling wrapper which sends the request to an instance
	 * of the specified class.
	 *
	 * @param __call The class to call to handle the request.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/13
	 */
	public CallingStreamWrapper(Class<RequestStreamHandler> __call)
		throws NullPointerException
	{
		if (__call == null)
			throw new NullPointerException();
		
		this.call = __call;
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

