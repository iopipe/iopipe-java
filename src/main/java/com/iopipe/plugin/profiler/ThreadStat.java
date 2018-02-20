package com.iopipe.plugin.profiler;

import java.util.Objects;

/**
 * This contains the information for a single thread which has been profiled.
 *
 * @since 2018/02/19
 */
public final class ThreadStat
{
	/** The thread to monitor. */
	protected final Thread thread;
	
	/** Currently tracked methods. */
	protected final MethodTracker methods;
	
	/** The logical thread index. */
	protected final int logicalindex;
	
	/** The name of the thread. */
	protected final String name;
	
	/** Time spent running the thread in whole time. */
	private volatile long _wgtime;
	
	/**
	 * Initializes the thread information.
	 *
	 * @param __thread The thread to record information for.
	 * @param __ldx Logical thread index.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/02/19
	 */
	public ThreadStat(Thread __thread, int __ldx, MethodTracker __m)
		throws NullPointerException
	{
		if (__thread == null || __m ==null)
			throw new NullPointerException();
		
		this.thread = __thread;
		this.methods = __m;
		this.logicalindex = __ldx;
		this.name = Objects.toString(__thread.getName(), "");
	}
	
	/**
	 * Parses and keeps track of the specified stack trace.
	 *
	 * @param __abs The absolute time since the start of execution in
	 * nanoseconds.
	 * @param __rel The relative time since the last trace.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/02/19
	 */
	public final void parseStackTrace(long __abs, int __rel)
		throws NullPointerException
	{
		Thread thread = this.thread;
		
		// Only count threads which are running, not any which are blocked by
		// a lock or terminated because they consume no CPU time
		Thread.State state = thread.getState();
		if (state != Thread.State.RUNNABLE)
			return;
		
		// Add to whole graph time
		this._wgtime += __rel;
	}
}

