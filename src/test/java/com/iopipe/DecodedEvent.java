package com.iopipe;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;

/**
 * This class contains a representation of the event that was sent to the
 * IOpipe service and decodes it into an object which simplifies testing.
 *
 * @since 2018/07/10
 */
public final class DecodedEvent
{
	/** Project token. */
	public final String token;
	
	/** Install method. */
	public final String installmethod;
	
	/** Duration. */
	public final long duration;
	
	/** System information stat. */
	public final Stat stat;
	
	/** Process ID. */
	public final int processid;
	
	/** Timestamp start. */
	public final long timestamp;
	
	/** Timestamp end. */
	public final long timestampend;
	
	/** AWS information. */
	public final AWS aws;
	
	/** Disk usage information. */
	public final Disk disk;
	
	/** Environment. */
	public final Environment environment;
	
	/** Errors. */
	public final Errors errors;
	
	/** Is this a cold start? */
	public final boolean coldstart;
	
	/** Custom metrics. */
	public final Map<String, CustomMetric> custommetrics;
	
	/** Performance entries. */
	public final Map<String, PerformanceEntry> performanceentries;
	
	/** Labels. */
	public final Set<String> labels;
	
	/** Plugins. */
	public final Map<String, Plugin> plugins;
	
	/**
	 * Intializes the event.
	 *
	 * @since 2018/07/13
	 */
	public DecodedEvent()
	{
		throw new Error("TODO");
	}
	
	/**
	 * Does this event have an error?
	 *
	 * @return If there is an error.
	 * @since 2018/07/10
	 */
	public final boolean hasError()
	{
		throw new Error("TODO");
	}
	
	/**
	 * Decodes the specified event.
	 *
	 * @param __data The event to decode.
	 * @return The decoded event.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/07/10
	 */
	public static DecodedEvent decode(String __data)
		throws NullPointerException
	{
		if (__data == null)
			throw new NullPointerException();
		
		try (StringReader r = new StringReader(__data))
		{
			return DecodedEvent.decode(
				((JsonObject)(Json.createReader(r).read())));
		}
	}
	
	/**
	 * Decodes the specified event.
	 *
	 * @param __data The event to decode.
	 * @return The decoded event.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/07/10
	 */
	public static DecodedEvent decode(JsonObject __data)
		throws NullPointerException
	{
		if (__data == null)
			throw new NullPointerException();
		
		throw new Error("TODO");
	}
	
	/**
	 * Agent information.
	 *
	 * @since 2018/07/13
	 */
	public static final class Agent
	{
		/** Runtime. */
		public final String runtime;
		
		/** Version. */
		public final String version;
		
		/** Load time. */
		public final long loadtime;
		
		/**
		 * Initializes the agent information.
		 *
		 * @since 2018/07/13
		 */
		public Agent()
		{
			throw new Error("TODO");
		}
	}
	
	/**
	 * AWS Information.
	 *
	 * @since 2018/07/13
	 */
	public static final class AWS
	{
		/** Function name. */
		public final String functionname;
		
		/** Function version. */
		public final String functionversion;
		
		/** Request ID. */
		public final String requestid;
		
		/** Invoked function ARN. */
		public final String invokedfunctionarn;
		
		/** Log group name. */
		public final String loggroupname;
		
		/** Log stream name. */
		public final String logstreamname;
		
		/** Memory limit in mibs. */
		public final long memorylimitmib;
		
		/** Remaining time in milliseconds. */
		public final long remainingtime;
		
		/** Trace ID. */
		public final String traceid;
		
		/**
		 * Initializes the AWS information.
		 *
		 * @since 2018/07/13 
		 */
		public AWS()
		{
			throw new Error("TODO");
		}
	}
	
	/**
	 * CPU information.
	 *
	 * @since 2018/07/13
	 */
	public static final class CPU
	{
		/** Times for this CPU. */
		public final Times times;
		
		/**
		 * Initializes the CPU information.
		 *
		 * @since 2018/07/13
		 */
		public CPU()
		{
			throw new Error("TODO");
		}
	}
	
	/**
	 * Disk information.
	 *
	 * @since 2018/07/13
	 */
	public static final class Disk
	{
		/** Total MiB. */
		public final long totalmib;
		
		/** Used MiB. */
		public final long usedmib;
		
		/** Used percentage. */
		public final double usedpercentage;
		
		/**
		 * Initializes the disk information.
		 *
		 * @since 2018/07/13
		 */
		public Disk()
		{
			throw new Error("TODO");
		}
	}
	
	/**
	 * Contains the environment information.
	 *
	 * @since 2018/07/13
	 */
	public static final class Environment
	{
		/** Agent information. */
		public final Agent agent;
		
		/** Runtime information. */
		public final Runtime runtime;
		
		/** Host information. */
		public final Host host;
		
		/** OS information. */
		public final OS os;
		
		/**
		 * Initializes the environment information.
		 *
		 * @since 2018/07/13
		 */
		public Environment()
		{
			throw new Error("TODO");
		}
	}
	
