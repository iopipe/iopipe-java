package com.iopipe.plugin.profiler;

/**
 * The thread stat and the node information all have the ability to obtain
 * nodes according to their subnodes.
 *
 * @since 2018/02/19
 */
public interface ThreadStatNodeTraversal
{
	/** 
	 * Obtains the specified sub-node.
	 *
	 * @param __m The method for tracking.
	 * @return The node for the given method.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/02/19
	 */
	public abstract ThreadStat.Node subNode(MethodTracker.TrackedMethod __m)
		throws NullPointerException;
	
	/**
	 * Returns an array of all the available sub-nodes.
	 *
	 * @return An array containing all of the nodes.
	 * @since 2018/02/19
	 */
	public abstract ThreadStat.Node[] subNodes();
}

