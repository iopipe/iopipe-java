package com.iopipe.generic;

import com.amazonaws.services.lambda.runtime.Context;
import com.iopipe.IOpipeExecution;
import com.iopipe.IOpipeFatalError;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

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
	 * @param __cl The used class.
	 * @param __h The handle to execute.
	 * @param __static Is this static?
	 * @param __parmeters Parameters to the entry point.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/08/13
	 */
	private EntryPoint(Class<?> __cl, MethodHandle __h, boolean __static,
		Type[] __parameters)
		throws InvalidEntryPointException, NullPointerException
	{
		if (__cl == null || __h == null || __parameters == null)
			throw new NullPointerException();
		
		this.inclass = __cl;
		this.handle = __h;
		this.isstatic = __static;
		this._parameters = __parameters.clone();
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
	 * Returns the entry that would be used for AWS services.
	 *
	 * @return The entry point for AWS method.
	 * @since 2018/08/14
	 */
	public static final EntryPoint defaultAWSEntryPoint()
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
				return EntryPoint.newAWSEntryPoint(Class.forName(pair),
					"handleRequest");
			
			// Class and method
			else
				return EntryPoint.newAWSEntryPoint(
					Class.forName(pair.substring(0, dx)),
					pair.substring(dx + 2));
		}
		catch (ClassNotFoundException e)
		{
			throw new InvalidEntryPointException("The environment variable " +
				"IOPIPE_GENERIC_HANDLER is set to a class which does not " +
				"exist. (" + pair + ")", e);
		}
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
		return EntryPoint.defaultAWSEntryPoint();
	}
	
	/**
	 * Initializes an entry point which is valid for Amazon AWS.
	 *
	 * @param __cl The class to call into.
	 * @param __m The method to be executed.
	 * @throws InvalidEntryPointException If the entry point is not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/08/03
	 */
	public static final EntryPoint newAWSEntryPoint(Class<?> __cl, String __m)
		throws InvalidEntryPointException, NullPointerException
	{
		if (__cl == null || __m == null)
			throw new NullPointerException();
		
		// There may be multiple type of entry points that are available
		// 0: ()
		// 1: (A)
		// 2: (A, Context)
		// 3: (I, O)
		// 4: (I, O, Context)
		// 5: (IOpipeExecution, A)
		// 6: (IOpipeExecution, I, O)
		__Target__ target = null;
		int discovered;
		for (discovered = 6; discovered >= 0; discovered--)
		{
			switch (discovered)
			{
				case 0:
					target = __Target__.__locate(__cl, __m, Object.class);
					break;
					
				case 1:
					target = __Target__.__locate(__cl, __m, Object.class,
						Object.class);
					break;
					
				case 2:
					target = __Target__.__locate(__cl, __m, Object.class,
						Object.class, Context.class);
					break;
					
				case 3:
					target = __Target__.__locate(__cl, __m, void.class,
						InputStream.class, OutputStream.class);
					break;
					
				case 4:
					target = __Target__.__locate(__cl, __m, void.class,
						InputStream.class, OutputStream.class, Context.class);
					break;
					
				case 5:
					target = __Target__.__locate(__cl, __m, Object.class,
						IOpipeExecution.class, Object.class);
					break;
					
				case 6:
					target = __Target__.__locate(__cl, __m, void.class,
						IOpipeExecution.class, InputStream.class,
						OutputStream.class);
					break;
			}
			
			// Found target
			if (target != null)
				break;
		}
		
		// One was not found
		if (discovered < 0 || target == null)
			throw new InvalidEntryPointException("The entry point " + __m +
				" in class " + __cl + " is not valid, no method was found.");
		
		// Extract all the details in the method to rebuild it
		Type[] parms = target.arguments;
		int pn = parms.length;
		Type pa = (pn > 0 ? parms[0] : null),
			pb = (pn > 1 ? parms[1] : null),
			pc = (pn > 2 ? parms[2] : null);
		
		// Always normalize parameters to either be a stream type or non-stream
		// type, with a context
		Type[] passparameters;
		switch (discovered)
		{
				// Parameter and context
			case 0:
			case 1:
			case 2:
				passparameters = new Type[]
					{
						(pa != null ? pa : Object.class),
						Context.class,
					};
				break;
				
				// Starts from second argument
			case 5:
				passparameters = new Type[]
					{
						(pb != null ? pb : Object.class),
						Context.class,
					};
				break;
			
				// Input and output streams
			case 3:
			case 4:
				passparameters = new Type[]
					{
						(pa != null ? pa : InputStream.class),
						(pb != null ? pb : OutputStream.class),
						Context.class,
					};
				break;
				
				// Starts from second argument
			case 6:
				passparameters = new Type[]
					{
						(pb != null ? pb : InputStream.class),
						(pc != null ? pc : OutputStream.class),
						Context.class,
					};
				break;
			
				// This indicates the code is wrong
			default:
				throw new Error("If this has happened then something is " +
					"very wrong.");
		}
		
		// Static determines if we use an extra parameter to wrap or not
		boolean isstatic = target.isstatic;
		
		// Build a compatible method handle and parameter set
		MethodHandle basehandle = target.basehandle;
		MethodHandle usedhandle;
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		try
		{
			switch (discovered)
			{
					// 0: ()
				case 0:
					if (isstatic)
						usedhandle = lookup.findStatic(__AWSAdapters__.class,
							"__type0Static", MethodType.methodType(
								Object.class, MethodHandle.class, Object.class,
								Context.class)).bindTo(basehandle);
					else
						usedhandle = lookup.findStatic(__AWSAdapters__.class,
							"__type0Instance", MethodType.methodType(
								Object.class, MethodHandle.class, Object.class,
								Object.class, Context.class)).bindTo(basehandle);
					break;
				
					// 1: (A)
				case 1:
					if (isstatic)
						usedhandle = lookup.findStatic(__AWSAdapters__.class,
							"__type1Static", MethodType.methodType(
								Object.class, MethodHandle.class, Object.class,
								Context.class)).bindTo(basehandle);
					else
						usedhandle = lookup.findStatic(__AWSAdapters__.class,
							"__type1Instance", MethodType.methodType(
								Object.class, MethodHandle.class, Object.class,
								Object.class, Context.class)).bindTo(basehandle);
					break;
				
					// 2: (A, Context), identity handler
				case 2:
					usedhandle = basehandle;
					break;
					
					// 3: (I, O)
				case 3:
					if (isstatic)
						usedhandle = lookup.findStatic(__AWSAdapters__.class,
							"__type3Static", MethodType.methodType(
								void.class, MethodHandle.class, InputStream.class,
								OutputStream.class, Context.class)).bindTo(basehandle);
					else
						usedhandle = lookup.findStatic(__AWSAdapters__.class,
							"__type3Instance", MethodType.methodType(
								void.class, MethodHandle.class, Object.class,
								InputStream.class, OutputStream.class,
								Context.class)).bindTo(basehandle);
					break;
					
					// 4: (I, O, Context), identity handler
				case 4:
					usedhandle = basehandle;
					break;
					
					// 5: (IOpipeExecution, A)
				case 5:
					if (isstatic)
						usedhandle = lookup.findStatic(__AWSAdapters__.class,
							"__type5Static", MethodType.methodType(
								Object.class, MethodHandle.class, Object.class,
								Context.class)).bindTo(basehandle);
					else
						usedhandle = lookup.findStatic(__AWSAdapters__.class,
							"__type5Instance", MethodType.methodType(
								Object.class, MethodHandle.class, Object.class,
								Object.class, Context.class)).bindTo(basehandle);
					break;
					
					// 6: (IOpipeExecution, I, O)
				case 6:
					if (isstatic)
						usedhandle = lookup.findStatic(__AWSAdapters__.class,
							"__type6Static", MethodType.methodType(
								void.class, MethodHandle.class, InputStream.class,
								OutputStream.class, Context.class)).bindTo(basehandle);
					else
						usedhandle = lookup.findStatic(__AWSAdapters__.class,
							"__type6Instance", MethodType.methodType(
								void.class, MethodHandle.class, Object.class,
								InputStream.class, OutputStream.class,
								Context.class)).bindTo(basehandle);
					break;
				
					// This indicates that the code is wrong
				default:
					throw new Error("If this has happened then something is " +
						"very wrong.");
			}
		}
		
		// If this happens this is fatal and the code is wrong
		catch (IllegalAccessException|NoSuchMethodException e)
		{
			throw new Error("Could not locate the AWS handle wrappers.", e);
		}
		
		return new EntryPoint(__cl, usedhandle, isstatic, passparameters);
	}
	
	/**
	 * This class stores information on a target method which was discovered
	 * in a class.
	 *
	 * @since 2018/09/04
	 */
	private static final class __Target__
	{
		/** The base method handle. */
		protected final MethodHandle basehandle;
		
		/** Arguments to the target. */
		protected final Type[] arguments;
		
		/** Is the method static? */
		protected final boolean isstatic;
		
		/**
		 * Initializes the target information.
		 *
		 * @param __bh The base handle.
		 * @param __args Arguments to the target.
		 * @param __s Is the method static?
		 * @throws NullPointerException On null arguments.
		 * @since 2018/09/04
		 */
		private __Target__(MethodHandle __bh, Type[] __args, boolean __s)
			throws NullPointerException
		{
			if (__bh == null || __args == null)
				throw new NullPointerException();
			
			this.basehandle = __bh;
			this.arguments = __args.clone();
			this.isstatic = __s;
		}
		
		/**
		 * Locates a target for the given class and method.
		 *
		 * @param __cl The class to look in.
		 * @param __name The name of the class.
		 * @param __rv The return value.
		 * @param __args The arguments to look for.
		 * @return The target if one has matched or {@code null} if none has
		 * matched.
		 * @throws NullPointerException On null arguments.
		 * @since 2018/09/04
		 */
		static __Target__ __locate(Class<?> __cl, String __name,
			Class<?> __rv, Class<?>... __args)
			throws NullPointerException
		{
			if (__cl == null || __name == null || __rv == null)
				throw new NullPointerException();
			
			// Defensive copy
			__args = (__args == null ? new Class<?>[0] : __args.clone());
			int numargs = __args.length;
			
			// The used method
			Method used = null;
			
			// Stack for class traversal when finding methods
			Deque<Class<?>> clstack = new ArrayDeque<>();
__outerloop:
			for (Class<?> in = __cl; in != null; in = in.getSuperclass())
			{
				// Add our class to the stack, this is used to resolve type
				// parameter as needed
				clstack.push(in);
				
				// Scan for compatible methods with our name
__methodloop:
				for (Method m : in.getDeclaredMethods())
				{
					// This is not what our method is called
					if (!__name.equals(m.getName()))
						continue;
						
					// Ignore abstract methods, they cannot be called
					if ((m.getModifiers() & Modifier.ABSTRACT) != 0)
						continue;
					
					// If the parameter count does not match then it uses some
					// other format
					int pn = m.getParameterCount();
					if (pn != numargs)
						continue;
					
					// They will be checked against the arguments we want,
					// they must all be a assignable to our method (compatible)
					Class<?>[] parms = m.getParameterTypes();
					for (int i = 0; i < pn; i++)
						if (!__args[i].isAssignableFrom(parms[i]))
							continue __methodloop;
					
					// Check return value also
					if (!__rv.isAssignableFrom(m.getReturnType()))
						continue;
					
					// This method has a signature and name match so use that
					used = m;
					break __outerloop;
				}
			}
			
			// No method was found
			if (used == null)
				return null;
			
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
			MethodHandle basehandle;
			try
			{
				basehandle = MethodHandles.lookup().unreflect(used);
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
			
			// Determine the types of arguments this takes, this will require
			// parameterized type resolution as required
			// The original parameters will always be used as the base
			Type[] xargs = used.getGenericParameterTypes();
			for (int i = 0; i < numargs; i++)
			{
				Type t = __Target__.__resolve(clstack, xargs[i]);
				if (t != null)
					xargs[i] = t;
			}
			
			// Initialize target information
			return new __Target__(basehandle, xargs,
				((used.getModifiers() & Modifier.STATIC) != 0));
		}
		
		/**
		 * Resolves the given type variable for a class.
		 *
		 * @param __cls The class stack.
		 * @param __t The type to resolve.
		 * @throws NullPointerException On null arguments.
		 * @since 2018/09/05
		 */
		static Type __resolve(Deque<Class<?>> __cls, Type __t)
			throws NullPointerException
		{
			if (__cls == null || __t == null)
				throw new NullPointerException();
			
			// If it is not a type variable, just return self
			if (!(__t instanceof TypeVariable))
				return __t;
			
			// Make a copy of the stack
			Deque<Class<?>> stackcopy = new ArrayDeque<>(__cls);
			
			TypeVariable tv = (TypeVariable)__t;
			String tvname = tv.getName();
			
			// The class that declares this type parameter was the
			// one the method was found in (it will be at the top)
			Class<?> pivotclass = stackcopy.pop();
			
			// Determine which index our type variable is in, we
			// need to look in the super class
			TypeVariable<?>[] pivotvars = pivotclass.getTypeParameters();
			int pivotdx = -1;
			for (int j = 0, n = pivotvars.length; j < n; j++)
				if (tvname.equals(pivotvars[j].getName()))
				{
					pivotdx = j;
					break;
				}
			
			// This should not happen normally (unless the class
			// was modified to break it or we are trying to initialize
			// a non-static inner class which cannot be initialized
			// anyway)
			if (pivotdx < 0)
				return __t;
			
			// Peek the current class
			Class<?> upperclass = stackcopy.peek();
			
			// Initializing a class which is just a type variable
			if (upperclass == null)
				return __t;
			
			// Need to go through all types and determine what this
			// is
			Type[] genints = upperclass.getGenericInterfaces();
			for (int j = 0, n = genints.length; j <= n; j++)
			{
				Type maybe = (j == 0 ?
					upperclass.getGenericSuperclass() :
					genints[j - 1]);
				
				if (!(maybe instanceof ParameterizedType))
					continue;
				
				throw new Error("TODO A");
			}
			
			throw new Error("TODO B");
		}
	}
}

