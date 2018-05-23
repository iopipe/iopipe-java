package com.iopipe.plugin.profiler;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Statistics for the garbage collector.
 *
 * @since 2018/05/22
 */
public final class GarbageCollectorStatistics
{
	/** The name of the garbage collector. */
	public final String name;
	
	/** The collection count. */
	public final long count;
	
	/** The time spent collecting. */
	public final long durationms;
	
	/**
	 * Initializes the garbage collector statistics.
	 *
	 * @param __n The name of the collector.
	 * @param __c The number of objects collected.
	 * @param __ns The duration of collection.
	 * @since 2018/05/22
	 */
	public GarbageCollectorStatistics(String __n, long __c, long __ns)
	{
		this.name = (__n != null ? __n : "Unknown");
		this.count = Math.max(-1, __c);
		this.durationms = Math.max(-1, __ns);
	}
	
	/**
	 * Creates snapshots of the garbage collector statistics.
	 *
	 * @return The snapshots which are available.
	 * @since 2018/05/22
	 */
	public static GarbageCollectorStatistics[] snapshots()
	{
		return GarbageCollectorStatistics.snapshots(
			ManagementFactory.getGarbageCollectorMXBeans());
	}
	
	/**
	 * Creates snapshots of the given garbage collector beans.
	 *
	 * @param __beans The beans to snapshot.
	 * @return The snapshotted beans.
	 * @since 2018/05/22
	 */
	public static GarbageCollectorStatistics[] snapshots(
		List<GarbageCollectorMXBean> __beans)
	{
		if (__beans == null)
			return new GarbageCollectorStatistics[0];
		
		int n = __beans.size();
		List<GarbageCollectorStatistics> rv = new ArrayList(n);
		for (int i = 0; i < n; i++)
		{
			GarbageCollectorMXBean bean = __beans.get(i);
			if (bean == null || !bean.isValid())
				continue;
			
			rv.add(new GarbageCollectorStatistics(
				bean.getName(),
				bean.getCollectionCount(),
				bean.getCollectionTime()));
		}
		
		return rv.<GarbageCollectorStatistics>toArray(
			new GarbageCollectorStatistics[rv.size()]);
	}
}

