package com.iopipe.generic;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.function.ToIntFunction;
import java.util.List;

/**
 * This is a conversion which adapts from one list or array type to another.
 *
 * @since 2018/08/27
 */
final class __ListyConvert__
	extends ObjectTranslator
{
	/** Function to read the from length. */
	protected final __FromLength__ fromlengthfunc;
	
	/** Function to read from an index on the source object. */
	protected final __FromGetter__ fromgetfunc;
	
	/** Function to create a new target type. */
	protected final __ToNew__ tonewfunc;
	
	/** Function to add or set the given target index. */
	protected final __ToSetOrAdd__ tosetoraddfunc;
	
	/** The converter used for element types. */
	protected final ObjectTranslator elementconverter;
	
	/**
	 * Initializes the translator.
	 *
	 * @param __f The from type.
	 * @param __t The to type.
	 * @param __elem The element translator.
	 * @throws NullPointerException On no converter was specified.
	 * @since 2018/08/27
	 */
	__ListyConvert__(Type __f, Type __t, ObjectTranslator __elem)
		throws NullPointerException
	{
		super(__f, __t);
		
		if (__elem == null)
			throw new NullPointerException();
		
		this.elementconverter = __elem;
		
		// Reading from array
		if (__f instanceof GenericArrayType ||
			((__f instanceof Class) && ((Class<?>)__f).isArray()))
		{
			this.fromlengthfunc = (__l) -> ((Object[])__l).length;
			this.fromgetfunc = (__l, __i) -> ((Object[])__l)[__i];
		}
		
		// Reading from list
		else
		{
			this.fromlengthfunc = (__l) -> ((List<?>)__l).size();
			this.fromgetfunc = (__l, __i) -> ((List<?>)__l).get(__i);
		}
		
		// Writing to array
		if (__t instanceof GenericArrayType ||
			((__t instanceof Class) && ((Class<?>)__f).isArray()))
		{
			// Need to figure out the array type first
			Class<?> newtype = ObjectTranslator.__rawClass(__t);
			
			this.tonewfunc = (__l) -> Array.newInstance(newtype, __l);
			this.tosetoraddfunc = (__l, __i, __v) -> ((Object[])__l)[__i] = __v;
		}
		
		// Writing to list
		else
		{
			this.tonewfunc = (__l) -> new ArrayList<Object>(__l);
			this.tosetoraddfunc = (__l, __i, __v) -> ((List<Object>)__l).add(__i, __v);
		}
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
		
		// Get source length
		int len = this.fromlengthfunc.length(__f);
		
		// Create target object
		Object rv = this.tonewfunc.createNew(len);
		
		// Copy all elements over
		__FromGetter__ fromgetfunc = this.fromgetfunc;
		__ToSetOrAdd__ tosetoraddfunc = this.tosetoraddfunc;
		ObjectTranslator elementconverter = this.elementconverter;
		for (int i = 0; i < len; i++)
			tosetoraddfunc.setOrAdd(rv, i,
				elementconverter.translate(fromgetfunc.get(__f, i)));
		
		return rv;
	}
	
	/**
	 * Interface used to get from the source list.
	 *
	 * @since 2018/08/27
	 */
	@FunctionalInterface
	static interface __FromGetter__
	{
		/**
		 * Obtains a value from the specified position.
		 *
		 * @param __l The input object.
		 * @param __i The index.
		 * @return The value.
		 * @since 2018/08/27
		 */
		public abstract Object get(Object __l, int __i);
	}
	
	/**
	 * Interface to get the length of the source list.
	 *
	 * @since 2018/08/27
	 */
	@FunctionalInterface
	static interface __FromLength__
	{
		/**
		 * Obtains the length from the given list.
		 *
		 * @param __l The input list.
		 * @return The length.
		 * @since 2018/08/27
		 */
		public abstract int length(Object __l);
	}
	
	/**
	 * Creates a new list instance with the given length.
	 *
	 * @since 2018/08/27
	 */
	@FunctionalInterface
	static interface __ToNew__
	{
		/**
		 * Creates a new instance.
		 *
		 * @param __l The length to use.
		 * @return The new object.
		 * @since 2018/08/27
		 */
		public abstract Object createNew(int __l);
	}
	
	/**
	 * Sets or adds the given index in the given list.
	 *
	 * @since 2018/08/27
	 */
	@FunctionalInterface
	static interface __ToSetOrAdd__
	{
		/**
		 * Sets or adds the given index.
		 *
		 * @param __l The list to add to.
		 * @param __i The index of the element.
		 * @param __v The value to set.
		 * @since 2018/08/27
		 */
		public abstract void setOrAdd(Object __l, int __i, Object __v);
	}
}

