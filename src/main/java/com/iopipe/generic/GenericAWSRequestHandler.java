package com.iopipe.generic;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.lang.invoke.MethodHandle;

/**
 * This class uses a generic request handler for AWS to forward and wrap
 * another method without writing a wrapper.
 *
 * @since 2018/08/09
 */
public final class GenericAWSRequestHandler
	implements RequestHandler<Object, Object>
{
	/** The handle used for entry. */
	protected final MethodHandle handle;
	
	/**
	 * Initializes the entry point for the generic handler using the
	 * default system provided entry point.
	 *
	 * @since 2018/08/17
	 */
	public GenericAWSRequestHandler()
	{
		this(EntryPoint.defaultEntryPoint());
	}
	
	/**
	 * Initializes the handler with the given entry point.
	 *
	 * @param __e The entry point to use.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/08/17
	 */
	public GenericAWSRequestHandler(EntryPoint __e)
		throws NullPointerException
	{
		if (__e == null)
			throw new NullPointerException();
		
		this.handle = __e.handleWithNewInstance();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/08/09
	 */
	@Override
	public final Object handleRequest(Object __in, Context __context)
	{
		throw new Error("TODO");
	}
}
