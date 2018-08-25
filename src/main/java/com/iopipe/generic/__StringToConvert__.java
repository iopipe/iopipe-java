package com.iopipe.generic;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Type;

/**
 * This is the identity conversion which converts to objects without performing
 * any kind of translation.
 *
 * @since 2018/08/24
 */
final class __StringToConvert__
	extends ObjectTranslator
{
	/** The method handle which generates the argument. */
	protected final MethodHandle constructor;
	
	/**
	 * Initializes the translator.
	 *
	 * @param __f The from type.
	 * @param __t The to type.
	 * @param __c The constructor to the method.
	 * @throws NullPointerException On no handle was specified.
	 * @since 2018/08/24
	 */
	__StringToConvert__(Type __f, Type __t, MethodHandle __c)
		throws NullPointerException
	{
		super(__f, __t);
		
		if (__c == null)
			throw new NullPointerException();
		
		this.constructor = __c;
	}

	/**
	 * {@inheritDoc}
	 * @since 2018/08/24
	 */
	@Override
	public final Object translate(Object __f)
	{
		// Constructors and valueOf() may fail since they may require
		// an argument, but this method always maps to null anyway so
		if (__f == null)
			return null;
		
		try
		{
			return this.constructor.invoke((String)__f);
		}
		catch (Throwable t)
		{
			if (t instanceof RuntimeException)
				throw (RuntimeException)t;
			else if (t instanceof Error)
				throw (Error)t;
			else
				throw new RuntimeException("Object translator threw " +
					"checked exception.", t);
		}
	}
}

