package com.iopipe;

/**
 * This represents the base of a value which is used to store something that
 * is set.
 *
 * @since 2018/01/24
 */
public abstract class Value
{
	/** The name of the value. */
	protected final String name;
	
	/**
	 * Initializes the storage.
	 *
	 * @param __n The variable name.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/24
	 */
	public Value(String __n)
		throws NullPointerException
	{
		if (__n == null)
			throw new NullPointerException();
		
		this.name = __n;
	}
	
	/**
	 * Returns the variable name.
	 *
	 * @return The variable name.
	 * @since 2018/01/24
	 */
	public final String name()
	{
		return this.name;
	}
}
