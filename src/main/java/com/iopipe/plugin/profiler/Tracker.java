package com.iopipe.plugin.profiler;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class keeps track of executions which have occured.
 *
 * @since 2018/02/12
 */
public final class Tracker
{
	/** Method tracker. */
	protected final MethodTracker methods =
		new MethodTracker();
	
	/** Thread recordings. */
	private final Map<Thread, TrackedThread> _threads =
		new HashMap<>();
	
	/**
	 * Returns the methods which have been tracked.
	 *
	 * @return The tracked methods.
	 * @since 2018/02/19
	 */
	public MethodTracker methods()
	{
		return this.methods;
	}
	
	/**
	 * Parses and keeps track of the specified stack trace.
	 *
	 * @param __abs The absolute time since the start of execution in
	 * nanoseconds.
	 * @param __rel The relative time since the last trace.
	 * @param __thread The thread which was traced.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/02/12
	 */
	public final void parseStackTrace(long __abs, int __rel, Thread __thread)
		throws NullPointerException
	{
		if (__thread == null)
			throw new NullPointerException();
		
		TrackedThread stat;
		
		// In the future recording traces could be done in multiple threads
		Map<Thread, TrackedThread> threads = this._threads;
		synchronized (threads)
		{
			stat = threads.get(__thread);
			if (stat == null)
				threads.put(__thread, (stat = new TrackedThread(__thread,
					threads.size(), this.methods)));
		}
		
		// Record thread information
		stat.parseStackTrace(__abs, __rel);
	}
	
	/**
	 * Returns the state information for each thread.
	 *
	 * @return The thread information.
	 * @since 2018/02/19
	 */
	public final TrackedThread[] threads()
	{
		Map<Thread, TrackedThread> threads = this._threads;
		synchronized (threads)
		{
			Collection<TrackedThread> values = threads.values();
			return values.<TrackedThread>toArray(new TrackedThread[values.size()]);
		}
	}
}

