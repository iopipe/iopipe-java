package com.iopipe;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class reads and provides a snapshot of all the system information which
 * is needed to keep track of measurements.
 *
 * @since 2017/12/19
 */
final class __SystemInfo__
{
	/** The boot ID. */
	protected final String bootid;
	
	/** The hostname. */
	protected final String hostname;
	
	/** The file descriptor count. */
	protected final int fdsize;
	
	/** Kernel time with children. */
	protected final long cstime;
	
	/** User time with children. */
	protected final long cutime;
	
	/** Kernel time. */
	protected final long stime;
	
	/** User time. */
	protected final long utime;
	
	/** Total amount of memory in KiB. */
	protected final long memorytotalkib;
	
	/** Free amount of memory in KiB. */
	protected final long memoryfreekib;
	
	/** The current process ID. */
	protected final int pid;
	
	/** The number of threads that exist. */
	protected final int threads;
	
	/** The resident set size in KiB. */
	protected final long vmrsskib;
	
	/** CPU information. */
	private final __Cpu__[] _cpus;
	
	/**
	 * Creates a snapshot of the system information.
	 *
	 * @since 2017/12/19
	 */
	__SystemInfo__()
	{
		this.hostname = __readFirstLine(Paths.get("/etc/hostname"), "unknown");
		this.bootid = __readFirstLine(
			Paths.get("/proc/sys/kernel/random/boot_id"), "unknown");
		
		// Memory information
		Map<String, String> meminfo = __readMap(Paths.get("/proc/meminfo"));
		this.memorytotalkib = __readLong(
			meminfo.getOrDefault("MemTotal", "0"));
		this.memoryfreekib = __readLong(
			meminfo.getOrDefault("MemFree", "0"));
		
		// Obtain CPU information
		List<__Cpu__> cpus = new ArrayList<>(
			Runtime.getRuntime().availableProcessors());
		Map<String, String> kernelstat = __readMap(Paths.get("/proc/stat"));
		for (int i = 0; i >= 0; i++)
		{
			String val = kernelstat.get("cpu" + i);
			if (val != null)
				cpus.add(new __Cpu__(val));
			else
				break;
		}
		this._cpus = cpus.<__Cpu__>toArray(new __Cpu__[cpus.size()]);
		
		// Parse current process info
		Map<String, String> pidstatus = __readMap(
			Paths.get("/proc/self/status"));
		this.pid = __readInt(pidstatus.getOrDefault("Pid", "0"));
		this.vmrsskib = __readLong(pidstatus.getOrDefault("VmRSS", "0"));
		this.threads = __readInt(pidstatus.getOrDefault("Threads", "0"));
		this.fdsize = __readInt(pidstatus.getOrDefault("FDSize", "0"));
		
		// Process times
		List<String> pidstat = __readValuesFromFile(
			Paths.get("/proc/self/stat"));
		this.cstime = __readLong(pidstat, 13);
		this.cutime = __readLong(pidstat, 14);
		this.stime = __readLong(pidstat, 15);
		this.utime = __readLong(pidstat, 16);
	}
	
	/**
	 * Returns the unique boot identifier.
	 *
	 * @return The boot identifier.
	 * @since 2017/12/19
	 */
	public String bootId()
	{
		return this.bootid;
	}
	
	/**
	 * Returns CPU related information.
	 *
	 * @return CPU information.
	 * @since 2017/12/19
	 */
	public __Cpu__[] cpus()
	{
		return this._cpus.clone();
	}
	
	/**
	 * Returns the amount of time spent in kernelspace including all of the
	 * process children.
	 *
	 * @return The time spent in kernelspace including children.
	 * @since 2017/12/19
	 */
	public long cstime()
	{
		return this.cstime;
	}
	
	/**
	 * Returns the amount of time spent in userspace including all of the
	 * process children.
	 *
	 * @return The time spent in userspace including children.
	 * @since 2017/12/19
	 */
	public long cutime()
	{
		return this.cutime;
	}
	
