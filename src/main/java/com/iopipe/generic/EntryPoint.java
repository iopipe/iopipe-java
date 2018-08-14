package com.iopipe.generic;

import com.iopipe.IOpipeFatalError;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

/**
 * This represents an entry point to another method.
 *
 * @since 2018/08/13
 */
public final class EntryPoint
{
	/** The class the entry point is in. */
	protected final Class<?> inclass;
	
	/** The method handle for execution. */
	protected final MethodHandle handle;
	
	/**
	 * Initializes the entry point.
	 *
	 * @param __cl The class to look within.
	 * @param __m The name of the method to load.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/08/13
	 */
	public EntryPoint(Class<?> __cl, String __m)
		throws NullPointerException
	{
		if (__cl == null || __m == null)
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
		return this.handle;
	}
	
	/**
	 * Returns the default entry point.
	 *
	 * @return The default entry point.
	 * @since 2018/08/13
	 */
	public static final EntryPoint defaultEntryPoint()
	{
		// For now since only AWS is supported detect the entry point for AWS
		return EntryPoint.__awsEntryPoint();
	}
	
	/**
	 * Returns the entry that would be used for AWS services.
	 *
	 * @return The entry point for AWS method.
	 * @since 2018/08/14
	 */
	private static final EntryPoint __awsEntryPoint()
	{
		// This variable is very important
		String pair = System.getenv("IOPIPE_GENERIC_HANDLER");
		if (pair == null)
			throw new IOpipeFatalError("The environment variable " +
				"IOPIPE_GENERIC_HANDLER has not been set, execution cannot" +
				"continue.");
		
		try
		{
			// Only a class is specified
			int dx = pair.indexOf("::");
			if (dx < 0)
				return new EntryPoint(Class.forName(pair), "handleRequest");
			
			// Class and method
			else
				return new EntryPoint(Class.forName(pair.substring(0, dx)),
					pair.substring(dx + 2));
		}
		catch (ClassNotFoundException e)
		{
			throw new IOpipeFatalError("The environment variable " +
				"IOPIPE_GENERIC_HANDLER is set to a class which does not " +
				"exist. (" + pair + ")", e);
		}
	}
}

