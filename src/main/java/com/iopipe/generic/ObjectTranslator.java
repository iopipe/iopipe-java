package com.iopipe.generic;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

/**
 * This class manages converters to the target type.
 *
 * @since 2018/08/20
 */
public abstract class ObjectTranslator
{
	/** The type to convert from. */
	protected final Type from;
	
	/** The type to convert to. */
	protected final Type to;
	
	/**
	 * Initializes a translator to the given type.
	 *
	 * @param __f The from type.
	 * @param __t The target type
	 * @throws NullPointerException On null arguments.
	 * @since 2018/08/20
	 */
	ObjectTranslator(Type __f, Type __t)
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
		return this.from.equals(o.from) &&
			this.to.equals(o.to);
	}
	
	/**
	 * Returns the from type.
	 *
	 * @return The from type.
	 * @since 2018/08/22
	 */
	public final Type from()
	{
		return this.from;
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
	 * Returns the to type.
	 *
	 * @return The to type.
	 * @since 2018/08/22
	 */
	public final Type to()
	{
		return this.to;
	}
	
	/**
	 * Creates a translator for converting objects to the given type.
	 *
	 * @param __f The from type.
	 * @param __t The to class.
	 * @return A translator to translate from one class to the other.
	 * @since 2018/08/20
	 */
	public static final ObjectTranslator translator(Type __f, Type __t)
		throws NullPointerException
	{
		if (__f == null || __t == null)
			throw new NullPointerException();
		
		// Identity conversion or any type to Object (which does no
		// conversion at all)
		if (__f.equals(__t) || __t.equals(Object.class))
			return new __IdentityConvert__(__f, __t);
		
		// Conversion of string to other type, construct or valueOf
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		if (__f.equals(String.class) && (__t instanceof Class))
		{
			Class<?> tcl = (Class<?>)__t;
			
			// Try valueOf() first
			try
			{
				return new __StringToConvert__(__f, __t,
					lookup.findStatic(tcl, "valueOf",
						MethodType.methodType(tcl, String.class)));
			}
			catch (IllegalAccessException|NoSuchMethodException e)
			{
			}
			
			// Try a constructor
			try
			{
				return new __StringToConvert__(__f, __t,
					lookup.findConstructor(tcl,
						MethodType.methodType(void.class, String.class)));
			}
			catch (IllegalAccessException|NoSuchMethodException e)
			{
			}
		}
		
		// The source is a number type
		if ((__f instanceof Class) && Number.class.isAssignableFrom(((Class<?>)__f)))
		{
			// To int
			if (__t.equals(Integer.class))
				return new __FunctionConvert__(__f, __t,
					(__o) -> Integer.valueOf(((Number)__o).intValue()));
			
			// To long
			else if (__t.equals(Long.class))
				return new __FunctionConvert__(__f, __t,
					(__o) -> Long.valueOf(((Number)__o).longValue()));
			
			// To float
			else if (__t.equals(Float.class))
				return new __FunctionConvert__(__f, __t,
					(__o) -> Float.valueOf(((Number)__o).floatValue()));
			
			// To double
			else if (__t.equals(Double.class))
				return new __FunctionConvert__(__f, __t,
					(__o) -> Double.valueOf(((Number)__o).doubleValue()));
		}
		
		// The source and target are array/list compatible
		Type sl = ObjectTranslator.__getListComponent(__f, true),
			tl = ObjectTranslator.__getListComponent(__t, false);
		if (sl != null && tl != null)
			return new __ListyConvert__(__f, __t,
				ObjectTranslator.translator(sl, tl));
		
		// Could not setup a well known basic conversion so fall back to a
		// generic conversion
		return new __JacksonConvert__(__f, __t);
	}
	
	/**
	 * Returns the type that is used for anything that can contain a list type
	 * or an array.
	 *
	 * @param __t The type to check.
	 * @param __anylist Can any list type be used?
	 * @return If the type is a list or array then this returns the component
	 * type.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/08/27
	 */
	static final Type __getListComponent(Type __t, boolean __anylist)
		throws NullPointerException
	{
		if (__t == null)
			throw new NullPointerException();
		
		// This represents a raw list?
		if (__t instanceof Class)
		{
			Class<?> t = (Class<?>)__t;
			
			// Target is an array, use the component type
			if (t.isArray())
				return t.getComponentType();
			
			// Is kind of list
			else if (__anylist && List.class.isAssignableFrom(t))
				return Object.class;
			
			// Just the base list
			else if (!__anylist && List.class.equals(t))
				return Object.class;
			
			// Not one
			return null;
		}
		
		// Could be a list
		else if (__t instanceof ParameterizedType)
		{
			ParameterizedType t = (ParameterizedType)__t;
			
			// Check if the base raw type is a list
			Class<?> raw = ObjectTranslator.__rawClass(t);
			if (__anylist && !List.class.isAssignableFrom(raw))
				return null;
			else if (!__anylist && !List.class.equals(raw))
				return null;
			
			// Only use the first argument if it is valid
			Type[] args = t.getActualTypeArguments();
			if (args.length == 1)
				return args[0];
			
			// Otherwise do not consider this type
			return null;
		}
		
		// Is an array
		else if (__t instanceof GenericArrayType)
			return ((GenericArrayType)__t).getGenericComponentType();
		
		// Not a list type
		return null;
	}
	
	/**
	 * Returns the raw class.
	 *
	 * @param __t The type to get the raw type from.
	 * @return The raw type
	 * @throw NullPointerException On null arguments.
	 * @since 2018/08/27
	 */
	static final Class<?> __rawClass(Type __t)
		throws NullPointerException
	{
		if (__t == null)
			throw new NullPointerException();
		
		// Just a normal class
		if (__t instanceof Class)
			return (Class<?>)__t;
		
		// Parameterized type
		else if (__t instanceof ParameterizedType)
			return ObjectTranslator.__rawClass(((ParameterizedType)__t).getRawType());
		
		// Unknown, lets just call it object
		return Object.class;
	}
}

