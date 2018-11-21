package com.iopipe.generic;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.iopipe.IOpipeService;
import com.iopipe.IOpipeWrappedException;
import java.lang.invoke.MethodHandle;
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
	/** The handle used for entry. */
	protected final MethodHandle handle;
	
	/**
	 * Initializes the entry point for the generic stream handler using the
	 * default system provided entry point.
	 *
	 * @throws InvalidEntryPointException If the entry point is not valid.
	 * @since 2018/08/13
	 */
	public GenericAWSRequestStreamHandler()
		throws InvalidEntryPointException
	{
		this(EntryPoint.defaultAWSEntryPoint());
	}
	
	/**
	 * Initializes the stream handler with the given entry point.
	 *
	 * @param __cl The entry class.
	 * @param __m The entry method.
	 * @throws InvalidEntryPointException If the entry point is not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/08/24
	 */
	public GenericAWSRequestStreamHandler(Class<?> __cl, String __m)
		throws InvalidEntryPointException, NullPointerException
	{
		this(EntryPoint.newAWSEntryPoint(__cl, __m));
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
		
		this.handle = __e.handleWithNewInstance();
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
		try
		{
			IOpipeService service = IOpipeService.instance();
			service.<Object>run(__context, (__exec) ->
				{
					try
					{
						this.handle.invoke(__in, __out, __context);
					}
					catch (Throwable e)
					{
						throw new IOpipeWrappedException(e.getMessage(), e);
					}
					
					return null;
				}, __in);
		}
		catch (IOpipeWrappedException e)
		{
			Throwable t = e.getCause();
			if (t instanceof IOException)
				throw (IOException)t;
			else if (t instanceof RuntimeException)
				throw (RuntimeException)t;
			else if (t instanceof Error)
				throw (Error)t;
			throw e;
		}
	}
}

