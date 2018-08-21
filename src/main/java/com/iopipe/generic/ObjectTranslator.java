package com.iopipe.generic;

/**
 * This class manages and handles translation between from a given class to
 * a given class.
 *
 * @param <F> The class to convert from.
 * @param <T> The class to convert to.
 * @since 2018/08/20
 */
public abstract class ObjectTranslator<F, T>
{
	/** The class to convert from. */
	protected final Class<? extends F> from;
	
	/** The class to convert to. */
	protected final Class<? extends T> to;
	
	/**
	 * Initializes a translator from the given to the given type.
	 *
	 * @param __f The from class.
	 * @param __t The to class.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/08/20
	 */
	private ObjectTranslator(Class<? extends F> __f, Class<? extends T> __t)
		throws NullPointerException
	{
		if (__f == null || __t == null)
			throw new NullPointerException();
		
		this.from = __f;
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
	public abstract T translate(F __f);
	
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
		return this.from.equals(o.from) &&
			this.to.equals(o.to);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/08/20
	 */
	@Override
	public final int hashCode()
	{
		return this.from.hashCode() ^ this.to.hashCode();
	}
	
	/**
	 * Translates from the given object to the destination object without
	 * using any compile time safety.
	 *
	 * @param __f The object to translate, if this is null then no object
	 * is translated and null is returned.
	 * @return The result of the translation, if {@code __f} is null then this
	 * will return null.
	 * @since 2018/08/20
	 */
	@SuppressWarnings({"unchecked"})
	public final T translateObject(Object __f)
	{
		return this.<T>translate((F)__f);
	}
	
	/**
	 * Creates a translator for converting from the input class to the output
	 * class.
	 *
	 * @param <F> The from class.
	 * @param <T> The to class.
	 * @param __f The from class.
	 * @param __t The to class.
	 * @return A translator to translate from one class to the other.
	 * @since 2018/08/20
	 */
	public static final <F, T> ObjectTranslator<F, T> translator(
		Class<? extends F> __f, Class<? extends T> __t)
		throws NullPointerException
	{
		if (__f == null || __t == null)
			throw new NullPointerException();
		
		// If F can be assigned to T then no translation is needed, it is just
		// a cast
		if (__t.isAssignableFrom(__f))
			return new __SimpleCast__<F, T>(__f, __t);
		
		throw new Error("TODO");
	}
	
	/**
	 * This is a translator which is a simple cast between two given classes.
	 *
	 * @since 2018/08/21
	 */
	private static final class __SimpleCast__<F, T>
		extends ObjectTranslator<F, T>
	{
		/**
		 * Initializes the translator.
		 *
		 * @param __f The from class.
		 * @param __t The to class.
		 * @since 2018/08/21
		 */
		private __SimpleCast__(Class<? extends F> __f, Class<? extends T> __t)
		{
			super(__f, __t);
		}
	
		/**
		 * {@inheritDoc}
		 * @since 2018/08/21
		 */
		@Override
		public final T translate(F __f)
		{
			return this.to.cast(__f);
		}
	}
}

