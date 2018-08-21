package com.iopipe.generic;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.iopipe.IOpipeService;
import com.iopipe.IOpipeWrappedException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.AbstractMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

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
	
	/** The target class of the first argument. */
	protected final Class<?> targetclass;
	
	/** Cache for translators. */
	private final Map<Class<?>, ObjectTranslator<?, ?>> _translators =
		new ConcurrentHashMap<>();
	
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
	 * @throws InvalidEntryPointException If the
	 * @throws NullPointerException On null arguments.
	 * @since 2018/08/17
	 */
	public GenericAWSRequestHandler(EntryPoint __e)
		throws NullPointerException
	{
		if (__e == null)
			throw new NullPointerException();
		
		MethodHandle handle;
		this.handle = (handle = __e.handleWithNewInstance());
		
		// Need to figure out the type of class to target
		MethodType type = handle.type();
		if (type.parameterCount() > 0)
			this.targetclass = handle.type().parameterType(0);
		else
			throw new InvalidEntryPointException("Entry point is not valid " +
				"because it lacks a first paramater: " + handle);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/08/09
	 */
	@Override
	public final Object handleRequest(Object __in, Context __context)
	{
		try
		{
			IOpipeService service = IOpipeService.instance();
			return service.<Object>run(__context, (__exec) ->
				{
					// Translate the input object and be sure to catch any
					// issues with it
					Map<Class<?>, ObjectTranslator<?, ?>> translators =
						this._translators;
					
					Class<?> fromcl = __in.getClass(),
						tocl = this.targetclass;
					
					ObjectTranslator<?, ?> translator = translators.get(
						fromcl);
					if (translator == null)
						translators.put(fromcl, (translator = ObjectTranslator.
							translator(fromcl, tocl)));
					
					// Invoke it
					try
					{
						return this.handle.invoke(translator.translateObject(__in),
							__context);
					}
					catch (Throwable e)
					{
						throw new IOpipeWrappedException(e.getMessage(), e);
					}
				}, __in);
		}
		catch (IOpipeWrappedException e)
		{
			Throwable t = e.getCause();
			if (t instanceof RuntimeException)
				throw (RuntimeException)t;
			else if (t instanceof Error)
				throw (Error)t;
			throw e;
		}
	}
}

