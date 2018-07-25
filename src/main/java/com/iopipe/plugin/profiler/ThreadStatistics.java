package com.iopipe.plugin.profiler;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * This contains statistics about a single thread.
 *
 * @since 2018/05/24
 */
public final class ThreadStatistics
{
	/** The thread ID. */
	public final long id;
	
	/** The thread name. */
	public final String name;
	
	/** The CPU time of the thread. */
	public final long cputime;
	
	/** The user time of the thread. */
	public final long usertime;
	
	/** How many times the thread blocked synchronizing on a monitor. */
	public final long blockedcount;
	
	/** The duration of time spent being blocked. */
	public final long blockedtime;
	
	/** Gets the name of the object being locked. */
	public final String lockedname;
	
	/** The ID number of the thread which owns the lock. */
	public final long lockedownerid;
	
	/** The current state of the thread. */
	public final Thread.State state;
	
	/** How many times the thread waited on a monitor. */
	public final long waitedcount;
	
	/** How long the thread spent waiting for a monitor. */
	public final long waitedtime;
	
	/**
	 * Initializes invalid thread statistics.
	 *
	 * @since 2018/05/24
	 */
	public ThreadStatistics()
	{
		this(-1, null, -1, -1, -1, -1, null, -1, null, -1, -1);
	}
	
	/**
	 * Initializes thread statistics.
	 *
	 * @param __id The thread ID.
	 * @param __name The thread name.
	 * @param __cputime The CPU time of the thread.
	 * @param __usertime The user time of the thread.
	 * @param __blockedcount How many times the thread blocked synchronizing on a monitor.
	 * @param __blockedtime The duration of time spent being blocked.
	 * @param __lockedname  Gets the name of the object being locked.
	 * @param __lockedownerid The ID number of the thread which owns the lock.
	 * @param __state The current state of the thread.
	 * @param __waitedcount How many times the thread waited on a monitor.
	 * @param __waitedtime How long the thread spent waiting for a monitor.
	 * @since 2018/05/24
	 */
	public ThreadStatistics(long __id, String __name, long __cputime,
		long __usertime, long __blockedcount, long __blockedtime,
		String __lockedname, long __lockedownerid, Thread.State __state,
		long __waitedcount, long __waitedtime)
	{
		this.id = __id;
		this.name = (__name != null ? __name : "Unknown");
		this.cputime = Math.max(-1, __cputime);
		this.usertime = Math.max(-1, __usertime);
		this.blockedcount = Math.max(-1, __blockedcount);
		this.blockedtime = Math.max(-1, __blockedtime);
		this.lockedname = __lockedname;
		this.lockedownerid = __lockedownerid;
		this.state = __state;
		this.waitedcount = Math.max(-1, __waitedcount);
		this.waitedtime = Math.max(-1, __waitedtime);
	}
	
	/**
	 * Creates snapshots of all the threads which exist.
	 *
	 * @return The statistics on all running threads.
	 * @since 2018/05/24
	 */
	public static ThreadStatistics[] snapshots()
	{
		return ThreadStatistics.snapshots(
			ManagementFactory.getThreadMXBean());
	}
	
	/**
	 * Creates snapshots of all the threads which exist.
	 *
	 * @param __bean The bean to get thread information from.
	 * @return The statistics on all running threads.
	 * @since 2018/05/24
	 */
	public static ThreadStatistics[] snapshots(ThreadMXBean __bean)
	{
		if (__bean == null)
			return new ThreadStatistics[0];
		
		// Get the ID of all threads
		long[] tids;
		try
		{
			tids = __bean.getAllThreadIds();
		}
		catch (SecurityException e)
		{
			tids = new long[0];
		}
		
		// Fill in thread information
		int numthreads = tids.length;
		ThreadStatistics[] rv = new ThreadStatistics[numthreads];
		for (int i = 0; i < numthreads; i++)
		{
			long id = tids[i];
			ThreadInfo info = __bean.getThreadInfo(id);
			
			// No information on the thread, ignore
			if (info == null)
				continue;
			
			// These might not be supported
			long cputime = -1;
			try
			{
				cputime = __bean.getThreadCpuTime(id);
			}
			catch (UnsupportedOperationException e)
			{
			}
			
			long usertime = -1;
			try
			{
				usertime = __bean.getThreadUserTime(id);
			}
			catch (UnsupportedOperationException e)
			{
			}
			
			long blockedtime = -1;
			try
			{
				blockedtime = info.getBlockedTime();
			}
			catch (UnsupportedOperationException e)
			{
			}
			
			long waitedtime = -1;
			try
			{
				waitedtime = info.getWaitedTime();
			}
			catch (UnsupportedOperationException e)
			{
			}
			
			// Build
			rv[i] = new ThreadStatistics(
				id,
				info.getThreadName(),
				cputime,
				usertime,
				info.getBlockedCount(),
				blockedtime,
				info.getLockName(),
				info.getLockOwnerId(),
				info.getThreadState(),
				info.getWaitedCount(),
				waitedtime);
		}
		
		return rv;
	}
}

