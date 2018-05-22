package com.iopipe.plugin.profiler;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

/**
 * This class is used to export statistics that were measured into a basic
 * CSV format.
 *
 * @since 2018/05/22
 */
final class __StatExport__
{
	/** Statistics used. */
	protected final List<ManagementStatistics> statistics;
	
	/**
	 * Initializes the statistics exporter.
	 *
	 * @param __s The statistics which were measured.
	 * @since 2018/05/22
	 */
	__StatExport__(ManagementStatistics... __s)
	{
		this(Arrays.<ManagementStatistics>asList((__s != null ? __s :
			new ManagementStatistics[0])));
	}
	
	/**
	 * Initializes the statistics exporter.
	 *
	 * @param __s The statistics which were measured.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/05/22
	 */
	__StatExport__(List<ManagementStatistics> __s)
		throws NullPointerException
	{
		if (__s == null)
			throw new NullPointerException();
		
		this.statistics = __s;
	}
	
	/**
	 * Dumps the statistics to the given output stream.
	 *
	 * @param __os The stream to write to.
	 * @throws IOException On write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/05/22
	 */
	public final void run(OutputStream __os)
		throws IOException, NullPointerException
	{
		if (__os == null)
			throw new NullPointerException();
		
		// Setup print stream which is written to, because this is just
		// CSV text.
		// Buffer all the output text to at least 64KiB and do not perform any
		// flushing when newlines are read.
		// This way when it comes to the deflator to compress all the text
		// bytes it has pretty much the entire file to work with at once rather
		// than having tiny chunks of files. This is more efficient.
		PrintStream ps = new PrintStream(new BufferedOutputStream(__os, 65536),
			false);
		
		// Determine the number of statistics made because there will just be
		// arrays allocated to store all the snapshot data
		List<ManagementStatistics> statistics = this.statistics;
		int nsnaps = statistics.size();
		
		// Used to map rows to columns more easily
		long[] xabs = new long[nsnaps],
			xrel = new long[nsnaps];
		ClassLoaderStatistics[] xcl = new ClassLoaderStatistics[nsnaps];
		CompilerStatistics[] xjit = new CompilerStatistics[nsnaps];
		
		// Go through all of the statistics and explode them into the single
		// array. It would be faster to write out all the columns with their
		// descriptive forms and timestamps being the row keys, however that
		// would be messy since English is a horizontal left to right
		// language. Additionally screens are usually wider. It is also easier
		// to grasp how much something is changing linearly than vertically.
		for (int i = 0; i < nsnaps; i++)
		{
			ManagementStatistics from = statistics.get(i);
			
			xabs[i] = from.abstime;
			xrel[i] = from.reltime;
			xcl[i] = from.classloader;
			xjit[i] = from.compiler;
		}
		
		// Absolute time
		ps.print("AbsoluteTime (ns)");
		for (int i = 0; i < nsnaps; i++)
		{
			ps.print(',');
			ps.print(xabs[i]);
		}
		ps.println();
		xabs = null;
		
		// Relative time
		ps.print("RelativeTime (ns)");
		for (int i = 0; i < nsnaps; i++)
		{
			ps.print(',');
			ps.print(xrel[i]);
		}
		ps.println();
		xrel = null;
		
		// Class loader counts
		__classLoader(xcl, nsnaps, ps);
		xcl = null;
		
		// Compiler counts
		__compiler(xjit, nsnaps, ps);
		xjit = null;
		
		// Before terminating, flush it so that all the data is written
		ps.flush();
	}
	
	/**
	 * Dumps class loader information.
	 *
	 * @param __xcl Class loader information.
	 * @param __nsnaps The number of snapshots.
	 * @param __ps The output stream.
	 * @throws IOException On write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/05/22
	 */
	private final void __classLoader(ClassLoaderStatistics[] __xcl,
		int __nsnaps, PrintStream __ps)
		throws IOException, NullPointerException
	{
		if (__xcl == null || __ps == null)
			throw new NullPointerException();
		
		// Storage for all the snapshots
		int[] xcur = new int[__nsnaps];
		long[] xlod = new long[__nsnaps],
			xunl = new long[__nsnaps];
		
		// Explode all the stats
		for (int i = 0; i < __nsnaps; i++)
		{
			ClassLoaderStatistics from = __xcl[i];
			
			xcur[i] = from.current;
			xlod[i] = from.loaded;
			xunl[i] = from.unloaded;
		}
		
		// Currently loaded classes
		__ps.print("CurrentLoadedClasses (classes)");
		for (int i = 0; i < __nsnaps; i++)
		{
			__ps.print(',');
			__ps.print(xcur[i]);
		}
		__ps.println();
		xcur = null;
		
		// Total loaded classes
		__ps.print("TotalLoadedClasses (classes)");
		for (int i = 0; i < __nsnaps; i++)
		{
			__ps.print(',');
			__ps.print(xlod[i]);
		}
		__ps.println();
		xlod = null;
		
		// Total unloaded classes
		__ps.print("TotalUnloadedClasses (classes)");
		for (int i = 0; i < __nsnaps; i++)
		{
			__ps.print(',');
			__ps.print(xunl[i]);
		}
		__ps.println();
		xunl = null;
	}
	
	/**
	 * Dumps compiler information.
	 *
	 * @param __xjit Compiler information.
	 * @param __nsnaps The number of snapshots.
	 * @param __ps The output stream.
	 * @throws IOException On write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/05/22
	 */
	private final void __compiler(CompilerStatistics[] __xjit,
		int __nsnaps, PrintStream __ps)
		throws IOException, NullPointerException
	{
		if (__xjit == null || __ps == null)
			throw new NullPointerException();
		
		long[] xctime = new long[__nsnaps];
		
		// Explode statistics
		for (int i = 0; i < __nsnaps; i++)
		{
			CompilerStatistics from = __xjit[i];
			
			xctime[i] = from.compilems;
		}
		
		// Total compilation time
		__ps.print("TotalCompilationTime (ms)");
		for (int i = 0; i < __nsnaps; i++)
		{
			__ps.print(',');
			__ps.print(xctime[i]);
		}
		__ps.println();
		xctime = null;
	}
}

