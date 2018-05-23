package com.iopipe.plugin.profiler;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	
	/** Compilation statistics. */
	public final CompilerStatistics compiler;
	
	/** Garbage collection statistics. */
	public final List<GarbageCollectorStatistics> gc;
	
	/** Uptime of the virtual machine. */
	public final UptimeStatistics uptime;
	
	/**
	 * Initializes the management statistics.
	 *
	 * @param __abs Absolute time.
	 * @param __rel Relative time.
	 * @param __cl The class loader statistics.
	 * @param __jit Compiler statistics.
	 * @param __gc Garbage collection statistics.
	 * @param __up Virtual machine uptime.
	 * @since 2018/05/22
	 */
	public ManagementStatistics(long __abs, long __rel,
		ClassLoaderStatistics __cl, CompilerStatistics __jit,
		GarbageCollectorStatistics[] __gc, UptimeStatistics __up)
	{
		this.abstime = __abs;
		this.reltime = __rel;
		this.classloader = (__cl != null ? __cl :
			new ClassLoaderStatistics(0, 0, 0));
		this.compiler = (__jit != null ? __jit :
			new CompilerStatistics(-1));
		this.gc = Collections.<GarbageCollectorStatistics>unmodifiableList(
			Arrays.<GarbageCollectorStatistics>asList(
			(__gc == null ? new GarbageCollectorStatistics[0] :
			(__gc = __gc.clone()))));
		this.uptime = (__up != null ? __up :
			new UptimeStatistics(0, -1));
		
		// Initialize values in the event they are null
		for (int i = 0, n = __gc.length; i < n; i++)
			if (__gc[i] == null)
				__gc[i] = new GarbageCollectorStatistics("Invalid", -1, -1);
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
			ClassLoaderStatistics.snapshot(),
			CompilerStatistics.snapshot(),
			GarbageCollectorStatistics.snapshots(),
			UptimeStatistics.snapshot());
	}
}

