package com.iopipe;

/**
 * This class reads and provides a snapshot of all the system information which
 * is needed to keep track of measurements.
 *
 * @since 2017/12/19
 */
final class __SystemInfo__
{
	/**
	 * Creates a snapshot of the system information.
	 *
	 * @since 2017/12/19
	 */
	__SystemInfo__()
	{
		throw new Error("TODO");
		/*
		String bootid = IOPipeMeasurement.__readFirstLine(
		Paths.get("/proc/sys/kernel/random/boot_id"));
		if (bootid != null)
			gen.write("boot_id", bootid);
		*/
	}
	
	/**
	 * Returns the unique boot identifier.
	 *
	 * @return The boot identifier.
	 * @since 2017/12/19
	 */
	public String bootId()
	{
		throw new Error("TODO");
	}
	
	/**
	 * Returns CPU related information.
	 *
	 * @return CPU information.
	 * @since 2017/12/19
	 */
	public __Cpu__[] cpus()
	{
		throw new Error("TODO");
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
		throw new Error("TODO");
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
		throw new Error("TODO");
	}
	
	/**
	 * Returns the upper size in file descriptors.
	 *
	 * @return The upper file descriptor size.
	 * @since 2017/12/19
	 */
	public int fdSize()
	{
		throw new Error("TODO");
	}
	
	/**
	 * Returns the hostname of the system.
	 *
	 * @return The system hostname.
	 * @since 2017/12/19
	 */
	public long hostName()
	{
		throw new Error("TODO");
	}
	
	/**
	 * Returns the amount of free memory in KiB.
	 *
	 * @return The free amount of available memory.
	 * @since 2017/12/19
	 */
	public long memoryFreeKiB()
	{
		throw new Error("TODO");
	}
	
	/**
	 * Returns the amount of total memory in KiB.
	 *
	 * @return The total amount of available memory.
	 * @since 2017/12/19
	 */
	public long memoryTotalKiB()
	{
		throw new Error("TODO");
	}
	
	/**
	 * Returns the current PID.
	 *
	 * @return The current PID.
	 * @since 2017/12/19
	 */
	public int pid()
	{
		throw new Error("TODO");
	}
	
	/**
	 * Returns the amount of time spent in kernelspace.
	 *
	 * @return The time spent in kernelspace.
	 * @since 2017/12/19
	 */
	public long stime()
	{
		throw new Error("TODO");
	}
	
	/**
	 * Returns the number of threads which exist.
	 *
	 * @return The number of threads which exist.
	 * @since 2017/12/19
	 */
	public int threads()
	{
		throw new Error("TODO");
	}
	
	/**
	 * Returns the amount of time spent in userspace.
	 *
	 * @return The time spent in userspace.
	 * @since 2017/12/19
	 */
	public long utime()
	{
		throw new Error("TODO");
	}
	
	/**
	 * Returns the resident set memory in KiB.
	 *
	 * @return The RSS memory.
	 * @since 2017/12/19
	 */
	public long vmRssKiB()
	{
		throw new Error("TODO");
	}
	
	/**
	 * Contains information about a single CPU.
	 *
	 * @since 2017/12/19
	 */
	static final class __Cpu__
	{
		/**
		 * Returns time spent doing nothing.
		 *
		 * @return Time spent doing nothing.
		 * @since 2017/12/19
		 */
		public long idle()
		{
			throw new Error("TODO");
		}
		
		/**
		 * Returns time spent handling IRQs.
		 *
		 * @return Time spent handling IRQs.
		 * @since 2017/12/19
		 */
		public long irq()
		{
			throw new Error("TODO");
		}
		
		/**
		 * Returns time spent in nice process.
		 *
		 * @return Time spent in nice process.
		 * @since 2017/12/19
		 */
		public long nice()
		{
			throw new Error("TODO");
		}
		
		/**
		 * Returns time spent in kernelspace.
		 *
		 * @return Time spent in kernelspace.
		 * @since 2017/12/19
		 */
		public long sys()
		{
			throw new Error("TODO");
		}
		
		/**
		 * Returns time spent in userspace.
		 *
		 * @return Time spent in userpsace.
		 * @since 2017/12/19
		 */
		public long user()
		{
			throw new Error("TODO");
		}
	}
}

