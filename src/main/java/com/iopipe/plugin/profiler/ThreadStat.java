package com.iopipe.plugin.profiler;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This contains the information for a single thread which has been profiled.
 *
 * @since 2018/02/19
 */
public final class ThreadStat
	implements ThreadStatNodeTraversal
{
	/** The thread to monitor. */
	protected final Thread thread;
	
	/** Currently tracked methods. */
	protected final MethodTracker methods;
	
	/** The logical thread index. */
	protected final int logicalindex;
	
	/** The name of the thread. */
	protected final String name;
	
	/** Root nodes for the thread tree. */
	private final Map<MethodTracker.TrackedMethod, Node> _nodes =
		new LinkedHashMap<>();
	
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
		
		// Generate stack trace, since each sub-node based on the origin point
		// of call is unique per stack trace, methods which call other methods
		// even though they may result in the same method will have different
		// timers and such
		StackTraceElement[] trace = thread.getStackTrace();
	}
	
	/** 
	 * {@inheritDoc}
	 * @since 2018/02/19
	 */
	@Override
	public final ThreadStat.Node subNode(MethodTracker.TrackedMethod __m)
		throws NullPointerException
	{
		if (__m == null)
			throw new NullPointerException();
		
		Map<MethodTracker.TrackedMethod, Node> nodes = this._nodes;
		synchronized (nodes)
		{
			Node rv = nodes.get(__m);
			if (rv == null)
				nodes.put(__m, (rv = new Node(__m)));
			return rv;
		}
	}
	
	/** 
	 * {@inheritDoc}
	 * @since 2018/02/19
	 */
	@Override
	public final ThreadStat.Node[] subNodes()
	{
		Map<MethodTracker.TrackedMethod, Node> nodes = this._nodes;
		synchronized (nodes)
		{
			Collection<Node> values = nodes.values();
			return values.<Node>toArray(new Node[values.size()]);
		}
	}
	
	/**
	 * This represents a single node within the trace tree.
	 *
	 * @since 2018/02/19
	 */
	public static final class Node
		implements ThreadStatNodeTraversal
	{
		/** The method being tracked. */
		protected final MethodTracker.TrackedMethod method;
		
		/** Nodes within this tree. */
		private final Map<MethodTracker.TrackedMethod, Node> _nodes =
			new LinkedHashMap<>();
		
		/**
		 * Initializes the node for tracking this method.
		 *
		 * @param __m The method to track.
		 * @throws NullPointerException On null arguments.
		 * @since 2018/02/19
		 */
		private Node(MethodTracker.TrackedMethod __m)
			throws NullPointerException
		{
			if (__m == null)
				throw new NullPointerException();
			
			this.method = __m;
		}
		
		/** 
		 * {@inheritDoc}
		 * @since 2018/02/19
		 */
		@Override
		public final ThreadStat.Node subNode(MethodTracker.TrackedMethod __m)
			throws NullPointerException
		{
			if (__m == null)
				throw new NullPointerException();
		
			Map<MethodTracker.TrackedMethod, Node> nodes = this._nodes;
			synchronized (nodes)
			{
				Node rv = nodes.get(__m);
				if (rv == null)
					nodes.put(__m, (rv = new Node(__m)));
				return rv;
			}
		}
	
		/** 
		 * {@inheritDoc}
		 * @since 2018/02/19
		 */
		@Override
		public final ThreadStat.Node[] subNodes()
		{
			Map<MethodTracker.TrackedMethod, Node> nodes = this._nodes;
			synchronized (nodes)
			{
				Collection<Node> values = nodes.values();
				return values.<Node>toArray(new Node[values.size()]);
			}
		}
	}
}

