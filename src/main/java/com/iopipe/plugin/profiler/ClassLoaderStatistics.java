package com.iopipe.plugin.profiler;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;

/**
 * This class contains a snapshot of information on the class loader.
 *
 * @since 2018/05/22
 */
public final class ClassLoaderStatistics
{
	/** Currently loaded classes. */
	public final int current;
	
	/** Total number of classes loaded. */
	public final long loaded;
	
	/** Total number of garbage collected classes. */
	public final long unloaded;
	
	/**
	 * Initializes the class loader statistics.
	 *
	 * @param __c The current number of classes loaded.
	 * @param __l The number of classes loaded.
	 * @param __u The number of classes unloaded.
	 * @since 2018/05/22
	 */
	public ClassLoaderStatistics(int __c, long __l, long __u)
	{
		this.current = Math.max(0, __c);
		this.loaded = Math.max(0, __l);
		this.unloaded = Math.max(0, __u);
	}
	
	/**
	 * Creates a snapshot of the class loader statistics.
	 *
	 * @return The generated statistics.
	 * @return The generated statistics or {@code null} if no statistics were
	 * generated.
	 * @since 2018/05/22
	 */
	public static ClassLoaderStatistics snapshot()
	{
		return ClassLoaderStatistics.snapshot(
			ManagementFactory.getClassLoadingMXBean());
	}
	
	/**
	 * Creates a snapshot of the class loader statistics.
	 *
	 * @param __bean The bean to get information from.
	 * @return The generated statistics or {@code null} if no statistics were
	 * generated.
	 * @since 2018/05/22
	 */
	public static ClassLoaderStatistics snapshot(ClassLoadingMXBean __bean)
	{
		if (__bean == null)
			return null;
		
		return new ClassLoaderStatistics(
			__bean.getLoadedClassCount(),
			__bean.getTotalLoadedClassCount(),
			__bean.getUnloadedClassCount());
	}
}

