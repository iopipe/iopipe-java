package com.iopipe;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.FileStore;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class reads and provides a snapshot of all the system information which
 * is needed to keep track of measurements.
 *
 * @since 2017/12/19
 */
public final class SystemMeasurement
{
	/** Indicates the self process. */
	public static final int SELF_PROCESS =
		Integer.MIN_VALUE;
	
	/** The boot ID. */
	public static final String BOOTID;
	
	/** The hostname. */
	public static final String HOSTNAME;
	
	/** Memory information. */
	public final Memory memory;
	
	/** CPUs. */
	public final List<Cpu> cpus;
	
	/** The process stat. */
	public final Stat stat;
	
	/** Proces times. */
	public final Times times;
	
	/** Temporary path disk usage. */
	public final Disk tempdir;
	
	/**
	 * This caches information which will always be the same regardless.
	 *
	 * @since 2017/12/19
	 */
	static
	{
		HOSTNAME = __readFirstLine(Paths.get("/etc/hostname"), "unknown");
		BOOTID = __readFirstLine(
			Paths.get("/proc/sys/kernel/random/boot_id"), "unknown");
	}
	
	/**
	 * Initializes the measurement snapshot information.
	 *
	 * @param __mem The memory information.
	 * @param __cpus CPU information,
	 * @param __times The process time information.
	 * @param __stat The process stat information.
	 * @param __tempdir Temporary directory usage information.
	 * @since 2017/12/19
	 */
	public SystemMeasurement(Memory __mem, Collection<Cpu> __cpus,
		Times __times, Stat __stat, Disk __tempdir)
	{
		this.memory = (__mem == null ? new Memory(0, 0) : __mem);
		this.cpus = Collections.<Cpu>unmodifiableList(Arrays.<Cpu>asList(
			(__cpus == null ? new Cpu[0] :
			__cpus.<Cpu>toArray(new Cpu[__cpus.size()]))));
		this.times = (__times == null ? new Times(0, 0, 0, 0) : __times);
		this.stat = (__stat == null ? new Stat(0, 0, 0, 0) : __stat);
		this.tempdir = (__tempdir == null ? new Disk(Paths.get(""), 0, 0) :
			__tempdir);
	}
	
	/**
	 * Performs all measurements.
	 *
	 * @return The measurements performed.
	 * @since 2018/05/17
	 */
	public static SystemMeasurement measure()
	{
		return new SystemMeasurement(measureMemory(), measureCPUs(),
			measureTimes(SELF_PROCESS), measureStat(SELF_PROCESS),
			measureDisk(Paths.get("/tmp")));
	}
	
	/**
	 * Measures disk usage at the given path.
	 *
	 * @param __p The path to measure.
	 * @return The disk usage for the given path.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/05/17
	 */
	public static Disk measureDisk(Path __p)
		throws NullPointerException
	{
		if (__p == null)
			throw new NullPointerException();
		
		try
		{
			FileStore store = Files.getFileStore(__p);
			
			// Usable space is the amount of space that can be used by the virtual
			// machine, which may include quotas and any other kind of limit
			long total = store.getTotalSpace(),
				useable = store.getUsableSpace();
			
			return new Disk(__p, total, total - useable);
		}
		
		// Failed to read, just use a dummy object
		catch (IOException e)
		{
			return new Disk(__p, 0, 0);
		}
	}
	
	/**
	 * Measures CPU information.
	 *
	 * @return The CPU information.
	 * @since 2018/05/17
	 */
	public static List<Cpu> measureCPUs()
	{
		// Obtain CPU information
		List<Cpu> cpus = new ArrayList<>(
			Runtime.getRuntime().availableProcessors());
		Map<String, String> kernelstat = __readMap(Paths.get("/proc/stat"));
		for (int i = 0; i >= 0; i++)
		{
			String val = kernelstat.get("cpu" + i);
			if (val != null)
			{
				List<String> fields = __readValues(val);
				int user = __readInt(fields, 1),
					nice = __readInt(fields, 2),
					sys = __readInt(fields, 3),
					idle = __readInt(fields, 4),
					irq = __readInt(fields, 6);
				
				cpus.add(new Cpu(user, nice, sys, idle, irq));
			}
			else
				break;
		}
		
		return cpus;
	}
	
