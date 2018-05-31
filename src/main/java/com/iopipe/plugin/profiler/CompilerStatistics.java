package com.iopipe.plugin.profiler;

import java.lang.management.CompilationMXBean;
import java.lang.management.ManagementFactory;

/**
 * This contains statistics on the compiler.
 *
 * @since 2018/05/22
 */
public final class CompilerStatistics
{
	/** Time spent in milliseconds compiling. */
	protected final long compilems;
	
	/**
	 * Initializes the compiler statistics.
	 *
	 * @param __ms The time spent compiling.
	 * @since 2018/05/22
	 */
	public CompilerStatistics(long __ms)
	{
		this.compilems = Math.max(-1, __ms);
	}
	
	/**
	 * Returns a snapshot of the compiler statistics.
	 *
	 * @return The compiler statistics or {@code null} if none are available.
	 * @since 2018/05/22
	 */
	public static CompilerStatistics snapshot()
	{
		// This is optional
		CompilationMXBean bean = ManagementFactory.getCompilationMXBean();
		if (bean == null)
			return null;
		
		return CompilerStatistics.snapshot(bean);
	}
	
	/**
	 * Returns a snapshot of the compiler statistics.
	 *
	 * @param __bean The bean to obtain the information from.
	 * @return The compiler statistics or {@code null} if none are available.
	 * @since 2018/05/22
	 */
	public static CompilerStatistics snapshot(CompilationMXBean __bean)
	{
		if (__bean == null)
			return null;
		
		// Might not be available
		try
		{
			return new CompilerStatistics(__bean.getTotalCompilationTime());
		}
		
		// Not measureable
		catch (UnsupportedOperationException e)
		{
			return new CompilerStatistics(-1);
		}
	}
}
