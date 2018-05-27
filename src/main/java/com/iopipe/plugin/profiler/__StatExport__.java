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
		MemoryStatistics[] xmem = new MemoryStatistics[nsnaps];
		ThreadStatistics[][] xthr = new ThreadStatistics[nsnaps][];
		
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
			xmem[i] = from.memory;
			
			List<GarbageCollectorStatistics> gc = from.gc;
			xgc[i] = gc.<GarbageCollectorStatistics>toArray(
				new GarbageCollectorStatistics[gc.size()]);
			
			List<MemoryPoolStatistics> mpool = from.mempools;
			xpool[i] = mpool.<MemoryPoolStatistics>toArray(
				new MemoryPoolStatistics[mpool.size()]);
				
			List<ThreadStatistics> threads = from.threads;
			xthr[i] = threads.<ThreadStatistics>toArray(
				new ThreadStatistics[threads.size()]);
		}
		
		// Absolute time
		__StatExport__.__printRow(ps, "AbsoluteTime (ns)", xabs);
		xabs = null;
		
		// Relative time
		__StatExport__.__printRow(ps, "RelativeTime (ns)", xrel);
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
		
		// Memory
		this.__memory(xmem, nsnaps, ps);
		xmem = null;
		
		// Threads
		this.__threads(xthr, nsnaps, ps);
		xthr = null;
		
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
		
		__StatExport__.__printRow(__ps, "CurrentLoadedClasses (classes)",
			xcur);
		xcur = null;
		
		__StatExport__.__printRow(__ps, "TotalLoadedClasses (classes)", xlod);
		xlod = null;
		
		__StatExport__.__printRow(__ps, "TotalUnloadedClasses (classes)",
			xunl);
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
		
		__StatExport__.__printRow(__ps, "TotalCompilationTime (ms)", xctime);
		xctime = null;
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
			
			__StatExport__.__printRow(__ps, "GCCount.%s (collections)", k,
				v._count);
			
			__StatExport__.__printRow(__ps, "GCTime.%s (ms)", k,
				v._durationms);
		}
	}
	
	/**
	 * Prints memory statistics.
	 *
	 * @param __xmem The input memory usages.
	 * @param __nsnaps The number of snapshots.
	 * @param __ps The stream to write to.
	 * @throws IOException On write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/05/24
	 */
	private final void __memory(MemoryStatistics[] __xmem,
		int __nsnaps, PrintStream __ps)
		throws IOException, NullPointerException
	{
		if (__xmem == null || __ps == null)
			throw new NullPointerException();
		
		MemoryUsageStatistics[] xheap = new MemoryUsageStatistics[__nsnaps],
			xnonheap = new MemoryUsageStatistics[__nsnaps];
		int[] xfin = new int[__nsnaps];
		
		// Explode
		for (int i = 0; i < __nsnaps; i++)
		{
			MemoryStatistics from = __xmem[i];
			
			xheap[i] = from.heap;
			xnonheap[i] = from.nonheap;
			xfin[i] = from.pendingfinalizers;
		}
		__xmem = null;
		
		this.__memoryUsage(xheap, __nsnaps, __ps, "MemoryHeap");
		xheap = null;
		
		this.__memoryUsage(xnonheap, __nsnaps, __ps, "MemoryNonHeap");
		xnonheap = null;
		
		__StatExport__.__printRow(__ps, "PendingFinalizers (count)", xfin);
		xfin = null;
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
	private final void __memoryUsage(MemoryUsageStatistics[] __xmus,
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
			MemoryUsageStatistics from = __xmus[i];
			
			xinit[i] = from.initbytes;
			xused[i] = from.usedbytes;
			xcomm[i] = from.committedbytes;
			xmaxx[i] = from.maxbytes;
		}
		__xmus = null;
		
		__StatExport__.__printRow(__ps, "%s.init (byte)", __prefix, xinit);
		xinit = null;
		
		__StatExport__.__printRow(__ps, "%s.used (byte)", __prefix, xused);
		xused = null;
		
		__StatExport__.__printRow(__ps, "%s.committed (byte)", __prefix,
			xcomm);
		xcomm = null;
		
		__StatExport__.__printRow(__ps, "%s.max (byte)", __prefix, xmaxx);
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
			final MemoryUsageStatistics[] _collectionusage =
				new MemoryUsageStatistics[__nsnaps];
			
			/** The threshold in bytes of the collection usage. */
			final long[] _collectionusagethresholdbytes =
				new long[__nsnaps];
			
			/** The number of times the collection threshold was reached. */
			final long[] _collectionusagethresholdcount =
				new long[__nsnaps];
			
			/** Peak memory usage. */
			final MemoryUsageStatistics[] _peakusage =
				new MemoryUsageStatistics[__nsnaps];
			
			/** Memory usage. */
			final MemoryUsageStatistics[] _usage =
				new MemoryUsageStatistics[__nsnaps];
			
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
			
			this.__memoryUsage(v._collectionusage, __nsnaps, __ps,
				String.format("MemPool.%s.CollectionUsage", k));
			
			__StatExport__.__printRow(__ps,
				"MemPool.%s.CollectionUsageThreshold (byte)", k,
				v._collectionusagethresholdbytes);
			
			__StatExport__.__printRow(__ps,
				"MemPool.%s.CollectionUsageThresholdHit (count)", k,
				v._collectionusagethresholdcount);
			
			this.__memoryUsage(v._peakusage, __nsnaps, __ps,
				String.format("MemPool.%s.PeakUsage", k));
			
			this.__memoryUsage(v._usage, __nsnaps, __ps,
				String.format("MemPool.%s.Usage", k));
			
			__StatExport__.__printRow(__ps,
				"MemPool.%s.UsageThreshold (byte)", k,
				v._usagethresholdbytes);
			
			__StatExport__.__printRow(__ps,
				"MemPool.%s.UsageThresholdHit (count)", k,
				v._usagethresholdcount);
		}
	}
	
	/**
	 * Dumps thread information.
	 *
	 * @param __xthr The threads to dump.
	 * @param __nsnaps The number of snapshots.
	 * @param __ps The output stream.
	 * @throws IOException On write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/05/24
	 */
	private final void __threads(ThreadStatistics[][] __xthr,
		final int __nsnaps, PrintStream __ps)
	{
		if (__xthr == null || __ps == null)
			throw new NullPointerException();
		
		// Thread data
		class __ThreadData__
		{
			/** The CPU time of the thread. */
			final long[] _cputime =
				new long[__nsnaps];

			/** The user time of the thread. */
			final long[] _usertime =
				new long[__nsnaps];

			/** How many times the thread blocked synching on a monitor. */
			final long[] _blockedcount =
				new long[__nsnaps];

			/** The duration of time spent being blocked. */
			final long[] _blockedtime =
				new long[__nsnaps];

			/** Gets the name of the object being locked. */
			final String[] _lockedname =
				new String[__nsnaps];

			/** The ID number of the thread which owns the lock. */
			final long[] _lockedownerid =
				new long[__nsnaps];

			/** The current state of the thread. */
			final Thread.State[] _state =
				new Thread.State[__nsnaps];

			/** How many times the thread waited on a monitor. */
			final long[] _waitedcount =
				new long[__nsnaps];

			/** How long the thread spent waiting for a monitor. */
			final long[] _waitedtime =
				new long[__nsnaps];
		};
		
		// Multiple threads can exist at once and might disappear through
		// execution
		Map<String, __ThreadData__> mapped = new LinkedHashMap<>();
		for (int i = 0; i < __nsnaps; i++)
		{
			ThreadStatistics[] from = __xthr[i];
			
			// For each state
			for (ThreadStatistics thread : from)
			{
				String key = thread.id + "#" + thread.name;
				
				// Initialize data if missing
				__ThreadData__ data = mapped.get(key);
				if (data == null)
					mapped.put(key, (data = new __ThreadData__()));
				
				// Store it at the index
				data._cputime[i] = thread.cputime;
				data._usertime[i] = thread.usertime;
				data._blockedcount[i] = thread.blockedcount;
				data._blockedtime[i] = thread.blockedtime;
				data._lockedname[i] = thread.lockedname;
				data._lockedownerid[i] = thread.lockedownerid;
				data._state[i] = thread.state;
				data._waitedcount[i] = thread.waitedcount;
				data._waitedtime[i] = thread.waitedtime;
			}
		}
		__xthr = null;
		
		// Print for each key
		for (Map.Entry<String, __ThreadData__> e : mapped.entrySet())
		{
			String k = e.getKey();
			__ThreadData__ v = e.getValue();
			
			__StatExport__.__printRow(__ps, "Thread.%s.CPUTime (ns)", k,
				v._cputime);
			
			__StatExport__.__printRow(__ps, "Thread.%s.UserTime (ns)", k,
				v._usertime);
			
			__StatExport__.__printRow(__ps, "Thread.%s.Blocked (count)", k,
				v._blockedcount);
			
			__StatExport__.__printRow(__ps, "Thread.%s.BlockedTime (ns)", k,
				v._blockedtime);
			
			__StatExport__.__printRow(__ps, "Thread.%s.LockedName (object)", k,
				v._lockedname);
			
			__StatExport__.__printRow(__ps, "Thread.%s.LockedOwner (threadid)",
				k, v._lockedownerid);
			
			__StatExport__.__printRow(__ps, "Thread.%s.State (state)", k,
				v._state);
			
			__StatExport__.__printRow(__ps, "Thread.%s.Waited (count)", k,
				v._waitedcount);
			
			__StatExport__.__printRow(__ps, "Thread.%s.WaitedTime (ns)", k,
				v._waitedtime);
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
		
		__StatExport__.__printRow(__ps, "StartTime (utc ms)", xboot);
		xboot = null;
		
		__StatExport__.__printRow(__ps, "UpTime (ms)", xup);
		xup = null;
	}
	
	/**
	 * Prints a row of values.
	 *
	 * @param __ps The stream to write to.
	 * @param __key The key to write.
	 * @param __v The values to write.
	 * @since 2018/05/24
	 */
	private static void __printRow(PrintStream __ps, String __key, int[] __v)
	{
		__ps.print(__key);
		for (int i = 0, n = __v.length; i < n; i++)
		{
			__ps.print(',');
			__ps.print(__v[i]);
		}
		__ps.println();
	}
	
	/**
	 * Prints a row of values.
	 *
	 * @param __ps The stream to write to.
	 * @param __key The key to write.
	 * @param __v The values to write.
	 * @since 2018/05/24
	 */
	private static void __printRow(PrintStream __ps, String __key, long[] __v)
	{
		__ps.print(__key);
		for (int i = 0, n = __v.length; i < n; i++)
		{
			__ps.print(',');
			__ps.print(__v[i]);
		}
		__ps.println();
	}
	
	/**
	 * Prints a row of values.
	 *
	 * @param __ps The stream to write to.
	 * @param __key The key to write.
	 * @param __v The values to write.
	 * @since 2018/05/24
	 */
	private static void __printRow(PrintStream __ps, String __key,
		Object[] __v)
	{
		__ps.print(__key);
		for (int i = 0, n = __v.length; i < n; i++)
		{
			__ps.print(',');
			
			Object v = __v[i];
			__ps.print((v == null ? "null" : v.toString()));
		}
		__ps.println();
	}
	
	/**
	 * Prints a row of values with an extra key specifier.
	 *
	 * @param __ps The stream to write to.
	 * @param __key The key to write.
	 * @param __spec Extra key specifier.
	 * @param __v The values to write.
	 * @since 2018/05/24
	 */
	private static void __printRow(PrintStream __ps, String __key,
		String __spec, int[] __v)
	{
		__StatExport__.__printRow(__ps, String.format(__key, __spec), __v);
	}
	
	/**
	 * Prints a row of values with an extra key specifier.
	 *
	 * @param __ps The stream to write to.
	 * @param __key The key to write.
	 * @param __spec Extra key specifier.
	 * @param __v The values to write.
	 * @since 2018/05/24
	 */
	private static void __printRow(PrintStream __ps, String __key,
		String __spec, long[] __v)
	{
		__StatExport__.__printRow(__ps, String.format(__key, __spec), __v);
	}
	
	/**
	 * Prints a row of values with an extra key specifier.
	 *
	 * @param __ps The stream to write to.
	 * @param __key The key to write.
	 * @param __spec Extra key specifier.
	 * @param __v The values to write.
	 * @since 2018/05/24
	 */
	private static void __printRow(PrintStream __ps, String __key,
		String __spec, Object[] __v)
	{
		__StatExport__.__printRow(__ps, String.format(__key, __spec), __v);
	}
}

