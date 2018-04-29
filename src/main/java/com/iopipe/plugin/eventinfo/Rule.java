package com.iopipe.plugin.eventinfo;

import java.util.function.Function;
import java.util.Optional;

/**
 * This contains a rule which is used to describe a key along with a means
 * of obtaining the value of it.
 *
 * @since 2018/04/29
 */
public final class Rule
{
	/** Is this rule required? */
	protected final boolean isrequired;
	
	/** The associated key. */
	protected final String key;
	
	/** The getter for the value. */
	protected final Function<Object, Object> getter;
	
	/**
	 * Initializes the rule.
	 *
	 * @param __req Is this required?
	 * @param __key The name of the key.
	 * @param __get The getter for the value of the key.
	 * @throws NullPointerException On null arguments.
	 */
	public Rule(boolean __req, String __key,
		Function<Object, Object> __get)
		throws NullPointerException
	{
		if (__key == null || __get == null)
			throw new NullPointerException();
		
		this.isrequired = __req;
		this.key = __key;
		this.getter = __get;
	}
	
	/**
	 * Returns a getter to read the value from the rule.
	 *
	 * @return The function to obtain the value.
	 * @since 2018/04/29
	 */
	public final Function<Object, Object> getter()
	{
		return this.getter;
	}
	
	/**
	 * Is this rule required?
	 *
	 * @return If this is required or not.
	 * @since 2018/04/29
	 */
	public final boolean isRequired()
	{
		return this.isrequired;
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
	 * Returns the input array.
	 *
	 * @param __r The input rule set.
	 * @return {@code __r}.
	 * @since 2018/04/29
	 */
	public static Rule[] rules(Rule... __r)
	{
		return (__r == null ? new Rule[0] : __r);
	}
}

