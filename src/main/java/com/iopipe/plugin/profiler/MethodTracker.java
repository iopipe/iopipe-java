package com.iopipe.plugin.profiler;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used to keep track of methods which have executed along with
 * their unique identifiers.
 *
 * @since 2018/02/19
 */
public final class MethodTracker
{
	/** Classes which have been tracked. */
	private final Map<String, TrackedClass> _classes =
		new HashMap<>();
	
	/** Methods which have been tracked by their linear index. */
	private final List<TrackedMethod> _methods =
		new ArrayList<>();
	
	/** The identifier of the next method to track. */
	private final AtomicInteger _nextid =
		new AtomicInteger();
	
	/**
	 * Returns the methods which have been tracked.
	 *
	 * @return The tracked methods.
	 * @since 2018/02/19
	 */
	public final MethodTracker.TrackedMethod[] methods()
	{
		List<TrackedMethod> methods = this._methods;
		synchronized (methods)
		{
			return methods.<TrackedMethod>toArray(
				new TrackedMethod[methods.size()]);
		}
	}
	
	/**
	 * Tracks the given method according to the stack trace element.
	 *
	 * @param __e The element to track.
	 * @return The method for the given track.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/02/19
	 */
	public MethodTracker.TrackedMethod track(StackTraceElement __e)
		throws NullPointerException
	{
		if (__e == null)
			throw new NullPointerException();
		
		return this.track(__e.getClassName(), __e.getMethodName());
	}
	
	/**
	 * Tracks the given class name and method.
	 *
	 * @param __c The class to track.
	 * @param __m The method to track.
	 * @return The tracked method.
	 * @since 2018/02/19
	 */
	public MethodTracker.TrackedMethod track(String __c, String __m)
	{
		TrackedClass cl;
		
		Map<String, TrackedClass> classes = this._classes;
		synchronized (classes)
		{
			cl = classes.get(__c);
			if (cl == null)
				classes.put(__c, (cl =
					new TrackedClass(__c, this._methods, this._nextid)));
		}
		
		return cl.track(__m);
	}
	
	/**
	 * A class which has been tracked.
	 *
	 * @since 2018/02/19
	 */
	public static final class TrackedClass
	{
		/** The class name. */
		protected final String name;
		
		/** Methods which have been tracked. */
		private final Map<String, TrackedMethod> _methods =
			new HashMap<>();
		
		/** Linear methods being tracked. */
		private final List<TrackedMethod> _linear;
		
		/** The identifier of the next method to track. */
		private final AtomicInteger _nextid;
		
		/**
		 * Initializes the class tracker.
		 *
		 * @param __s The name for this class.
		 * @param __l Linear method list.
		 * @param __i The next index to track.
		 * @throws NullPointerException If no next index value was specified
		 * or no linear method list was specified.
		 * @since 2018/02/19
		 */
		private TrackedClass(String __s, List<TrackedMethod> __l,
			AtomicInteger __i)
			throws NullPointerException
		{
			if (__l == null || __i == null)
				throw new NullPointerException();
			
			this.name = __s;
			this._linear = __l;
			this._nextid = __i;
		}
		
		/**
		 * Tracks the specified method.
		 *
		 * @param __m The method to track.
		 * @return The tracker for the given method.
		 * @since 2018/02/19
		 */
		public final MethodTracker.TrackedMethod track(String __m)
		{
			Map<String, TrackedMethod> methods = this._methods;
			synchronized (methods)
			{
				TrackedMethod rv = methods.get(__m);
				
				if (rv == null)
				{
					// Need to also keep track of methods linearly for easy
					// access
					List<TrackedMethod> linear = this._linear;
					synchronized (linear)
					{
						methods.put(__m, (rv = new TrackedMethod(this.name,
							__m, this._nextid.getAndIncrement())));
						linear.add(rv);
					}
				}
				
				return rv;
			}
		}
	}
	
	/**
	 * A method which has been tracked.
	 *
	 * @since 2018/02/19
	 */
	public static final class TrackedMethod
	{
		/** The class for this method. */
		protected final String classname;
		
		/** The name of the method. */
		protected final String methodname;
		
		/** The ID of this method. */
		protected final int index;
		
		/**
		 * Initializes the tracked method.
		 *
		 * @param __c The containing class.
		 * @param __m The method this is within.
		 * @param __id The index of this method in the global table.
		 * @since 2018/02/19
		 */
		private TrackedMethod(String __c, String __m, int __id)
		{
			this.classname = __c;
			this.methodname = __m;
			this.index = __id;
		}
		
		/**
		 * Returns the name of the class.
		 *
		 * @return The class name.
		 * @since 2018/02/19
		 */
		public final String className()
		{
			return this.classname;
		}
		
		/**
		 * Returns the index of the method.
		 *
		 * @return The method index.
		 * @since 2018/02/19
		 */
		public final int index()
		{
			return this.index;
		}
		
		/**
		 * Returns the name of the method.
		 *
		 * @return The method name.
		 * @since 2018/02/19
		 */
		public final String methodName()
		{
			return this.methodname;
		}
	}
}

