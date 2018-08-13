package com.iopipe.generic;

import java.lang.invoke.MethodHandle;

/**
 * This represents an entry point to another method.
 *
 * @since 2018/08/13
 */
public final class EntryPoint
{
	/**
	 * Initializes the entry point.
	 *
	 * @param __s The entry point to refer to.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/08/13
	 */
	public EntryPoint(String __s)
		throws NullPointerException
	{
		if (__s == null)
			throw new NullPointerException();
		
		throw new Error("TODO");
	}
	
	/**
	 * Returns the method handle for the invocation.
	 *
	 * @return The method handle for the invocation.
	 * @since 2018/08/13
	 */
	public final MethodHandle handle()
	{
		throw new Error("TODO");
	}
	
	/**
	 * Returns the default entry point.
	 *
	 * @return The default entry point.
	 * @since 2018/08/13
	 */
	public static final EntryPoint defaultEntryPoint()
	{
		throw new Error("TODO");
	}
}

