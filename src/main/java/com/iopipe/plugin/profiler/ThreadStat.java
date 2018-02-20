package com.iopipe.plugin.profiler;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
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
	
	/** The total number of invocations done in this thread. */
	private final AtomicInteger _numinvokes =
		new AtomicInteger();
	
	/** Root nodes for the thread tree. */
	private final Map<MethodTracker.TrackedMethod, Node> _nodes =
		new LinkedHashMap<>();
	
	/** Time spent sleeping or running in this thread. */
	private volatile long _wgabstime;
	
	/** Time spent running the thread in whole time. */
	private volatile long _wgtime;
	
	/** Time spent sleeping in this thread. */
	private volatile long _wgsleeptime;
	
	/** Gross absolute time for each node. */
	private volatile long _grosswgtime;
	
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
	 * Returns the gross time executing all the nodes.
	 *
	 * @return The entire tree gross time.
	 * @since 2018/02/19
	 */
	public final long grossWholeGraphTime()
	{
		return this._grosswgtime;
	}
	
	/**
	 * An approximation of how much this thread has spent executing on the
	 * CPU when not asleep.
	 *
	 * @return The estimated gross time not spent sleeping.
	 * @since 2018/02/19
	 */
	public final long grossWholeGraphTimeApproxNotSleeping()
	{
		return this._grosswgtime - this._wgsleeptime;
	}
	
	/**
	 * Returns the number of invocations made in the thread.
	 *
	 * @return The invocation count.
	 * @since 2018/02/19
	 */
	public final int invocationCount()
	{
		return this._numinvokes.get();
	}
	
	/**
	 * Returns the logical index of the thread.
	 *
	 * @return The thread logical index.
	 * @since 2018/02/19
	 */
	public final int logicalIndex()
	{
		return this.logicalindex;
	}
	
	/**
	 * Returns the name of the thread.
	 *
	 * @return The thread name.
	 * @since 2018/02/19
	 */
	public final String name()
	{
		return this.name;
	}
	
	/**
	 * Parses and keeps track of the specified stack trace.
	 *
	 * @param __abs The absolute time since the start of execution in
	 * nanoseconds.
	 * @param __rel The relative time since the last trace.
	 * @since 2018/02/19
	 */
	public final void parseStackTrace(long __abs, int __rel)
	{
		Thread thread = this.thread;
		MethodTracker methods = this.methods;
		
		// Only count threads which are running, not any which are blocked by
		// a lock or terminated because they consume no CPU time
		Thread.State state = thread.getState();
		if (state != Thread.State.RUNNABLE)
		{
			if (state == Thread.State.TERMINATED)
				return;
			
			// Add time spent sleeping or waiting on a lock
			this._wgabstime += __rel;
			this._wgsleeptime += __rel;
			
			// Add to the gross time to keep an estimate during sleep
			this._grosswgtime += __rel;
			
			return;
		}
		
		// Add to whole graph time
		this._wgabstime += __rel;
		this._wgtime += __rel;
		
		// Gross 
		long grosswgtime = this._grosswgtime;
		
		// Node traversal starts at the root node
		ThreadStatNodeTraversal traversal = this;
		
		// Generate stack trace, since each sub-node based on the origin point
		// of call is unique per stack trace, methods which call other methods
		// even though they may result in the same method will have different
		// timers and such
		// The last element is the lowest method in the trace so it will be
		// the root node
		StackTraceElement[] traces = thread.getStackTrace();
		int n = traces.length;
		boolean top = true;
		for (int i = n - 1; i >= 0; i--, top = false)
		{
			// Add gross time spent executing nodes
			grosswgtime += __rel;
			
			// Find the index for this unique method
			StackTraceElement trace = traces[i];
			MethodTracker.TrackedMethod tracked = methods.track(trace);
			
			// Need the node for this entry
			Node sub = traversal.subNode(tracked);
			
			// Parse this node
			sub.parse(__abs, __rel, top);
			
			// Traverse into the sub-tree
			traversal = sub;
		}
		
		// Record gross time after time spent in nodes calculated
		this._grosswgtime = grosswgtime;
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
	 * Returns the time spent absolutly in this thread.
	 *
	 * @return The time spent absolutly in this thread.
	 * @since 2018/02/19
	 */
	public final long wholeGraphAbsoluteTime()
	{
		return this._wgabstime;
	}
	
	/**
	 * Returns the time spent sleeping in this thread.
	 *
	 * @return The time spent sleeping in this thread.
	 * @since 2018/02/19
	 */
	public final long wholeGraphSleepTime()
	{
		return this._wgsleeptime;
	}
	
	/**
	 * Returns the time spent running in this whole graph.
	 *
	 * @return The time spent running in this whole graph.
	 * @since 2018/02/19
	 */
	public final long wholeGraphTime()
	{
		return this._wgtime;
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
		
		/** Time spent with this node in the stack trace. */
		private volatile long _graphtime;
		
		/** Time spent actually at the top of the stack. */
		private volatile long _selftime;
		
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
		 * Parses this single node for execution.
		 *
		 * @param __abs The absolute time since the start of execution in
		 * nanoseconds.
		 * @param __rel The relative time since the last trace.
		 * @param __top Is this node at the top of the stack?
		 * @since 2018/02/19
		 */
		public final void parse(long __abs, int __rel, boolean __top)
		{
			// Time was spent in this method so always add it
			this._graphtime += __rel;
			
			// But it is only considered executing if it is at the top of
			// the stack
			if (__top)
				this._selftime += __rel;
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

