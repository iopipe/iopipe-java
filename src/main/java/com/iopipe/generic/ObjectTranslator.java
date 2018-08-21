package com.iopipe.generic;

import java.lang.reflect.Type;

/**
 * This class manages converters to the target type.
 *
 * @since 2018/08/20
 */
public abstract class ObjectTranslator
{
	/** The type to convert to. */
	protected final Type to;
	
	/**
	 * Initializes a translator to the given type.
	 *
	 * @param __t The target type
	 * @throws NullPointerException On null arguments.
	 * @since 2018/08/20
	 */
	private ObjectTranslator(Type __t)
		throws NullPointerException
	{
		if (__t == null)
			throw new NullPointerException();
		
		this.to = __t;
	}
	
	/**
	 * Translates from the given object to the destination object.
	 *
	 * @param __f The object to translate, if this is null then no object
	 * is translated and null is returned.
	 * @return The result of the translation, if {@code __f} is null then this
	 * will return null.
	 * @since 2018/08/20
	 */
	public abstract Object translate(Object __f);
	
	/**
	 * {@inheritDoc}
	 * @since 2018/08/20
	 */
	@Override
	public final boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		if (!(__o instanceof ObjectTranslator))
			return false;
		
		ObjectTranslator o = (ObjectTranslator)__o;
		return this.to.equals(o.to);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/08/20
	 */
	@Override
	public final int hashCode()
	{
		return this.to.hashCode();
	}
	
	/**
	 * Creates a translator for converting objects to the given type.
	 *
	 * @param __t The to class.
	 * @return A translator to translate from one class to the other.
	 * @since 2018/08/20
	 */
	public static final ObjectTranslator translator(Type __t)
		throws NullPointerException
	{
		if (__t == null)
			throw new NullPointerException();
		
		return new __JacksonConvert__(__t);
	}
	
	/**
	 * Converter which uses jackson.
	 *
	 * @since 2018/08/21
	 */
	private static final class __JacksonConvert__
		extends ObjectTranslator
	{
		/**
		 * Initializes the translator.
		 *
		 * @param __t The to type.
		 * @since 2018/08/21
		 */
		private __JacksonConvert__(Type __t)
		{
			super(__t);
		}
	
		/**
		 * {@inheritDoc}
		 * @since 2018/08/21
		 */
		@Override
		public final Object translate(Object __f)
		{
			throw new Error("No conversion yet for " + this.to);
		}
	}
}

