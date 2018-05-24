package com.iopipe.plugin.profiler;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

/**
 * This contains statistics for the VM memory.
 *
 * @since 2018/05/22
 */
public final class MemoryStatistics
{
	/** Heap memory usage. */
	public final MemoryUsageStatistic heap;
	
	/** Non-heap memory usage. */
	public final MemoryUsageStatistic nonheap;
	
	/** Pending finalization count. */
	public final int pendingfinalizers;
	
	/**
	 * Initializes the memory statistics.
	 *
	 * @param __h Heap usage.
	 * @param __n Non-heap usage.
	 * @param __f Pending finalizers.
	 * @since 2018/05/24
	 */
	public MemoryStatistics(MemoryUsageStatistic __h, MemoryUsageStatistic __n,
		int __f)
	{
		this.heap = (__h != null ? __h :
			new MemoryUsageStatistic(-1, -1, -1, -1));
		this.nonheap = (__n != null ? __n :
			new MemoryUsageStatistic(-1, -1, -1, -1));
		this.pendingfinalizers = Math.max(-1, __f);
	}
	
	/**
	 * Obtains a snapshot of the memory usage.
	 *
	 * @return The memory usage snapshot or {@code null} if it is not valid.
	 * @since 2018/05/24
	 */
	public static MemoryStatistics snapshot()
	{
		return MemoryStatistics.snapshot(
			ManagementFactory.getMemoryMXBean());
	}
	
	/**
	 * Builds a snapshot from the given bean.
	 *
	 * @param __bean The bean to snapshot.
	 * @return The snapshot of the given bean or {@code null} if it is not
	 * valid.
	 * @since 2018/05/24
	 */
	public static MemoryStatistics snapshot(MemoryMXBean __bean)
	{
		if (__bean == null)
			return null;
		
		return new MemoryStatistics(
			MemoryUsageStatistic.from(__bean.getHeapMemoryUsage()),
			MemoryUsageStatistic.from(__bean.getNonHeapMemoryUsage()),
			__bean.getObjectPendingFinalizationCount());
	}
}

