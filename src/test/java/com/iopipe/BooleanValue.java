package com.iopipe;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents a boolean value.
 *
 * @since 2018/01/24
 */
public final class BooleanValue
	extends Value
{
	/** The internal value. */
	protected final AtomicBoolean value =
		new AtomicBoolean();
	
	/**
	 * Initializes the storage.
	 *
	 * @param __n The variable name.
	 * @since 2018/01/24
	 */
	public BooleanValue(String __n)
	{
		super(__n);
	}
	
	/**
	 * Obtains the value.
	 *
	 * @return The value.
	 * @since 2018/01/24
	 */
	public boolean get()
	{
		return this.value.get();
	}
	
	/**
	 * Sets the value.
	 *
	 * @param __v The new value.
	 * @sicne 2018/01/24
	 */
	public void set(boolean __v)
	{
		this.value.set(__v);
	}
}