	/**
	 * Returns the upper size in file descriptors.
	 *
	 * @return The upper file descriptor size.
	 * @since 2017/12/19
	 */
	public int fdSize()
	{
		return this.fdsize;
	}
	
	/**
	 * Returns the hostname of the system.
	 *
	 * @return The system hostname.
	 * @since 2017/12/19
	 */
	public String hostName()
	{
		return this.hostname;
	}
	
	/**
	 * Returns the amount of free memory in KiB.
	 *
	 * @return The free amount of available memory.
	 * @since 2017/12/19
	 */
	public long memoryFreeKiB()
	{
		return this.memoryfreekib;
	}
	
	/**
	 * Returns the amount of total memory in KiB.
	 *
	 * @return The total amount of available memory.
	 * @since 2017/12/19
	 */
	public long memoryTotalKiB()
	{
		return this.memorytotalkib;
	}
	
	/**
	 * Returns the current PID.
	 *
	 * @return The current PID.
	 * @since 2017/12/19
	 */
	public int pid()
	{
		return this.pid;
	}
	
	/**
	 * Returns the amount of time spent in kernelspace.
	 *
	 * @return The time spent in kernelspace.
	 * @since 2017/12/19
	 */
	public long stime()
	{
		return this.stime;
	}
	
	/**
	 * Returns the number of threads which exist.
	 *
	 * @return The number of threads which exist.
	 * @since 2017/12/19
	 */
	public int threads()
	{
		return this.threads;
	}
	
	/**
	 * Returns the amount of time spent in userspace.
	 *
	 * @return The time spent in userspace.
	 * @since 2017/12/19
	 */
	public long utime()
	{
		return this.utime;
	}
	
	/**
	 * Returns the resident set memory in KiB.
	 *
	 * @return The RSS memory.
	 * @since 2017/12/19
	 */
	public long vmRssKiB()
	{
		return this.vmrsskib;
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
				if (!l.isEmpty())
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
	static final class __Cpu__
	{
		/** Idle time. */
		protected final long idle;
		
		/** IRQ time. */
		protected final long irq;
		
		/** Nice time. */
		protected final long nice;
		
		/** System time. */
		protected final long sys;
		
		/** User time. */
		protected final long user;
		
		/**
		 * Initializes the CPU information, decoded from the given string.
		 *
		 * @param __s The string to decode from.
		 * @throws NullPointerException On null arguments.
		 * @since 2017/12/19
		 */
		__Cpu__(String __s)
			throws NullPointerException
		{
			if (__s == null)
				throw new NullPointerException();
			
			List<String> fields = __readValues(__s);
			this.user = __readLong(fields, 1);
			this.nice = __readLong(fields, 2);
			this.sys = __readLong(fields, 3);
			this.idle = __readLong(fields, 4);
			this.irq = __readLong(fields, 6);
		}
		
		/**
		 * Returns time spent doing nothing.
		 *
		 * @return Time spent doing nothing.
		 * @since 2017/12/19
		 */
		public long idle()
		{
			return this.idle;
		}
		
		/**
		 * Returns time spent handling IRQs.
		 *
		 * @return Time spent handling IRQs.
		 * @since 2017/12/19
		 */
		public long irq()
		{
			return this.irq;
		}
		
		/**
		 * Returns time spent in nice process.
		 *
		 * @return Time spent in nice process.
		 * @since 2017/12/19
		 */
		public long nice()
		{
			return this.nice;
		}
		
		/**
		 * Returns time spent in kernelspace.
		 *
		 * @return Time spent in kernelspace.
		 * @since 2017/12/19
		 */
		public long sys()
		{
			return this.sys;
		}
		
		/**
		 * Returns time spent in userspace.
		 *
		 * @return Time spent in userpsace.
		 * @since 2017/12/19
		 */
		public long user()
		{
			return this.user;
		}
	}
}

