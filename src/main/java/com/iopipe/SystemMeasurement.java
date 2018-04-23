package com.iopipe;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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
	/** The boot ID. */
	public static final String BOOTID;
	
	/** The hostname. */
	public static final String HOSTNAME;
	
	/** The file descriptor count. */
	public final int fdsize;
	
	/** Total amount of memory in bytes. */
	public final long memorytotalbytes;
	
	/** Total amount of memory in KiB. */
	public final int memorytotalkib;
	
	/** Free amount of memory in bytes. */
	public final long memoryfreebytes;
	
	/** Free amount of memory in KiB. */
	public final int memoryfreekib;
	
	/** The current process ID. */
	public final int pid;
	
	/** The number of threads that exist. */
	public final int threads;
	
	/** The resident set size in KiB. */
	public final int vmrsskib;
	
	/** CPU information. */
	public final List<Cpu> cpus;
	
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
	 * Creates a snapshot of the system information.
	 *
	 * @since 2017/12/19
	 */
	public SystemMeasurement()
	{
		// Memory information
		Map<String, String> meminfo = __readMap(Paths.get("/proc/meminfo"));
		int mtkib, mfkib;
		this.memorytotalkib = (mtkib = __readInt(
			meminfo.getOrDefault("MemTotal", "0")));
		this.memoryfreekib = (mfkib = __readInt(
			meminfo.getOrDefault("MemFree", "0")));
		
		// Memory information is in KiB, so just multiply the values for now
		this.memorytotalbytes = mtkib * 1024L;
		this.memoryfreebytes = mfkib * 1024L;
		
		// Obtain CPU information
		List<Cpu> cpus = new ArrayList<>(
			Runtime.getRuntime().availableProcessors());
		Map<String, String> kernelstat = __readMap(Paths.get("/proc/stat"));
		for (int i = 0; i >= 0; i++)
		{
			String val = kernelstat.get("cpu" + i);
			if (val != null)
				cpus.add(new Cpu(val));
			else
				break;
		}
		this.cpus = Collections.<Cpu>unmodifiableList(cpus);
		
		// Parse current process info
		Map<String, String> pidstatus = __readMap(
			Paths.get("/proc/self/status"));
		this.pid = __readInt(pidstatus.getOrDefault("Pid", "0"));
		this.vmrsskib = __readInt(pidstatus.getOrDefault("VmRSS", "0"));
		this.threads = __readInt(pidstatus.getOrDefault("Threads", "0"));
		this.fdsize = __readInt(pidstatus.getOrDefault("FDSize", "0"));
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
		 * Initializes the CPU information, decoded from the given string.
		 *
		 * @param __s The string to decode from.
		 * @throws NullPointerException On null arguments.
		 * @since 2017/12/19
		 */
		Cpu(String __s)
			throws NullPointerException
		{
			if (__s == null)
				throw new NullPointerException();
			
			List<String> fields = __readValues(__s);
			this.user = __readInt(fields, 1);
			this.nice = __readInt(fields, 2);
			this.sys = __readInt(fields, 3);
			this.idle = __readInt(fields, 4);
			this.irq = __readInt(fields, 6);
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
		 * @since 2017/12/19
		 */
		public Times()
		{
			List<String> pidstat = __readValuesFromFile(
				Paths.get("/proc/self/stat"));
			this.cstime = __readInt(pidstat, 13);
			this.cutime = __readInt(pidstat, 14);
			this.stime = __readInt(pidstat, 15);
			this.utime = __readInt(pidstat, 16);
		}
	}
}