	/**
	 * Measures the memory information on the system.
	 *
	 * @return The memory measurement.
	 * @since 2018/05/17
	 */
	public static Memory measureMemory()
	{
		// Memory information
		Map<String, String> meminfo = __readMap(Paths.get("/proc/meminfo"));
		long mtkib = (mtkib = __readInt(
			meminfo.getOrDefault("MemTotal", "0")));
		long mfkib = (mfkib = __readInt(
			meminfo.getOrDefault("MemFree", "0")));
		
		// Memory information is in KiB, so just multiply the values for now
		return new Memory(mtkib * 1024L, mfkib * 1024L);
	}
	
	/**
	 * Measures stat for the given process.
	 *
	 * @param __id The process ID, {@code SELF_PROCESS} means the current
	 * process.
	 * @return The stat for the given process.
	 * @since 2018/05/17
	 */
	public static Stat measureStat(int __id)
	{
		// Parse current process info
		Map<String, String> pidstatus = __readMap(
			Paths.get("/proc/" + (__id == SELF_PROCESS ? "self" : __id) +
			"/status"));
		return new Stat(__readInt(pidstatus.getOrDefault("Pid", "0")),
			__readInt(pidstatus.getOrDefault("FDSize", "0")),
			__readInt(pidstatus.getOrDefault("Threads", "0")),
			__readInt(pidstatus.getOrDefault("VmRSS", "0")));
	}
	
	/**
	 * Measures the given process times.
	 *
	 * @param __id The process ID, {@code SELF_PROCESS} means the current
	 * process.
	 * @return The process times for the given process.
	 * @since 2018/05/17
	 */
	public static Times measureTimes(int __id)
	{
		List<String> pidstat = __readValuesFromFile(
			Paths.get("/proc/" + (__id == SELF_PROCESS ? "self" : __id) +
			"/stat"));
		return new Times(__readInt(pidstat, 13), __readInt(pidstat, 14),
			__readInt(pidstat, 15), __readInt(pidstat, 16));
	}
	
	/**
	 * Reads the first non-empty line for the given path.
	 *
	 * @param __p The path to read.
	 * @param __def Default value if it could not be read.
	 * @return The first non-empty line or {@code __def} if the file could not
	 * be read or has only empty lines.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/17
	 */
	private static String __readFirstLine(Path __p, String __def)
		throws NullPointerException
	{
		if (__p == null)
			throw new NullPointerException();
		
		try
		{
			for (String l : Files.readAllLines(__p))
			{
				l = l.trim();
				if (!l.isEmpty())
					return l;
			}
			
			return __def;
		}
		
		catch (IOException e)
		{
			return __def;
		}
	}
	
	/**
	 * Decodes an integer value from the specified string.
	 *
	 * @param __s The string to decode a value from.
	 * @return The decoded integer value.
	 * @since 2017/12/19
	 */
	public static int __readInt(String __s)
	{
		long rv = __readLong(__s);
		if (rv < Integer.MIN_VALUE)
			return Integer.MIN_VALUE;
		else if (rv > Integer.MAX_VALUE)
			return Integer.MAX_VALUE;
		return (int)rv;
	}
	
	/**
	 * Reads an int value from the given list
	 *
	 * @param __l The list to read values from.
	 * @param __dx The index of the element to read.
	 * @return The decoded int value.
	 * @since 2017/12/19
	 */
	public static int __readInt(List<String> __l, int __dx)
	{
		int n = __l.size();
		if (__l == null || __dx < 0 || __dx >= n)
			return 0;
		
		return __readInt(__l.get(__dx));
	}
	
