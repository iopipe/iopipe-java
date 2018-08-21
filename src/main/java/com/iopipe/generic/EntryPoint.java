package com.iopipe.generic;

import com.iopipe.IOpipeFatalError;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

/**
 * This represents an entry point to another method.
 *
 * @since 2018/08/13
 */
public final class EntryPoint
{
	/** The class the entry point is in. */
	protected final Class<?> inclass;
	
	/** The base method handle for execution. */
	protected final MethodHandle handle;
	
	/** Is this a static method? */
	protected final boolean isstatic;
	
	/** Parameters to the method. */
	private final Type[] _parameters;
	
	/**
	 * Initializes the entry point.
	 *
	 * @param __cl The class to look within.
	 * @param __m The name of the method to load.
	 * @throws InvalidEntryPointException If the entry point is not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/08/13
	 */
	public EntryPoint(Class<?> __cl, String __m)
		throws InvalidEntryPointException, NullPointerException
	{
		if (__cl == null || __m == null)
			throw new NullPointerException();
		
		// Look in this class for the given named method
		Method used = null;
__outer:
		for (Class<?> look = __cl; look != null; look = look.getSuperclass())
			for (Method m : look.getDeclaredMethods())
				if (__m.equals(m.getName()))
				{
					used = m;
					break;
				}
		
		// No method found
		if (used == null)
			throw new InvalidEntryPointException("The entry point " + __m +
				" in class " + __cl + " is not valid.");
		
		// Allow us to call this method without performing any access checks
		boolean access = used.isAccessible();
		if (!access)
			try
			{
				used.setAccessible(true);
			}
			catch (SecurityException e)
			{
			}
		
		// Get method handle from it, assuming the previous call worked
		try
		{
			this.handle = MethodHandles.lookup().unreflect(used);
		}
		catch (IllegalAccessException e)
		{
			throw new InvalidEntryPointException("Could not access the " +
				"generic entry point method.", e);
		}
		
		// If this was not accessible, then we would have tried to make it so
		// so just revert access to it
		if (!access)
			try
			{
				used.setAccessible(false);
			}
			catch (SecurityException e)
			{
			}
		
		this.inclass = __cl;
		this.isstatic = ((used.getModifiers() & Modifier.STATIC) != 0);
		this._parameters = used.getGenericParameterTypes();
	}
	
	/**
	 * Returns the method handle for the invocation.
	 *
	 * @param __instance The instance to call on, for the first argument. If
	 * the entry point is static then this is ignored.
	 * @return The method handle for the invocation.
	 * @since 2018/08/13
	 */
	public final MethodHandle handle(Object __instance)
	{
		MethodHandle rv = this.handle;
		
		// Bind to the instance if this is not static so that calling the
		// handle only involves the method arguments and does not require a
		// the code using the handle to check if it is static
		if (!this.isstatic)
			return rv.bindTo(__instance);
		
		return rv;
	}
	
	/**
	 * Returns a method handle with a new instance of the current entry point
	 * if it is non-static.
	 *
	 * @return The method handle with the new instance.
	 * @since 2018/08/16
	 */
	public final MethodHandle handleWithNewInstance()
	{
		if (this.isstatic)
			return this.handle(null);
		
		return this.handle(this.newInstance());
	}
	
	/**
	 * Is this method handle static?
	 *
	 * @return If this method handle is static.
	 * @since 2018/08/13
	 */
	public final boolean isStatic()
	{
		return this.isstatic;
	}
	
	/**
	 * Create a new instance of the object for entry.
	 *
	 * @return The new object.
	 * @throws InvalidEntryPointException If the entry point is not valid.
	 * @throws IllegalStateException If the entry point is static, which means
	 * that no instance is initialized.
	 * @since 2018/08/14
	 */
	public final Object newInstance()
		throws InvalidEntryPointException, IllegalStateException
	{
		if (this.isstatic)
			throw new IllegalStateException("Entry point is static, an " +
				"instance cannot be created.");
		
		// Get the default constructor
		Constructor used;
		try
		{
			used = this.inclass.getConstructor();
		}
		catch (NoSuchMethodException e)
		{
			// Failed to get that constructor so try to get one that was
			// declared
			try
			{
				used = this.inclass.getDeclaredConstructor();
			}
			catch (NoSuchMethodException f)
			{
				throw new InvalidEntryPointException("Could not obtain the " +
					"constructor for the entry point.", f);
			}
		}
		
		// Need to make this constructor visible
		boolean access = used.isAccessible();
		if (!access)
			try
			{
				used.setAccessible(true);
			}
			catch (SecurityException e)
			{
			}
		
		// Creating an instance may fail
		try
		{
			return used.newInstance();
		}
		
		// Constructor threw exception, so unwrap it
		catch (InvocationTargetException e)
		{
			Throwable c = e.getCause();
			if (c instanceof RuntimeException)
				throw (RuntimeException)c;
			else if (c instanceof Error)
				throw (Error)c;
			else
				throw new RuntimeException("Constructor threw checked " +
					"exception.", c);
		}
		
		// Failed to initialize it somehow
		catch (IllegalAccessException|IllegalArgumentException|
			InstantiationException e)
		{
			throw new InvalidEntryPointException("Could not construct an " +
				"instance of the entry point class.", e);
		}
		
		// Always try to revert the accessible state of the constructor so
		// it does not remain accessible if it was not
		finally
		{
			if (!access)
				try
				{
					used.setAccessible(false);
				}
				catch (SecurityException e)
				{
				}
		}
	}
	
	/**
	 * Returns the parameters for the entry type.
	 *
	 * @return The parameters for the method call.
	 * @since 2018/08/21
	 */
	public final Type[] parameters()
	{
		return this._parameters.clone();
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
			throw new InvalidEntryPointException("The environment variable " +
				"IOPIPE_GENERIC_HANDLER has not been set, execution cannot " +
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
			throw new InvalidEntryPointException("The environment variable " +
				"IOPIPE_GENERIC_HANDLER is set to a class which does not " +
				"exist. (" + pair + ")", e);
		}
	}
}

