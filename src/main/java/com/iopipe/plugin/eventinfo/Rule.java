package com.iopipe.plugin.eventinfo;

import java.util.function.Function;
import java.util.Optional;

/**
 * This contains a rule which is used to describe a key along with a means
 * of obtaining the value of it.
 *
 * @param <T> The type of class to input.
 * @since 2018/04/29
 */
public final class Rule<T>
{
	/** The associated key. */
	protected final String key;
	
	/** The getter for the value. */
	protected final Function<T, Object> getter;
	
	/**
	 * Initializes the rule.
	 *
	 * @param __req Is this required?
	 * @param __key The name of the key.
	 * @param __get The getter for the value of the key.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/04/29
	 */
	public Rule(String __key, Function<T, Object> __get)
		throws NullPointerException
	{
		if (__key == null || __get == null)
			throw new NullPointerException();
		
		this.key = __key;
		this.getter = __get;
	}
	
	/**
	 * Returns a getter to read the value from the rule.
	 *
	 * @return The function to obtain the value.
	 * @since 2018/04/29
	 */
	public final Function<T, Object> getter()
	{
		return this.getter;
	}
	
	/**
	 * Returns the key this is associated with.
	 *
	 * @return The associated key.
	 * @since 2018/04/29
	 */
	public final String key()
	{
		return this.key;
	}
	
	/**
	 * Casts the given object to an optional type of the given class.
	 *
	 * @param <C> The class to cast to.
	 * @param __cl The class to cast to.
	 * @param __v The value to cast.
	 * @return An optional of the casted value.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/04/30
	 */
	public static <C> Optional<C> cast(Class<C> __cl, Object __v)
		throws NullPointerException
	{
		if (__cl == null)
			throw new NullPointerException("NARG");
		
		return Optional.<C>ofNullable(__cl.cast(__v));
	}
	
	/**
	 * Returns the input array.
	 *
	 * @param __r The input rule set.
	 * @return {@code __r}.
	 * @since 2018/04/29
	 */
	public static Rule<?>[] rules(Rule<?>... __r)
	{
		return (__r == null ? new Rule<?>[0] : __r);
	}
}

