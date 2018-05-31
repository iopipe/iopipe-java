package com.iopipe.plugin.profiler;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * This contains information on the uptime of the virtual machine.
 *
 * @since 2018/05/22
 */
public final class UptimeStatistics
{
	/** The start time of the virtual machine in milliseconds. */
	public final long startms;
	
	/** The number of milliseconds the virtual machine has been up. */
	public final long uptimems;
	
	/**
	 * Initializes the uptime statistics.
	 *
	 * @param __s The start time in milliseconds.
	 * @param __u The uptime in milliseconds.
	 * @since 2018/05/22
	 */
	public UptimeStatistics(long __s, long __u)
	{
		this.startms = __s;
		this.uptimems = Math.max(-1, __u);
	}
	
	/**
	 * Creates a snapshot of the uptime information.
	 *
	 * @return The uptime information or {@code null} if it is not valid.
	 * @since 2018/05/22
	 */
	public static UptimeStatistics snapshot()
	{
		return UptimeStatistics.snapshot(ManagementFactory.getRuntimeMXBean());
	}
	
	/**
	 * Creates a snapshot of the uptime information.
	 *
	 * @param __bean The bean to obtain information from.
	 * @return The uptime information or {@code null} if it is not valid.
	 * @since 2018/05/22
	 */
	public static UptimeStatistics snapshot(RuntimeMXBean __bean)
	{
		if (__bean == null)
			return null;
		
		return new UptimeStatistics(__bean.getStartTime(), __bean.getUptime());
	}
}

