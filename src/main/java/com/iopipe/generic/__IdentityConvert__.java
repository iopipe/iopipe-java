package com.iopipe.generic;

import java.lang.reflect.Type;

/**
 * This is the identity conversion which converts to objects without performing
 * any kind of translation.
 *
 * @since 2018/08/23
 */
final class __IdentityConvert__
	extends ObjectTranslator
{
	/**
	 * Initializes the translator.
	 *
	 * @param __f The from type.
	 * @param __t The to type.
	 * @since 2018/08/23
	 */
	__IdentityConvert__(Type __f, Type __t)
	{
		super(__f, __t);
	}

	/**
	 * {@inheritDoc}
	 * @since 2018/08/23
	 */
	@Override
	public final Object translate(Object __f)
	{
		return __f;
	}
}

