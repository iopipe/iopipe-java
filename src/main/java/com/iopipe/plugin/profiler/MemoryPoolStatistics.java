package com.iopipe.plugin.profiler;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Statistics for a single memory pool.
 *
 * @since 2018/05/23
 */
public final class MemoryPoolStatistics
{
	/** The name of the memory pool. */
	public final String name;
	
	/** Collection usage, the time the VM spent in recycling unused objects. */
	public final MemoryUsageStatistics collectionusage;
	
	/** The threshold in bytes of the collection usage. */
	public final long collectionusagethresholdbytes;
	
	/** The number of times the collection threshold was reached. */
	public final long collectionusagethresholdcount;
	
	/** Peak memory usage. */
	public final MemoryUsageStatistics peakusage;
	
	/** Memory usage. */
	public final MemoryUsageStatistics usage;
	
	/** Memory usage threshold in bytes. */
	public final long usagethresholdbytes;
	
	/** The number of times the threshold was exceeded. */
	public final long usagethresholdcount;
	
	/**
	 * Initializes invalid memory statistics.
	 *
	 * @since 2018/05/24
	 */
	public MemoryPoolStatistics()
	{
		this(null, null, -1, -1, null, null, -1, -1);
	}
	
	/**
	 * Initializes the memory usage statistics.
	 *
	 * @param __name The name of the memory pool.
	 * @param __cu Collection usage, the time the VM spent in recycling unused objects.
	 * @param __cutb The threshold in bytes of the collection usage.
	 * @param __cutc The number of times the collection threshold was reached.
	 * @param __peak Peak memory usage.
	 * @param __use Memory usage.
	 * @param __utb Memory usage threshold in bytes.
	 * @param __utc The number of times the threshold was exceeded.
	 * @since 2018/05/23
	 */
	public MemoryPoolStatistics(String __name, MemoryUsageStatistics __cu,
		long __cutb, long __cutc, MemoryUsageStatistics __peak,
		MemoryUsageStatistics __use, long __utb, long __utc)
	{
		this.name = (__name != null ? __name : "Unknown");
		this.collectionusage = (__cu != null ? __cu :
			new MemoryUsageStatistics(-1, -1, -1, -1));
		this.collectionusagethresholdbytes = Math.max(-1, __cutb);
		this.collectionusagethresholdcount = Math.max(-1, __cutc);
		this.peakusage = (__peak != null ? __peak :
			new MemoryUsageStatistics(-1, -1, -1, -1));
		this.usage = (__use != null ? __use :
			new MemoryUsageStatistics(-1, -1, -1, -1));
		this.usagethresholdbytes = Math.max(-1, __utb);
		this.usagethresholdcount = Math.max(-1, __utc);
	}
	
	/**
	 * Initializes the snapshots of all the memory pools.
	 *
	 * @return The snapshotted statistics.
	 * @since 2018/05/23
	 */
	public static MemoryPoolStatistics[] snapshots()
	{
		return MemoryPoolStatistics.snapshots(
			ManagementFactory.getMemoryPoolMXBeans());
	}
	
	/**
	 * Initializes the snapshots of all the specified memory pools.
	 *
	 * @param __beans The input statistics.
	 * @return The snapshotted statistics.
	 * @since 2018/05/23
	 */
	public static MemoryPoolStatistics[] snapshots(
		List<MemoryPoolMXBean> __beans)
	{
		if (__beans == null)
			return new MemoryPoolStatistics[0];
		
		int n = __beans.size();
		List<MemoryPoolStatistics> rv = new ArrayList<>(n);
		for (int i = 0; i < n; i++)
		{
			MemoryPoolMXBean bean = __beans.get(i);
			if (bean == null || !bean.isValid())
				continue;
			
			// Threshold bytes
			long collthreshbytes = -1;
			try
			{
				collthreshbytes = bean.getCollectionUsageThreshold();
			}
			catch (UnsupportedOperationException e)
			{
			}
			
			// Threshold count
			long collthreshcount = -1;
			try
			{
				collthreshcount = bean.getCollectionUsageThresholdCount();
			}
			catch (UnsupportedOperationException e)
			{
			}
			
			// Usage threshhold bytes
			long usagethreshbytes = -1;
			try
			{
				usagethreshbytes = bean.getUsageThreshold();
			}
			catch (UnsupportedOperationException e)
			{
			}
			
			// Usage threshold count
			long usagethreshcount = -1;
			try
			{
				usagethreshcount = bean.getUsageThresholdCount();
			}
			catch (UnsupportedOperationException e)
			{
			}
			
			rv.add(new MemoryPoolStatistics(
				bean.getName(),
				MemoryUsageStatistics.from(bean.getCollectionUsage()),
				collthreshbytes,
				collthreshcount,
				MemoryUsageStatistics.from(bean.getPeakUsage()),
				MemoryUsageStatistics.from(bean.getUsage()),
				usagethreshbytes,
				usagethreshcount));
		}
		
		return rv.<MemoryPoolStatistics>toArray(
			new MemoryPoolStatistics[rv.size()]);
	}
}

