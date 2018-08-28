package com.iopipe.generic;

import java.lang.reflect.Type;
import java.util.function.Function;

/**
 * This is a conversion which uses a simple functional mapping from one type
 * to another.
 *
 * This is much simpler than {@link __StringToConvert__} because this class
 * only operates on known methods that exist.
 *
 * @since 2018/08/27
 */
final class __FunctionConvert__
	extends ObjectTranslator
{
	/** The function used for conversion. */
	protected final Function<Object, Object> function;
	
	/**
	 * Initializes the translator.
	 *
	 * @param __f The from type.
	 * @param __t The to type.
	 * @param __func The conversion function.
	 * @throws NullPointerException On no handle was specified.
	 * @since 2018/08/27
	 */
	__FunctionConvert__(Type __f, Type __t, Function<Object, Object> __func)
		throws NullPointerException
	{
		super(__f, __t);
		
		if (__func == null)
			throw new NullPointerException();
		
		this.function = __func;
	}

	/**
	 * {@inheritDoc}
	 * @since 2018/08/27
	 */
	@Override
	public final Object translate(Object __f)
	{
		// Map null to null
		if (__f == null)
			return null;
		
		// Apply the translation
		return this.function.apply(__f);
	}
}

