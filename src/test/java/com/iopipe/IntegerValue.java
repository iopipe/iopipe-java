package com.iopipe;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents an integer value.
 *
 * @since 2018/01/26
 */
public final class IntegerValue
	extends Value
{
	/** The internal value. */
	protected final AtomicInteger value =
		new AtomicInteger();
	
	/**
	 * Initializes the storage.
	 *
	 * @param __n The variable name.
	 * @since 2018/01/26
	 */
	public IntegerValue(String __n)
	{
		super(__n);
	}
	
	/**
	 * Obtains the value.
	 *
	 * @return The value.
	 * @since 2018/01/26
	 */
	public int get()
	{
		return this.value.get();
	}
	
	/**
	 * Increments the value then returns it.
	 *
	 * @return The value after incrementing.
	 * @since 2018/01/26
	 */
	public int incrementAndGet()
	{
		return this.value.incrementAndGet();
	}
	
	/**
	 * Sets the value.
	 *
	 * @param __v The new value.
	 * @since 2018/01/26
	 */
	public void set(int __v)
	{
		this.value.set(__v);
	}
}