	/**
	 * Decodes a long value from the specified string.
	 *
	 * @param __s The string to decode a value from.
	 * @return The decoded long value or {@code 0} if it is not valid.
	 * @since 2017/12/19
	 */
	public static long __readLong(String __s)
	{
		if (__s == null)
			return 0;
		
		// There may be extra data following a space
		int sp = __s.indexOf(' ');
		if (sp >= 0)
			__s = __s.substring(0, sp);
		
		try
		{
			return Long.parseLong(__s);
		}
		catch (NumberFormatException e)
		{
			return 0L;
		}
	}
	
	/**
	 * Reads a long value from the given list
	 *
	 * @param __l The list to read values from.
	 * @param __dx The index of the element to read.
	 * @return The decoded long value.
	 * @since 2017/12/19
	 */
	public static long __readLong(List<String> __l, int __dx)
	{
		int n = __l.size();
		if (__l == null || __dx < 0 || __dx >= n)
			return 0L;
		
		return __readLong(__l.get(__dx));
	}
	
	/**
	 * Parses a map-like structure of keys and values from the given path.
	 *
	 * @param __p The path to decode.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/19
	 */
	private static Map<String, String> __readMap(Path __p)
		throws NullPointerException
	{
		if (__p == null)
			throw new NullPointerException("NARG");
		
		Map<String, String> rv = new LinkedHashMap<>();
		
		try
		{
			for (String l : Files.readAllLines(__p))
			{
				l = l.trim();
				if (l.isEmpty())
					continue;
				
				// Determine splice point which is earliest of the colon
				// or the space
				int fc = l.indexOf(':'),
					fs = l.indexOf(' ');
				int splice = ((fc < 0) || (fs >= 0 && fs < fc) ? fs : fc);
				
				rv.put(l.substring(0, splice).trim(),
					l.substring(splice + 1).trim());
			}
		}
		catch (IOException e)
		{
		}
		
		return rv;
	}
	
	/**
	 * Reads a number of space separated values from a file.
	 *
	 * @param __s The string to decode from.
	 * @return The list of values which are separated by space.
	 * @since 2017/12/19
	 */
	private static List<String> __readValues(String __s)
	{
		if (__s == null)
			throw new NullPointerException("NARG");
		
		return Arrays.<String>asList(__s.split(" "));
	}
	
	/**
	 * Reads a number of space separated values from a file.
	 *
	 * @param __p The path to read from.
	 * @return The list of values which are separated by space.
	 * @since 2017/12/19
	 */
	private static List<String> __readValuesFromFile(Path __p)
	{
		if (__p == null)
			throw new NullPointerException("NARG");
		
		return __readValues(__readFirstLine(__p, ""));
	}
	
	/**
	 * Contains information about a single CPU.
	 *
	 * @since 2017/12/19
	 */
	public static final class Cpu
	{
		/** Idle time. */
		public final int idle;
		
		/** IRQ time. */
		public final int irq;
		
		/** Nice time. */
		public final int nice;
		
		/** System time. */
		public final int sys;
		
		/** User time. */
		public final int user;
		
		/**
		 * Initializes the CPU information.
		 *
		 * @param __user The user usage.
		 * @param __nice The nice usage.
		 * @param __sys The system usage.
		 * @param __idle The idle usage.
		 * @param __irq The IRQ usage.
		 * @since 2017/12/19
		 */
		public Cpu(int __user, int __nice, int __sys, int __idle, int __irq)
		{
			this.user = __user;
			this.nice = __nice;
			this.sys = __sys;
			this.idle = __idle;
			this.irq = __irq;
		}
	}
	
	/**
	 * This contains the information on disk usage for a given path.
	 *
	 * @since 2018/05/17
	 */
	public static final class Disk
	{
		/** The path this represents. */
		public final Path path;
		
		/** The total number of bytes. */
		public final long totalbytes;
		
		/** The used number of bytes. */
		public final long usedbytes;
		
		/** The free number of bytes. */
		public final long freebytes;
		
		/** The total number of MiB. */
		public final double totalmib;
		
		/** The used number of MiB. */
		public final double usedmib;
		
