package com.iopipe.plugin.profiler;

import java.util.ArrayList;
import java.util.List;
import java.lang.management.BufferPoolMXBean;
import java.lang.management.ManagementFactory;

/**
 * This contains statistics on the buffer pool.
 *
 * @since 2108/05/27
 */
public final class BufferPoolStatistics
{
	/** The name of the pool. */
	public final String name;
	
	/** The number of objects in the pool. */
	public final long count;
	
	/** The memory used in the pool. */
	public final long usedbytes;
	
	/** The capacity of the pool in bytes. */
	public final long capacitybytes;
	
	/**
	 * Initializes an unknown buffer pool statistic.
	 *
	 * @since 2018/05/30
	 */
	public BufferPoolStatistics()
	{
		this(null, -1, -1, -1);
	}
	
	/**
	 * Initializes the buffer pool statistics.
	 *
	 * @param __name The name of the pool.
	 * @param __count The objects in the pool.
	 * @param __usedbytes The number of used bytes.
	 * @param __capacitybytes The capacity of the pool.
	 * @since 2018/05/27
	 */
	public BufferPoolStatistics(String __name, long __count, long __usedbytes,
		long __capacitybytes)
	{
		this.name = (__name != null ? __name : "Unknown");
		this.count = Long.max(-1, __count);
		this.usedbytes = Long.max(-1, __usedbytes);
		this.capacitybytes = Long.max(-1, __capacitybytes);
	}
	
	/**
	 * Obtains a snapshot of the statistics.
	 *
	 * @return The buffer pool statistics or {@code null} if it is not valid.
	 * @since 2018/05/27
	 */
	public static BufferPoolStatistics[] snapshots()
	{
		return BufferPoolStatistics.snapshots(
			ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class));
	}
	
	/**
	 * Obtains the snapshots of the given beans.
	 *
	 * @param __beans The beans to snapshot.
	 * @return The snapshotted beans.
	 * @since 2018/05/30
	 */
	public static BufferPoolStatistics[] snapshots(
		List<BufferPoolMXBean> __beans)
	{
		if (__beans == null)
			return new BufferPoolStatistics[0];
		
		int n = __beans.size();
		List<BufferPoolStatistics> rv = new ArrayList(n);
		for (int i = 0; i < n; i++)
		{
			BufferPoolMXBean bean = __beans.get(i);
			if (bean == null)
				continue;
			
			rv.add(new BufferPoolStatistics(
				bean.getName(),
				bean.getCount(),
				bean.getMemoryUsed(),
				bean.getTotalCapacity()));
		}
		
		return rv.<BufferPoolStatistics>toArray(
			new BufferPoolStatistics[rv.size()]);
	}
}

