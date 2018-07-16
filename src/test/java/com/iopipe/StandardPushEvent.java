package com.iopipe;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
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
public final class StandardPushEvent
	implements Event
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
	 * @param __token Token.
	 * @param __installmethod Installation method.
	 * @param __duration Duration spen in invocation.
	 * @param __stat System Stat.
	 * @param __processid Process ID.
	 * @param __timestamp Starting timestamp,
	 * @param __timestampend Ending timestamp.
	 * @param __aws AWS information.
	 * @param __disk Disk usage.
	 * @param __environment Environment of the invocation.
	 * @param __errors Error recorded.
	 * @param __coldstart Is this a cold start?
	 * @param __custommetrics Custom metrics recorded.
	 * @param __performanceentries Performance entries measured.
	 * @param __labels Labels recorded.
	 * @param __plugins Plugins used.
	 * @since 2018/07/13
	 */
	public StandardPushEvent(String __token, String __installmethod,
		long __duration, Stat __stat, int __processid, long __timestamp,
		long __timestampend, AWS __aws, Disk __disk, Environment __environment,
		Errors __errors, boolean __coldstart,
		Map<String, CustomMetric> __custommetrics,
		Map<String, PerformanceEntry> __performanceentries,
		Set<String> __labels, Map<String, Plugin> __plugins)
	{
		this.token = __token;
		this.installmethod = __installmethod;
		this.duration = __duration;
		this.stat = __stat;
		this.processid = __processid;
		this.timestamp = __timestamp;
		this.timestampend = __timestampend;
		this.aws = __aws;
		this.disk = __disk;
		this.environment = __environment;
		this.errors = __errors;
		this.coldstart = __coldstart;
		
		this.custommetrics = Collections.<String, CustomMetric>unmodifiableMap(
			(__custommetrics == null ? new LinkedHashMap<>() :
			new LinkedHashMap<>(__custommetrics)));
		this.performanceentries = Collections.<String, PerformanceEntry>
			unmodifiableMap((__performanceentries == null ?
			new LinkedHashMap<String, PerformanceEntry>() :
			new LinkedHashMap<>(__performanceentries)));
		this.labels = Collections.<String>unmodifiableSet(
			(__labels == null ? new LinkedHashSet<String>() :
			new LinkedHashSet<>(__labels)));
		this.plugins = Collections.<String, Plugin>unmodifiableMap(
			(__plugins == null ? new LinkedHashMap<String, Plugin>() :
			new LinkedHashMap<>(__plugins)));
	}
	
	/**
	 * Does this event have an error?
	 *
	 * @return If there is an error.
	 * @since 2018/07/10
	 */
	public final boolean hasError()
	{
		return this.errors != null;
	}
	
	/**
	 * Decodes the specified event.
	 *
	 * @param __data The event to decode.
	 * @return The decoded event.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/07/10
	 */
	public static StandardPushEvent decode(String __data)
		throws NullPointerException
	{
		if (__data == null)
			throw new NullPointerException();
		
		try (StringReader r = new StringReader(__data))
		{
			return StandardPushEvent.decode(
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
	public static StandardPushEvent decode(JsonObject __data)
		throws NullPointerException
	{
		if (__data == null)
			throw new NullPointerException();
		
		String token = null;
		String installmethod = null;
		long duration = Long.MIN_VALUE;
		Stat stat = null;
		int processid = Integer.MIN_VALUE;
		long timestamp = Long.MIN_VALUE;
		long timestampend = Long.MIN_VALUE;
		AWS aws = null;
		Disk disk = null;
		Environment environment = null;
		Errors errors = null;
		boolean coldstart = false;
		Map<String, CustomMetric> custommetrics = new LinkedHashMap<>();
		Map<String, PerformanceEntry> performanceentries =
			new LinkedHashMap<>();
		Set<String> labels = new LinkedHashSet<>();
		Map<String, Plugin> plugins = new LinkedHashMap<>();
		
		for (Map.Entry<String, JsonValue> e : __data.entrySet())
		{
			JsonValue v = e.getValue();
			
			// Check and only use valid keys
			String k;
			switch ((k = e.getKey()))
			{
				case "client_id":
					token = ((JsonString)v).getString();
					break;
				
				case "installMethod":
					installmethod = ((JsonString)v).getString();
					break;
				
				case "duration":
					duration = ((JsonNumber)v).longValue();
					break;
				
				case "processId":
					processid = ((JsonNumber)v).intValue();
					break;
				
				case "timestamp":
					timestamp = ((JsonNumber)v).longValue();
					break;
				
				case "timestampEnd":
					timestampend = ((JsonNumber)v).longValue();
					break;
				
				case "aws":
					aws = AWS.decodeEvent((JsonObject)v);
					break;
				
				case "disk":
					disk = Disk.decodeEvent((JsonObject)v);
					break;
				
				case "environment":
					environment = Environment.decodeEvent((JsonObject)v);
					break;
				
				case "errors":
					errors = Errors.decodeEvent((JsonObject)v);
					break;
				
				case "coldstart":
					coldstart = JsonValue.TRUE.equals(v);
					break;
				
				case "custom_metrics":
					for (JsonValue w : (JsonArray)v)
					{
						CustomMetric m = StandardPushEvent.decodeCustomMetric(
							((JsonObject)w));
						custommetrics.put(m.name(), m);
					}
					break;
				
				case "performanceEntries":
					for (JsonValue w : (JsonArray)v)
					{
						PerformanceEntry p = StandardPushEvent.
							decodePerformanceEntry((JsonObject)w);
						performanceentries.put(p.name(), p);
					}
					break;
				
				case "labels":
					for (JsonValue w : (JsonArray)v)
						labels.add(((JsonString)w).getString());
					break;
				
				case "plugins":
					for (JsonValue w : (JsonArray)v)
					{
						Plugin p = Plugin.decodeEvent((JsonObject)w);
						plugins.put(p.name, p);
					}
					break;
				
					// Unknown
				default:
					throw new RuntimeException("Invalid key in event: " + k);
			}
		}
		
		return new StandardPushEvent(token, installmethod, duration, stat,
			processid, timestamp, timestampend, aws, disk, environment, errors,
			coldstart, custommetrics, performanceentries, labels, plugins);
	}
	
	/**
	 * Decodes a custom metric.
	 *
	 * @param __data The data to decode.
	 * @return The decoded custom metric.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/07/16
	 */
	public static CustomMetric decodeCustomMetric(JsonObject __data)
		throws NullPointerException
	{
		if (__data == null)
			throw new NullPointerException();
		
		String name = ((JsonString)__data.get("name")).getString();
		
		JsonString sval = (JsonString)__data.get("s");
		JsonNumber nval = (JsonNumber)__data.get("n");
		
		if (sval != null)
			return new CustomMetric(name, sval.getString());
		return new CustomMetric(name, nval.longValue());
	}
	
	/**
	 * Decodes a performance entry.
	 *
	 * @param __data The input data.
	 * @return The decoded entry.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/07/16
	 */
	public static PerformanceEntry decodePerformanceEntry(JsonObject __data)
		throws NullPointerException
	{
		if (__data == null)
			throw new NullPointerException();
		
		return new PerformanceEntry(
			((JsonString)(__data.get("name"))).getString(),
			((JsonString)(__data.get("entryType"))).getString(),
			(long)(((JsonNumber)(__data.get("startTime"))).doubleValue() * 1_000_000.0D),
			(long)(((JsonNumber)(__data.get("duration"))).doubleValue() * 1_000_000.0D),
			((JsonNumber)(__data.get("timestamp"))).longValue());
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
		 * @param __runtime The current runtime.
		 * @param __version The current version.
		 * @param __loadtime The current load time.
		 * @since 2018/07/13
		 */
		public Agent(String __runtime, String __version, long __loadtime)
		{
			this.runtime = __runtime;
			this.version = __version;
			this.loadtime = __loadtime;
		}
		
		/**
		 * Decodes the agent information.
		 *
		 * @param __data The agent data.
		 * @return The decoded information.
		 * @throws NullPointerException On null arguments.
		 * @since 2018/07/16
		 */
		public static Agent decodeEvent(JsonObject __data)
			throws NullPointerException
		{
			if (__data == null)
				throw new NullPointerException();
			
			String runtime = null;
			String version = null;
			long loadtime = Long.MIN_VALUE;
			
			for (Map.Entry<String, JsonValue> e : __data.entrySet())
			{
				JsonValue v = e.getValue();
				
				String k;
				switch ((k = e.getKey()))
				{
					case "runtime":
						runtime = ((JsonString)v).getString();
						break;
					
					case "version":
						version = ((JsonString)v).getString();
						break;
					
					case "load_time":
						loadtime = ((JsonNumber)v).longValue();
						break;
						
						// Unknown
					default:
						throw new RuntimeException(
							"Invalid key in Agent event: " + k);
				}
			}
			
			return new Agent(runtime, version, loadtime);
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
		 * @param __functionname The function name.
		 * @param __functionversion The function version.
		 * @param __requestid The request ID.
		 * @param __invokedfunctionarn The invoked function ARN.
		 * @param __loggroupname The log group name.
		 * @param __logstreamname The log stream name.
		 * @param __memorylimitmib Memory limit in MiB.
		 * @param __remainingtime Remaining time.
		 * @param __traceid Trace ID.
		 * @since 2018/07/13 
		 */
		public AWS(String __functionname, String __functionversion,
			String __requestid, String __invokedfunctionarn,
			String __loggroupname, String __logstreamname,
			long __memorylimitmib, long __remainingtime, String __traceid)
		{
			this.functionname = __functionname;
			this.functionversion = __functionversion;
			this.requestid = __requestid;
			this.invokedfunctionarn = __invokedfunctionarn;
			this.loggroupname = __loggroupname;
			this.logstreamname = __logstreamname;
			this.memorylimitmib = __memorylimitmib;
			this.remainingtime = __remainingtime;
			this.traceid = __traceid;
		}
		
		/**
		 * Decodes an AWS JSON event.
		 *
		 * @param __data The object to decode.
		 * @return The decoded object.
		 * @throws NullPointerException On null arguments.
		 * @since 2018/07/16
		 */
		public static AWS decodeEvent(JsonObject __data)
			throws NullPointerException
		{
			if (__data == null)
				throw new NullPointerException();
			
			String functionname = null;
			String functionversion = null;
			String requestid = null;
			String invokedfunctionarn = null;
			String loggroupname = null;
			String logstreamname = null;
			long memorylimitmib = Long.MIN_VALUE;
			long remainingtime = Long.MIN_VALUE;
			String traceid = null;
			
			for (Map.Entry<String, JsonValue> e : __data.entrySet())
			{
				JsonValue v = e.getValue();
				
				String k;
				switch ((k = e.getKey()))
				{
					case "functionName":
						functionname = ((JsonString)v).getString();
						break;
					
					case "functionVersion":
						functionversion = ((JsonString)v).getString();
						break;
					
					case "awsRequestId":
						requestid = ((JsonString)v).getString();
						break;
					
					case "invokedFunctionArn":
						invokedfunctionarn = ((JsonString)v).getString();
						break;
					
					case "logGroupName":
						loggroupname = ((JsonString)v).getString();
						break;
					
					case "logStreamName":
						logstreamname = ((JsonString)v).getString();
						break;
					
					case "memoryLimitInMB":
						memorylimitmib = ((JsonNumber)v).longValue();
						break;
					
					case "getRemainingTimeInMillis":
						remainingtime = ((JsonNumber)v).longValue();
						break;
					
					case "traceId":
						traceid = ((JsonString)v).getString();
						break;
					
						// Unknown
					default:
						throw new RuntimeException(
							"Invalid key in AWS event: " + k);
				}
			}
			
			return new AWS(functionname, functionversion, requestid,
				invokedfunctionarn, loggroupname, logstreamname,
				memorylimitmib, remainingtime, traceid);
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
		 * @param __times CPU times.
		 * @since 2018/07/13
		 */
		public CPU(Times __times)
		{
			this.times = __times;
		}
		
		/**
		 * Decodes event information.
		 *
		 * @param __data The event data.
		 * @return The decoded information.
		 * @throws NullPointerException On null arguments.
		 * @since 2018/07/16
		 */
		public static CPU decodeEvent(JsonObject __data)
			throws NullPointerException
		{
			if (__data == null)
				throw new NullPointerException();
			
			Times times = null;
			
			for (Map.Entry<String, JsonValue> e : __data.entrySet())
			{
				JsonValue v = e.getValue();
				
				String k;
				switch ((k = e.getKey()))
				{
					case "times":
						times = Times.decodeEvent((JsonObject)v);
						break;
					
						// Unknown
					default:
						throw new RuntimeException(
							"Invalid key in CPU event: " + k);
				}
			}
			
			return new CPU(times);
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
		 * @param __totalmib Total MiB.
		 * @param __usedmib Used MiB.
		 * @param __usedpercentage Used percentage.
		 * @since 2018/07/13
		 */
		public Disk(long __totalmib, long __usedmib, double __usedpercentage)
		{
			this.totalmib = __totalmib;
			this.usedmib = __usedmib;
			this.usedpercentage = __usedpercentage;
		}
		
		/**
		 * Decodes a disk event.
		 *
		 * @param __data Data.
		 * @throws NullPointerException On null arguments.
		 * @since 2108/07/16
		 */
		public static Disk decodeEvent(JsonObject __data)
			throws NullPointerException
		{
			if (__data == null)
				throw new NullPointerException();
			
			long totalmib = -1;
			long usedmib = -1;
			double usedpercentage = Double.NaN;
			
			for (Map.Entry<String, JsonValue> e : __data.entrySet())
			{
				JsonValue v = e.getValue();
				
				String k;
				switch ((k = e.getKey()))
				{
					case "totalMiB":
						totalmib = ((JsonNumber)v).longValue();
						break;
					
					case "usedMiB":
						usedmib = ((JsonNumber)v).longValue();
						break;
					
					case "usedPercentage":
						usedpercentage = ((JsonNumber)v).doubleValue();
						break;
					
						// Unknown
					default:
						throw new RuntimeException(
							"Invalid key in Disk event: " + k);
				}
			}
			
			return new Disk(totalmib, usedmib, usedpercentage);
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
		 * @param __agent Agent information.
		 * @param __runtime Runtime information.
		 * @param __host Host information.
		 * @param __os Operating system information.
		 * @since 2018/07/13
		 */
		public Environment(Agent __agent, Runtime __runtime, Host __host,
			OS __os)
		{
			this.agent = __agent;
			this.runtime = __runtime;
			this.host = __host;
			this.os = __os;
		}
		
		/**
		 * Decodes the environment information.
		 *
		 * @param __data The input data.
		 * @return The decoded environment information.
		 * @throws NullPointerException On null arguments.
		 * @since 2018/07/16
		 */
		public static Environment decodeEvent(JsonObject __data)
			throws NullPointerException
		{
			if (__data == null)
				throw new NullPointerException();
			
			Agent agent = null;
			Runtime runtime = null;
			Host host = null;
			OS os = null;
			
			for (Map.Entry<String, JsonValue> e : __data.entrySet())
			{
				JsonValue v = e.getValue();
				
				String k;
				switch ((k = e.getKey()))
				{
					case "agent":
						agent = Agent.decodeEvent((JsonObject)v);
						break;
					
					case "runtime":
						runtime = Runtime.decodeEvent((JsonObject)v);
						break;
					
					case "host":
						host = Host.decodeEvent((JsonObject)v);
						break;
					
					case "os":
						os = OS.decodeEvent((JsonObject)v);
						break;
					
						// Unknown
					default:
						throw new RuntimeException(
							"Invalid key in Environment event: " + k);
				}
			}
			
			return new Environment(agent, runtime, host, os);
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
		 * @param __stack The stack.
		 * @param __name The error name.
		 * @param __message The message.
		 * @since 2018/07/13
		 */
		public Errors(String __stack, String __name, String __message)
		{
			this.stack = __stack;
			this.name = __name;
			this.message = __message;
		}
		
		/**
		 * Decodes the host information.
		 *
		 * @param __data The input event data.
		 * @return The decoded data.
		 * @throws NullPointerException On null arguments.
		 * @since 2018/07/17
		 */
		public static Errors decodeEvent(JsonObject __data)
			throws NullPointerException
		{
			if (__data == null)
				throw new NullPointerException();
			
			String stack = null;
			String name = null;
			String message = null;
			
			
			for (Map.Entry<String, JsonValue> e : __data.entrySet())
			{
				JsonValue v = e.getValue();
				
				String k;
				switch ((k = e.getKey()))
				{
					case "stack":
						stack = ((JsonString)v).getString();
						break;
					
					case "name":
						name = ((JsonString)v).getString();
						break;
						
					case "message":
						message = ((JsonString)v).getString();
						break;
						
						// Unknown
					default:
						throw new RuntimeException(
							"Invalid key in Error event: " + k);
				}
			}
			
			return new Errors(stack, name, message);
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
		 * @param __bootid The boot identifier.
		 * @since 2018/07/13
		 */
		public Host(String __bootid)
		{
			this.bootid = __bootid;
		}
		
		/**
		 * Decodes the host information.
		 *
		 * @param __data The input data.
		 * @return The host information.
		 * @throws NullPointerException On null arguments.
		 * @since 2018/07/16
		 */
		public static Host decodeEvent(JsonObject __data)
			throws NullPointerException
		{
			if (__data == null)
				throw new NullPointerException();
			
			String bootid = null;
			
			for (Map.Entry<String, JsonValue> e : __data.entrySet())
			{
				JsonValue v = e.getValue();
				
				String k;
				switch ((k = e.getKey()))
				{
					case "boot_id":
						bootid = ((JsonString)v).getString();
						break;
						
						// Unknown
					default:
						throw new RuntimeException(
							"Invalid key in Host event: " + k);
				}
			}
			
			return new Host(bootid);
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
		 * @param __pids Process ID maps.
		 * @since 2018/07/13
		 */
		public Linux(Map<String, Pid> __pids)
		{
			this.pids = Collections.<String, Pid>unmodifiableMap(
				(__pids == null ? new LinkedHashMap<String, Pid>() :
				new LinkedHashMap<>(__pids)));
		}
		
		/**
		 * Decodes the specified event.
		 *
		 * @param __data The data to decode.
		 * @return The decoded data.
		 * @throws NullPointerException On null arguments.
		 * @since 2018/07/16
		 */
		public static Linux decodeEvent(JsonObject __data)
			throws NullPointerException
		{
			if (__data == null)
				throw new NullPointerException();
			
			Map<String, Pid> pids = new LinkedHashMap<>();
			
			for (Map.Entry<String, JsonValue> e : __data.entrySet())
			{
				JsonValue v = e.getValue();
				
				String k;
				switch ((k = e.getKey()))
				{
					case "pid":
						for (Map.Entry<String, JsonValue> f :
							((JsonObject)v).entrySet())
							pids.put(f.getKey(),
								Pid.decodeEvent((JsonObject)f.getValue()));
						break;
						
						// Unknown
					default:
						throw new RuntimeException(
							"Invalid key in Host event: " + k);
				}
			}
			
			return new Linux(pids);
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
		
		/** Total memory. */
		public final long totalmem;
		
		/** Free memory. */
		public final long freemem;
		
		/** Used memory. */
		public final long usedmem;
		
		/** CPU information. */
		public final List<CPU> cpus;
		
		/** Linux information. */
		public final Linux linux;
		
		/**
		 * Initializes the OS information.
		 *
		 * @param __hostname Hostname.
		 * @param __memory Memory information.
		 * @param __cpus CPU information.
		 * @param __linux Linux information.
		 * @since 2018/07/13
		 */
		public OS(String __hostname, long __totalmem, long __freemem,
			long __usedmem, List<CPU> __cpus, Linux __linux)
		{
			this.hostname = __hostname;
			this.totalmem = __totalmem;
			this.freemem = __freemem;
			this.usedmem = __usedmem;
			this.linux = __linux;
			
			this.cpus = Collections.<CPU>unmodifiableList((__cpus == null ?
				new ArrayList<CPU>() : new ArrayList<>(__cpus)));
		}
		
		/**
		 * Decodes the OS information.
		 *
		 * @param __data The input data.
		 * @return The OS information.
		 * @throws NullPointerException On null arguments.
		 * @since 2018/07/16
		 */
		public static OS decodeEvent(JsonObject __data)
			throws NullPointerException
		{
			if (__data == null)
				throw new NullPointerException();
			
			String hostname = null;
			long totalmem = Long.MIN_VALUE;
			long freemem = Long.MIN_VALUE;
			long usedmem = Long.MIN_VALUE;
			List<CPU> cpus = new ArrayList<>();
			Linux linux = null;
			
			for (Map.Entry<String, JsonValue> e : __data.entrySet())
			{
				JsonValue v = e.getValue();
				
				String k;
				switch ((k = e.getKey()))
				{
					case "hostname":
						hostname = ((JsonString)v).getString();
						break;
					
					case "totalmem":
						totalmem = ((JsonNumber)v).longValue();
						break;
					
					case "freemem":
						freemem = ((JsonNumber)v).longValue();
						break;
					
					case "usedmem":
						usedmem = ((JsonNumber)v).longValue();
						break;
					
					case "cpus":
						for (JsonValue w : ((JsonArray)v))
							cpus.add(CPU.decodeEvent((JsonObject)w));
						break;
					
					case "linux":
						linux = Linux.decodeEvent((JsonObject)v);
						break;
						
						// Unknown
					default:
						throw new RuntimeException(
							"Invalid key in OS event: " + k);
				}
			}
			
			return new OS(hostname, totalmem, freemem, usedmem, cpus, linux);
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
		 * @param __stat End stat.
		 * @param __statstart Starting stat.
		 * @param __status Current process status
		 * @since 2018/07/13
		 */
		public Pid(Stat __stat, Stat __statstart, Status __status)
		{
			this.stat = __stat;
			this.statstart = __statstart;
			this.status = __status;
		}
		
		/**
		 * Decodes the event data.
		 *
		 * @param __data Event data.
		 * @return Decoded event data.
		 * @throws NullPointerException On null arguments.
		 * @since 2018/07/16
		 */
		public static Pid decodeEvent(JsonObject __data)
			throws NullPointerException
		{
			if (__data == null)
				throw new NullPointerException();
			
			Stat stat = null;
			Stat statstart = null;
			Status status = null;
			
			for (Map.Entry<String, JsonValue> e : __data.entrySet())
			{
				JsonValue v = e.getValue();
				
				String k;
				switch ((k = e.getKey()))
				{
					case "stat":
						stat = Stat.decodeEvent((JsonObject)v);
						break;
					
					case "stat_start":
						statstart = Stat.decodeEvent((JsonObject)v);
						break;
					
					case "status":
						status = Status.decodeEvent((JsonObject)v);
						break;
					
						// Unknown
					default:
						throw new RuntimeException(
							"Invalid key in Pid event: " + k);
				}
			}
			
			return new Pid(stat, statstart, status);
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
		 * @param __name Name.
		 * @param __version Version.
		 * @param __vendor Vendor.
		 * @param __vmvendor VM Vendor.
		 * @param __vmversion VM Version.
		 * @since 2018/07/13
		 */
		public Runtime(String __name, String __version, String __vendor,
			String __vmvendor, String __vmversion)
		{
			this.name = __name;
			this.version = __version;
			this.vendor = __vendor;
			this.vmvendor = __vmvendor;
			this.vmversion = __vmversion;
		}
		
		/**
		 * Decodes the runtime event information.
		 *
		 * @param __data The input event.
		 * @return The decoded runtime information.
		 * @throws NullPointerException On null arguments.
		 * @since 2018/07/16
		 */
		public static Runtime decodeEvent(JsonObject __data)
			throws NullPointerException
		{
			if (__data == null)
				throw new NullPointerException();
			
			String name = null;
			String version = null;
			String vendor = null;
			String vmvendor = null;
			String vmversion = null;
			
			for (Map.Entry<String, JsonValue> e : __data.entrySet())
			{
				JsonValue v = e.getValue();
				
				String k;
				switch ((k = e.getKey()))
				{
					case "name":
						name = ((JsonString)v).getString();
						break;
					
					case "version":
						version = ((JsonString)v).getString();
						break;
					
					case "vendor":
						vendor = ((JsonString)v).getString();
						break;
					
					case "vmVendor":
						vmvendor = ((JsonString)v).getString();
						break;
					
					case "vmVersion":
						vmversion = ((JsonString)v).getString();
						break;
					
						// Unknown
					default:
						throw new RuntimeException(
							"Invalid key in Runtime event: " + k);
				}
			}
			
			return new Runtime(name, version, vendor, vmvendor, vmversion);
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
		
		/** Uploads. */
		public final List<String> uploads;
		
		/**
		 * Initializes the plugin information.
		 *
		 * @param __name Name.
		 * @param __version Version.
		 * @param __homepage Homepage.
		 * @param __enabled Is this enabled?
		 * @param __uploads Uploads.
		 * @since 2018/07/13
		 */
		public Plugin(String __name, String __version, String __homepage,
			boolean __enabled, List<String> __uploads)
		{
			this.name = __name;
			this.version = __version;
			this.homepage = __homepage;
			this.enabled = __enabled;
			this.uploads = Collections.<String>unmodifiableList(
				(__uploads == null ? new ArrayList<String>() :
				new ArrayList<>(__uploads)));
		}
		
		/**
		 * Decodes the plugin information.
		 *
		 * @param __data The input event data.
		 * @return The decoded data.
		 * @throws NullPointerException On null arguments.
		 * @since 2018/07/17
		 */
		public static Plugin decodeEvent(JsonObject __data)
			throws NullPointerException
		{
			if (__data == null)
				throw new NullPointerException();
			
			String name = null;
			String version = null;
			String homepage = null;
			boolean enabled = false;
			List<String> uploads = new ArrayList<>();
			
			for (Map.Entry<String, JsonValue> e : __data.entrySet())
			{
				JsonValue v = e.getValue();
				
				String k;
				switch ((k = e.getKey()))
				{
					case "name":
						name = ((JsonString)v).getString();
						break;
					
					case "version":
						version = ((JsonString)v).getString();
						break;
					
					case "homepage":
						homepage = ((JsonString)v).getString();
						break;
					
					case "enabled":
						enabled = JsonValue.TRUE.equals(v);
						break;
					
					case "uploads":
						for (JsonValue w : (JsonArray)v)
							uploads.add(((JsonString)w).getString());
						break;
					
						// Unknown
					default:
						throw new RuntimeException(
							"Invalid key in Plugin event: " + k);
				}
			}
			
			return new Plugin(name, version, homepage, enabled, uploads);
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
		 * @param __utime User time.
		 * @param __stime System time.
		 * @param __cutime User time with children.
		 * @param __cstime System time with children.
		 * @since 2018/07/13
		 */
		public Stat(long __utime, long __stime, long __cutime, long __cstime)
		{
			this.utime = __utime;
			this.stime = __stime;
			this.cutime = __cutime;
			this.cstime = __cstime;
		}
		
		/**
		 * Decodes the status information.
		 *
		 * @param __data The input event data.
		 * @return The decoded data.
		 * @throws NullPointerException On null arguments.
		 * @since 2018/07/17
		 */
		public static Stat decodeEvent(JsonObject __data)
			throws NullPointerException
		{
			if (__data == null)
				throw new NullPointerException();
			
			long utime = Long.MIN_VALUE;
			long stime = Long.MIN_VALUE;
			long cutime = Long.MIN_VALUE;
			long cstime = Long.MIN_VALUE;
			
			for (Map.Entry<String, JsonValue> e : __data.entrySet())
			{
				JsonValue v = e.getValue();
				
				String k;
				switch ((k = e.getKey()))
				{
					case "utime":
						utime = ((JsonNumber)v).longValue();
						break;
					
					case "stime":
						stime = ((JsonNumber)v).longValue();
						break;
					
					case "cutime":
						cutime = ((JsonNumber)v).longValue();
						break;
					
					case "cstime":
						cstime = ((JsonNumber)v).longValue();
						break;
					
						// Unknown
					default:
						throw new RuntimeException(
							"Invalid key in Stat event: " + k);
				}
			}
			
			return new Stat(utime, stime, cutime, cstime);
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
		 * @param __vmrss Resident virtual memory.
		 * @param __threads The number of threads
		 * @param __fdsize The file descriptor count.
		 * @since 2018/07/13
		 */
		public Status(long __vmrss, int __threads, int __fdsize)
		{
			this.vmrss = __vmrss;
			this.threads = __threads;
			this.fdsize = __fdsize;
		}
		
		/**
		 * Decodes the status information.
		 *
		 * @param __data The input event data.
		 * @return The decoded data.
		 * @throws NullPointerException On null arguments.
		 * @since 2018/07/17
		 */
		public static Status decodeEvent(JsonObject __data)
			throws NullPointerException
		{
			if (__data == null)
				throw new NullPointerException();
			
			long vmrss = Long.MIN_VALUE;
			int threads = Integer.MIN_VALUE;
			int fdsize = Integer.MIN_VALUE;
			
			for (Map.Entry<String, JsonValue> e : __data.entrySet())
			{
				JsonValue v = e.getValue();
				
				String k;
				switch ((k = e.getKey()))
				{
					case "VmRSS":
						vmrss = ((JsonNumber)v).longValue();
						break;
					
					case "Threads":
						threads = ((JsonNumber)v).intValue();
						break;
					
					case "FDSize":
						fdsize = ((JsonNumber)v).intValue();
						break;
					
						// Unknown
					default:
						throw new RuntimeException(
							"Invalid key in Status event: " + k);
				}
			}
			
			return new Status(vmrss, threads, fdsize);
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
		 * @param __idle Idle time.
		 * @param __irq IRQ time.
		 * @param __sys System time.
		 * @param __user User time.
		 * @param __nice Nice time.
		 * @since 2108/07/13
		 */
		public Times(long __idle, long __irq, long __sys, long __user,
			long __nice)
		{
			this.idle = __idle;
			this.irq = __irq;
			this.sys = __sys;
			this.user = __user;
			this.nice = __nice;
		}
		
		/**
		 * Decodes the event information.
		 *
		 * @param __data The input event data.
		 * @return The decoded data.
		 * @throws NullPointerException On null arguments.
		 * @since 2108/07/16
		 */
		public static Times decodeEvent(JsonObject __data)
			throws NullPointerException
		{
			if (__data == null)
				throw new NullPointerException();
			
			long idle = Long.MIN_VALUE;
			long irq = Long.MIN_VALUE;
			long sys = Long.MIN_VALUE;
			long user = Long.MIN_VALUE;
			long nice = Long.MIN_VALUE;
			
			for (Map.Entry<String, JsonValue> e : __data.entrySet())
			{
				JsonValue v = e.getValue();
				
				String k;
				switch ((k = e.getKey()))
				{
					case "idle":
						idle = ((JsonNumber)v).longValue();
						break;
						
					case "irq":
						irq = ((JsonNumber)v).longValue();
						break;
						
					case "sys":
						sys = ((JsonNumber)v).longValue();
						break;
						
					case "user":
						user = ((JsonNumber)v).longValue();
						break;
						
					case "nice":
						nice = ((JsonNumber)v).longValue();
						break;
					
						// Unknown
					default:
						throw new RuntimeException(
							"Invalid key in Runtime event: " + k);
				}
			}
			
			return new Times(idle, irq, sys, user, nice);
		}
	}
}