		/** The free number of MiB. */
		public final double freemib;
		
		/** The percentage of used space against total space. */
		public final double usedpercent;
		
		/**
		 * Initializes the disk usage information.
		 *
		 * @param __p The disk path.
		 * @param __totalbytes The number of used bytes.
		 * @param __usedbytes The number of used bytes.
		 * @throws NullPointerException On null arguments.
		 * @since 2018/05/17
		 */
		public Disk(Path __p, long __totalbytes, long __usedbytes)
			throws NullPointerException
		{
			if (__p == null)
				throw new NullPointerException();
			
			this.path = __p;
			
			this.totalbytes = __totalbytes;
			this.usedbytes = __usedbytes;
			this.freebytes = (__totalbytes - __usedbytes);
			
			this.totalmib = (double)__totalbytes / 1048576.0;
			this.usedmib = (double)__usedbytes / 1048576.0;
			this.freemib = (__totalbytes - __usedbytes) / 1048576.0;
			
			this.usedpercent = Double.min(1.0, Double.max(0.0,
				(double)__usedbytes / (double)__totalbytes));
		}
	}
	
	/**
	 * This contains the memory information.
	 *
	 * @since 2018/05/17
	 */
	public static final class Memory
	{
		/** Total amount of memory in bytes. */
		public final long totalbytes;
		
		/** Total amount of memory in KiB. */
		public final int totalkib;
		
		/** Free amount of memory in bytes. */
		public final long freebytes;
		
		/** Free amount of memory in KiB. */
		public final int freekib;
		
		/** The number of used bytes. */
		public final long usedbytes;
		
		/** The number of used KiB. */
		public final int usedkib;
		
		/**
		 * Initializes the memory information.
		 *
		 * @param __total The total number of bytes.
		 * @param __free The free number of bytes.
		 * @since 2018/05/17
		 */
		public Memory(long __total, long __free)
		{
			this.totalbytes = __total;
			this.freebytes = __free;
			this.usedbytes = __total - __free;
			
			this.totalkib = (int)Long.min(Integer.MAX_VALUE, __total / 1024);
			this.freekib = (int)Long.min(Integer.MAX_VALUE, __free / 1024);
			this.usedkib = (int)Long.min(Integer.MAX_VALUE,
				(__total - __free) / 1024);
		}
	}
	
	/**
	 * Process stat information.
	 *
	 * @since 2018/05/17
	 */
	public static final class Stat
	{
		/** The current process ID. */
		public final int pid;
		
		/** The file descriptor count. */
		public final int fdsize;
		
		/** The number of threads that exist. */
		public final int threads;
		
		/** The resident set size in KiB. */
		public final int vmrsskib;
		
		/**
		 * Initializes the stat information.
		 *
		 * @param __pid The current process ID.
		 * @param __fdsize The file descriptor count.
		 * @param __threads The number of threads that exist.
		 * @param __vmrsskib The resident set size in KiB.
		 * @since 2018/05/17
		 */
		public Stat(int __pid, int __fdsize, int __threads, int __vmrsskib)
		{
			this.pid = __pid;
			this.fdsize = __fdsize;
			this.threads = __threads;
			this.vmrsskib = __vmrsskib;
		}
	}
	
	/**
	 * This contains the process time information.
	 *
	 * @since 2017/12/19
	 */
	public static final class Times
	{
		/** Kernel time with children. */
		public final int cstime;
	
		/** User time with children. */
		public final int cutime;
	
		/** Kernel time. */
		public final int stime;
	
		/** User time. */
		public final int utime;
		
		/**
		 * Initializes the snapshot of the process times.
		 *
		 * @param __cstime Kernel time with children.
		 * @param __cutime User time with children.
		 * @param __stime Kernel time.
		 * @param __utime User time.
		 * @since 2017/12/19
		 */
		public Times(int __cstime, int __cutime, int __stime, int __utime)
		{
			this.cstime = __cstime;
			this.cutime = __cutime;
			this.stime = __stime;
			this.utime = __utime;
		}
	}
}

