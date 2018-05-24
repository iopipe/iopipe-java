package com.iopipe.plugin.profiler;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		GarbageCollectorStatistics[][] xgc =
			new GarbageCollectorStatistics[nsnaps][];
		UptimeStatistics[] xup = new UptimeStatistics[nsnaps];
		MemoryPoolStatistics[][] xpool =
			new MemoryPoolStatistics[nsnaps][];
		
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
			xup[i] = from.uptime;
			
			List<GarbageCollectorStatistics> gc = from.gc;
			xgc[i] = gc.<GarbageCollectorStatistics>toArray(
				new GarbageCollectorStatistics[gc.size()]);
			
			List<MemoryPoolStatistics> mpool = from.mempools;
			xpool[i] = mpool.<MemoryPoolStatistics>toArray(
				new MemoryPoolStatistics[mpool.size()]);
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
		
		// Uptime
		this.__uptime(xup, nsnaps, ps);
		xup = null;
		
		// Class loader counts
		this.__classLoader(xcl, nsnaps, ps);
		xcl = null;
		
		// Compiler counts
		this.__compiler(xjit, nsnaps, ps);
		xjit = null;
		
		// Garbage collection counts
		this.__garbage(xgc, nsnaps, ps);
		xgc = null;
		
		// Memory pools
		this.__memPool(xpool, nsnaps, ps);
		xpool = null;
		
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
		__xcl = null;
		
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
		__xjit = null;
		
		// Total compilation time
		__ps.print("TotalCompilationTime (ms)");
		for (int i = 0; i < __nsnaps; i++)
		{
			__ps.print(',');
			__ps.print(xctime[i]);
		}
		__ps.println();
	}
	
	/**
	 * Dumps garbage collector information.
	 *
	 * @param __xgc Garbage collector information.
	 * @param __nsnaps The number of snapshots.
	 * @param __ps The output stream.
	 * @throws IOException On write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/05/22
	 */
	private final void __garbage(GarbageCollectorStatistics[][] __xgc,
		final int __nsnaps, PrintStream __ps)
		throws IOException, NullPointerException
	{
		if (__xgc == null || __ps == null)
			throw new NullPointerException();
		
		// Stores individual garbage collector data
		class __GCData__
		{
			/** Count. */
			final long[] _count =
				new long[__nsnaps];
			
			/** Duration. */
			final long[] _durationms =
				new long[__nsnaps];
		};
		
		// There can be multiple garbage collectors available at once so
		// this copies the information for each one linearly
		Map<String, __GCData__> gcses = new LinkedHashMap<>();
		for (int i = 0; i < __nsnaps; i++)
		{
			GarbageCollectorStatistics[] from = __xgc[i];
			
			// For each state
			for (GarbageCollectorStatistics gcs : from)
			{
				String key = gcs.name;
				
				// Initialize data if missing
				__GCData__ data = gcses.get(key);
				if (data == null)
					gcses.put(key, (data = new __GCData__()));
				
				// Store it at the index
				data._count[i] = gcs.count;
				data._durationms[i] = gcs.durationms;
			}
		}
		__xgc = null;
		
		// Print for each key
		for (Map.Entry<String, __GCData__> e : gcses.entrySet())
		{
			String k = e.getKey();
			__GCData__ v = e.getValue();
			
			// Garbage collection count
			__ps.printf("GCCount.%s (collections)", k);
			long[] count = v._count;
			for (int i = 0; i < __nsnaps; i++)
			{
				__ps.print(',');
				__ps.print(count[i]);
			}
			__ps.println();
			
			// Garbage collection time
			__ps.printf("GCTime.%s (ms)", k);
			long[] durationms = v._durationms;
			for (int i = 0; i < __nsnaps; i++)
			{
				__ps.print(',');
				__ps.print(durationms[i]);
			}
			__ps.println();
		}
	}
	
	/**
	 * Prints memory usage statistics.
	 *
	 * @param __xmus The input memory usages.
	 * @param __nsnaps The number of snapshots.
	 * @param __ps The stream to write to.
	 * @param __prefix The prefix to use for printing.
	 * @throws IOException On write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/05/23
	 */
	private final void __memoryUsage(MemoryUsageStatistic[] __xmus,
		int __nsnaps, PrintStream __ps, String __prefix)
		throws IOException, NullPointerException
	{
		if (__xmus == null || __ps == null)
			throw new NullPointerException();
		
		long[] xinit = new long[__nsnaps],
			xused = new long[__nsnaps],
			xcomm = new long[__nsnaps],
			xmaxx = new long[__nsnaps];
		
		// Explode
		for (int i = 0; i < __nsnaps; i++)
		{
			MemoryUsageStatistic from = __xmus[i];
			
			xinit[i] = from.initbytes;
			xused[i] = from.usedbytes;
			xcomm[i] = from.committedbytes;
			xmaxx[i] = from.maxbytes;
		}
		__xmus = null;
		
		// Initial bytes
		__ps.print(__prefix);
		__ps.print(".init (byte)");
		for (int i = 0; i < __nsnaps; i++)
		{
			__ps.print(',');
			__ps.print(xinit[i]);
		}
		__ps.println();
		xinit = null;
		
		// Used bytes
		__ps.print(__prefix);
		__ps.print(".used (byte)");
		for (int i = 0; i < __nsnaps; i++)
		{
			__ps.print(',');
			__ps.print(xused[i]);
		}
		__ps.println();
		xused = null;
		
		// Committed bytes
		__ps.print(__prefix);
		__ps.print(".committed (byte)");
		for (int i = 0; i < __nsnaps; i++)
		{
			__ps.print(',');
			__ps.print(xcomm[i]);
		}
		__ps.println();
		xcomm = null;
		
		// Committed bytes
		__ps.print(__prefix);
		__ps.print(".max (byte)");
		for (int i = 0; i < __nsnaps; i++)
		{
			__ps.print(',');
			__ps.print(xmaxx[i]);
		}
		__ps.println();
		xmaxx = null;
	}
	
	/**
	 * Dumps memory pool information.
	 *
	 * @param __xpool The input memory pools.
	 * @param __nsnaps The number of snapshots.
	 * @param __ps The output stream.
	 * @throws IOException On write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/05/23
	 */
	private final void __memPool(MemoryPoolStatistics[][] __xpool,
		final int __nsnaps, PrintStream __ps)
		throws IOException, NullPointerException
	{
		if (__xpool == null || __ps == null)
			throw new NullPointerException();
		
		// Pool data storage
		class __PoolData__
		{
			/** Collection usage, the time the VM spent in recycling. */
			final MemoryUsageStatistic[] _collectionusage =
				new MemoryUsageStatistic[__nsnaps];
			
			/** The threshold in bytes of the collection usage. */
			final long[] _collectionusagethresholdbytes =
				new long[__nsnaps];
			
			/** The number of times the collection threshold was reached. */
			final long[] _collectionusagethresholdcount =
				new long[__nsnaps];
			
			/** Peak memory usage. */
			final MemoryUsageStatistic[] _peakusage =
				new MemoryUsageStatistic[__nsnaps];
			
			/** Memory usage. */
			final MemoryUsageStatistic[] _usage =
				new MemoryUsageStatistic[__nsnaps];
			
			/** Memory usage threshold in bytes. */
			final long[] _usagethresholdbytes =
				new long[__nsnaps];
			
			/** The number of times the threshold was exceeded. */
			final long[] _usagethresholdcount =
				new long[__nsnaps];
		};
		
		// Multiple pools can exist and they might not all exist at the
		// same time, so correctly handle that
		Map<String, __PoolData__> mapped = new LinkedHashMap<>();
		for (int i = 0; i < __nsnaps; i++)
		{
			MemoryPoolStatistics[] from = __xpool[i];
			
			// For each state
			for (MemoryPoolStatistics pool : from)
			{
				String key = pool.name;
				
				// Initialize data if missing
				__PoolData__ data = mapped.get(key);
				if (data == null)
					mapped.put(key, (data = new __PoolData__()));
				
				// Store it at the index
				data._collectionusage[i] = pool.collectionusage;
				data._collectionusagethresholdbytes[i] =
					pool.collectionusagethresholdbytes;
				data._collectionusagethresholdcount[i] =
					pool.collectionusagethresholdcount;
				data._peakusage[i] = pool.peakusage;
				data._usage[i] = pool.usage;
				data._usagethresholdbytes[i] = pool.usagethresholdbytes;
				data._usagethresholdcount[i] = pool.usagethresholdcount;
			}
		}
		__xpool = null;
		
		// Print for each key
		for (Map.Entry<String, __PoolData__> e : mapped.entrySet())
		{
			String k = e.getKey();
			__PoolData__ v = e.getValue();
			
			// Collection Usage
			this.__memoryUsage(v._collectionusage, __nsnaps, __ps,
				String.format("MemPool.%s.CollectionUsage", k));
			
			// Garbage collection count
			__ps.printf("MemPool.%s.CollectionUsageThreshold (bytes)", k);
			long[] collectionusagethresholdbytes =
				v._collectionusagethresholdbytes;
			for (int i = 0; i < __nsnaps; i++)
			{
				__ps.print(',');
				__ps.print(collectionusagethresholdbytes[i]);
			}
			__ps.println();
			
			// Garbage collection count exceeded
			__ps.printf("MemPool.%s.CollectionUsageThresholdHit (count)", k);
			long[] collectionusagethresholdcount =
				v._collectionusagethresholdcount;
			for (int i = 0; i < __nsnaps; i++)
			{
				__ps.print(',');
				__ps.print(collectionusagethresholdcount[i]);
			}
			__ps.println();
			
			// Peak Usage
			this.__memoryUsage(v._peakusage, __nsnaps, __ps,
				String.format("MemPool.%s.PeakUsage", k));
			
			// Current Usage
			this.__memoryUsage(v._usage, __nsnaps, __ps,
				String.format("MemPool.%s.Usage", k));
			
			// Usage threshold limit
			__ps.printf("MemPool.%s.UsageThreshold (bytes)", k);
			long[] usagethresholdbytes = v._usagethresholdbytes;
			for (int i = 0; i < __nsnaps; i++)
			{
				__ps.print(',');
				__ps.print(usagethresholdbytes[i]);
			}
			__ps.println();
			
			// Times usage threshold was hit
			__ps.printf("MemPool.%s.UsageThresholdHit (count)", k);
			long[] usagethresholdcount = v._usagethresholdcount;
			for (int i = 0; i < __nsnaps; i++)
			{
				__ps.print(',');
				__ps.print(usagethresholdcount[i]);
			}
			__ps.println();
		}
	}
	
	/**
	 * Dumps uptime information.
	 *
	 * @param __xup Uptime information.
	 * @param __nsnaps The number of snapshots.
	 * @param __ps The output stream.
	 * @throws IOException On write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/05/22
	 */
	private final void __uptime(UptimeStatistics[] __xup,
		int __nsnaps, PrintStream __ps)
		throws IOException, NullPointerException
	{
		if (__xup == null || __ps == null)
			throw new NullPointerException();
		
		long[] xboot = new long[__nsnaps],
			xup = new long[__nsnaps];
		
		// Explode all the stats
		for (int i = 0; i < __nsnaps; i++)
		{
			UptimeStatistics from = __xup[i];
			
			xboot[i] = from.startms;
			xup[i] = from.uptimems;
		}
		__xup = null;
		
		// Start time of the VM
		__ps.print("StartTime (utc ms)");
		for (int i = 0; i < __nsnaps; i++)
		{
			__ps.print(',');
			__ps.print(xboot[i]);
		}
		__ps.println();
		xboot = null;
		
		// Total loaded classes
		__ps.print("UpTime (ms)");
		for (int i = 0; i < __nsnaps; i++)
		{
			__ps.print(',');
			__ps.print(xup[i]);
		}
		__ps.println();
		xup = null;
	}
}

