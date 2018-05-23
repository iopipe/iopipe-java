package com.iopipe.plugin.profiler;

import java.lang.management.MemoryUsage;

/**
 * This stores a single memory usage statistic.
 *
 * @since 2018/05/23
 */
public final class MemoryUsageStatistic
{
	/** Initial memory amount. */
	public final long initbytes;
	
	/** The used number of bytes. */
	public final long usedbytes;
	
	/** The committed number of bytes. */
	public final long committedbytes;
	
	/** The maximum number of bytes that can be used. */
	public final long maxbytes;
	
	/**
	 * Initializes the memory usage statistic.
	 *
	 * @param __i The initial number of bytes.
	 * @param __u The used number of bytes.
	 * @param __c The committed number of bytes.
	 * @param __m The maximum number of bytes.
	 * @since 2018/05/23
	 */
	public MemoryUsageStatistic(long __i, long __u, long __c, long __m)
	{
		this.initbytes = Math.max(-1, __i);
		this.usedbytes = Math.max(-1, __u);
		this.committedbytes = Math.max(-1, __c);
		this.maxbytes = Math.max(-1, __m);
	}
	
	/**
	 * Initializes the memory usage from the given usage.
	 *
	 * @param __m The input memory usage.
	 * @return Statistic for that usage or {@code null} if it is not valid.
	 * @since 2018/05/23
	 */
	public static MemoryUsageStatistic from(MemoryUsage __m)
	{
		if (__m == null)
			return null;
		
		return new MemoryUsageStatistic(__m.getInit(),
			__m.getUsed(),
			__m.getCommitted(),
			__m.getMax());
	}
}