	/**
	 * Contains error information.
	 *
	 * @since 2018/07/13
	 */
	public static final class Errors
	{
		/** Stack trace. */
		public final String stack;
		
		/** Name of the error. */
		public final String name;
		
		/** Message of the error. */
		public final String message;
		
		/**
		 * Initializes error information.
		 *
		 * @since 2018/07/13
		 */
		public Errors()
		{
			throw new Error("TODO");
		}
	}
	
	/**
	 * Host information.
	 *
	 * @since 2018/07/13
	 */
	public static final class Host
	{
		/** Boot ID. */
		public final String bootid;
		
		/**
		 * Initializes the host information.
		 *
		 * @since 2018/07/13
		 */
		public Host()
		{
			throw new Error("TODO");
		}
	}
	
	/**
	 * Linux information.
	 *
	 * @since 2018/07/13
	 */
	public static final class Linux
	{
		/** PID. */
		public final Map<String, Pid> pids;
		
		/**
		 * Initializes the Linux information.
		 *
		 * @since 2018/07/13
		 */
		public Linux()
		{
			throw new Error("TODO");
		}
	}
	
	/**
	 * Memory information.
	 *
	 * @since 2018/07/13
	 */
	public static final class Memory
	{
		/** Total memory. */
		public final long totalbytes;
		
		/** Free memory. */
		public final long freebytes;
		
		/** Used memory. */
		public final long usedbytes;
		
		/**
		 * Initializes memory information.
		 *
		 * @since 2018/07/13
		 */
		public Memory()
		{
			throw new Error("TODO");
		}
	}
	
	/**
	 * OS information.
	 *
	 * @since 2018/07/13
	 */
	public static final class OS
	{
		/** Hostname. */
		public final String hostname;
		
		/** Memory information. */
		public final Memory memory;
		
		/** CPU information. */
		public final List<CPU> cpus;
		
		/** Linux information. */
		public final Linux linux;
		
		/**
		 * Initializes the OS information.
		 *
		 * @since 2018/07/13
		 */
		public OS()
		{
			throw new Error("TODO");
		}
	}
	
	/**
	 * Pid information.
	 *
	 * @since 2018/07/13
	 */
	public static final class Pid
	{
		/** End stat information. */
		public final Stat stat;
		
		/** Start stat information. */
		public final Stat statstart;
		
		/** Status information. */
		public final Status status;
		
		/**
		 * Initializes the PID information.
		 *
		 * @since 2018/07/13
		 */
		public Pid()
		{
			throw new Error("TODO");
		}
	}
	
	/**
	 * The runtime used.
	 *
	 * @since 2018/07/13
	 */
	public static final class Runtime
	{
		/** Name. */
		public final String name;
		
		/** Version. */
		public final String version;
		
		/** Vendor. */
		public final String vendor;
		
		/** VM Vendor. */
		public final String vmvendor;
		
		/** VM Version. */
		public final String vmversion;
		
		/**
		 * Initializes the runtime.
		 *
		 * @since 2018/07/13
		 */
		public Runtime()
		{
			throw new Error("TODO");
		}
	}
	
	/**
	 * This represents information about a plugin.
	 *
	 * @since 2018/07/10
	 */
	public static final class Plugin
	{
		/** Name. */
		public final String name;
		
		/** Version. */
		public final String version;
		
		/** Homepage. */
		public final String homepage;
		
		/** Is the plugin enabled? */
		public final boolean enabled;
		
		/**
		 * Initializes the plugin information.
		 *
		 * @since 2018/07/13
		 */
		public Plugin()
		{
			throw new Error("TODO");
		}
	}
	
	/**
	 * Stat information.
	 *
	 * @since 2018/07/13
	 */
	public static final class Stat
	{
		/** User time. */
		public final long utime;
		
		/** System time. */
		public final long stime;
		
		/** User time with children. */
		public final long cutime;
		
		/** System time with children. */
		public final long cstime;
		
		/**
		 * Initializes stat information.
		 *
		 * @since 2018/07/13
		 */
		public Stat()
		{
			throw new Error("TODO");
		}
	}
	
	/**
	 * Status information.
	 *
	 * @since 2018/07/13
	 */
	public static final class Status
	{
		/** VmRSS. */
		public final long vmrss;
		
		/** Threads. */
		public final int threads;
		
		/** FDSize. */
		public final int fdsize;
		
		/**
		 * Initialize status information.
		 *
		 * @since 2108/07/13
		 */
		public Status()
		{
			throw new Error("TODO");
		}
	}
	
	/**
	 * CPU time information.
	 *
	 * @since 2018/07/13
	 */
	public static final class Times
	{
		/** Idle. */
		public final long idle;
		
		/** IRQ. */
		public final long irq;
		
		/** System. */
		public final long sys;
		
		/** User. */
		public final long user;
		
		/** Nice. */
		public final long nice;
		
		/**
		 * Initializes the times information.
		 *
		 * @since 2108/07/13
		 */
		public Times()
		{
			throw new Error("TODO");
		}
	}
}

