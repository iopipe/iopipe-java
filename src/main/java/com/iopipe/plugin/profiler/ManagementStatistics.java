package com.iopipe.plugin.profiler;

/**
 * This class contains the information on all of the management statistics
 * which have been measured.
 *
 * @since 2018/05/22
 */
public final class ManagementStatistics
{
	/** The absolute time the sample was taken. */
	public final long abstime;
	
	/** The relative time the sample was taken. */
	public final long reltime;
	
	/** Class loader statistics. */
	public final ClassLoaderStatistics classloader;
	
	/**
	 * Initializes the management statistics.
	 *
	 * @param __abs Absolute time.
	 * @param __rel Relative time.
	 * @param __cl The class loader statistics.
	 * @since 2018/05/22
	 */
	public ManagementStatistics(long __abs, long __rel,
		ClassLoaderStatistics __cl)
	{
		this.abstime = __abs;
		this.reltime = __rel;
		this.classloader = (__cl != null ? __cl :
			new ClassLoaderStatistics(0, 0, 0));
	}
	
	/**
	 * Creates a snapshot of the statistics used for management.
	 *
	 * @param __rel The relative time.
	 * @return The snapshot of the all statistics.
	 * @since 2018/05/22
	 */
	public static ManagementStatistics snapshot(long __rel)
	{
		return new ManagementStatistics(
			System.nanoTime(), __rel,
			ClassLoaderStatistics.snapshot());
	}
}

