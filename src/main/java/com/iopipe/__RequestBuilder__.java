package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * This class manages building the requests to be sent to the IOpipe service.
 *
 * @since 2018/11/20
 */
final class __RequestBuilder__
{
	/** The trace ID. */
	private static final String _TRACE_ID =
		Objects.toString(System.getenv("_X_AMZN_TRACE_ID"), "unknown");
	
	/** Monotonic start time. */
	protected final long monostart;
	
	/** The event data. */
	private final ByteArrayOutputStream _baos =
		new ByteArrayOutputStream(1048576);
	
	/** Printer to the event stream. */
	private final PrintStream _ps =
		new PrintStream(_baos, 
	
	/**
	 * Initializes the print stream.
	 *
	 * @since 2018/11/20
	 */
	{
		PrintStream ps;
		try
		{
			ps = new PrintStream(this._baos, true, "utf-8");
		}
		catch (UnsupportedEncodingException e)
		{
			ps = new PrintStream(this._baos, true);
		}
		this._ps = ps;
	}
	
	/**
	 * Initializes the request.
	 *
	 * @param __cntx The context.
	 * @param __conf The configuration.
	 * @param __startns Start nanoseconds.
	 * @param __cs Is this a cold start?
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/20
	 */
	__RequestBuilder__(Context __cntx, IOpipeConfiguration __conf,
		long __startns, boolean __cs)
		throws NullPointerException
	{
		if (__cntx == null || __conf == null)
			throw new NullPointerException("NARG");
		
		this.monostart = __startns;
		
		// The output event is going to be written to early with all of the
		// needed information.
		PrintStream ps = this._ps;
		
		// Open
		ps.print('{');
		
		// Client ID
		ps.print("\"client_id\":\"");
		ps.print(__conf.token);
		ps.print("\",");
		
		// Install method
		ps.print("\"installMethod\":\"");
		ps.print(Objects.toString(__conf.installmethod, "unknown"));
		ps.print("\",");
		
		// Process ID
		ps.print("\"processId\":\"");
		ps.print(__Shared__._PROCESS_ID.toString());
		ps.print("\",");
		
		// Timestamp
		ps.print("\"timestamp\":");
		ps.print(System.currentTimeMillis());
		ps.print(",");
		
		// Coldstart?
		ps.print("\"coldstart\":");
		ps.print(__cs);
		ps.print(",");
		
		// AWS Object
		ps.print("\"aws\":{");
		{
			// Function name
			ps.print("\"functionName\":\"");
			ps.print(__cntx.getFunctionName());
			ps.print("\",");
			
			// Function version
			ps.print("\"functionVersion\":\"");
			ps.print(__cntx.getFunctionVersion());
			ps.print("\",");
			
			// Request ID
			ps.print("\"awsRequestId\":\"");
			ps.print(__cntx.getAwsRequestId());
			ps.print("\",");
			
			// Invoked function ARN
			ps.print("\"invokedFunctionArn\":\"");
			ps.print(__cntx.getInvokedFunctionArn());
			ps.print("\",");
			
			// Log group name
			ps.print("\"logGroupName\":\"");
			ps.print(__cntx.getLogGroupName());
			ps.print("\",");
			
			// Log stream name
			ps.print("\"logStreamName\":\"");
			ps.print(__cntx.getLogStreamName());
			ps.print("\",");
			
			// Memory limit in MiB
			ps.print("\"memoryLimitInMB\":");
			ps.print(__cntx.getMemoryLimitInMB());
			ps.print(",");
			
			// Remaining time
			ps.print("\"getRemainingTimeInMillis\":");
			ps.print(__cntx.getRemainingTimeInMillis());
			ps.print(",");
			
			// Trace ID
			ps.print("\"traceId\":\"");
			ps.print(_TRACE_ID);
			ps.print("\",");
		}
		ps.print("},");
		
		throw new Error("TODO");
		
			// Environment start
			gen.writeStartObject("environment");

			// Agent
			gen.writeStartObject("agent");
			gen.write("runtime", "java");
			gen.write("version", IOpipeConstants.AGENT_VERSION);
			gen.write("load_time", IOpipeConstants.LOAD_TIME);
			gen.writeEnd();

			// Runtime information
			gen.writeStartObject("runtime");
			gen.write("name", "java");
			gen.write("version", System.getProperty("java.version", ""));
			gen.write("vendor", System.getProperty("java.vendor", ""));
			gen.write("vmVendor", System.getProperty("java.vm.vendor", ""));
			gen.write("vmVersion", System.getProperty("java.vm.version", ""));
			gen.writeEnd();

			// Unique operating system boot identifier
			gen.writeStartObject("host");
			gen.write("boot_id", SystemMeasurement.BOOTID);
			gen.writeEnd();

			// Operating System Start
			gen.writeStartObject("os");
			gen.write("hostname", SystemMeasurement.HOSTNAME);

			SystemMeasurement.Memory memory = sysinfo.memory;
			gen.write("totalmem", memory.totalbytes);
			gen.write("freemem", memory.freebytes);
			gen.write("usedmem", memory.usedbytes);

			// Start CPUs
			gen.writeStartArray("cpus");

			List<SystemMeasurement.Cpu> cpus = sysinfo.cpus;
			for (int i = 0, n = cpus.size(); i < n; i++)
			{
				SystemMeasurement.Cpu cpu = cpus.get(i);

				gen.writeStartObject();
				gen.writeStartObject("times");

				gen.write("idle", cpu.idle);
				gen.write("irq", cpu.irq);
				gen.write("sys", cpu.sys);
				gen.write("user", cpu.user);
				gen.write("nice", cpu.nice);

				gen.writeEnd();
				gen.writeEnd();
			}

			// End CPUs
			gen.writeEnd();

			// Linux information
			if (_IS_LINUX)
			{
				// Start Linux
				gen.writeStartObject("linux");

				// Start PID
				gen.writeStartObject("pid");

				// Start self
				gen.writeStartObject("self");

				gen.writeStartObject("stat");

				SystemMeasurement.Times times = sysinfo.times;
				gen.write("utime", times.utime);
				gen.write("stime", times.stime);
				gen.write("cutime", times.cutime);
				gen.write("cstime", times.cstime);

				gen.writeEnd();

				gen.writeStartObject("stat_start");

				times = IOpipeService._STAT_START;
				gen.write("utime", times.utime);
				gen.write("stime", times.stime);
				gen.write("cutime", times.cutime);
				gen.write("cstime", times.cstime);

				gen.writeEnd();

				gen.writeStartObject("status");

				gen.write("VmRSS", stat.vmrsskib);
				gen.write("Threads", stat.threads);
				gen.write("FDSize", stat.fdsize);

				gen.writeEnd();

      			// End self
      			gen.writeEnd();

				// End PID
				gen.writeEnd();

				// End Linux
				gen.writeEnd();
			}

			// Operating System end
			gen.writeEnd();

			// Environment end
			gen.writeEnd();

			Throwable thrown = this._thrown.get();
			if (thrown != null)
			{
				// If this was a wrapped IOException then instead of reporting
				// our wrapper instead report the wrapped exception
				if (thrown instanceof IOpipeWrappedException)
				{
					Throwable instead = thrown.getCause();
					if (instead != null)
						thrown = instead;
				}
				
				gen.writeStartObject("errors");

				// Write the stack as if it were normally output on the console
				StringWriter trace = new StringWriter();
				try (PrintWriter pw = new PrintWriter(trace))
				{
					thrown.printStackTrace(pw);

					pw.flush();
				}

				gen.write("stack", trace.toString());
				gen.write("name", thrown.getClass().getName());
				gen.write("message",
					Objects.toString(thrown.getMessage(), ""));
				// UNUSED: "stackHash": "s",
				// UNUSED: "count": "n"

				gen.writeEnd();
			}

			
			// Add custom metrics, which multiple threads could be adding at
			// once
			gen.writeStartArray("custom_metrics");
			CustomMetric[] custmetrics = this.getCustomMetrics();
			for (int i = 0, n = custmetrics.length; i < n; i++)
			{
				CustomMetric metric = custmetrics[i];
				
				// Check that the name is in the limit
				String xname = metric.name();
				if (!__ActiveExecution__.__isNameInLimit(xname))
				{
					Logger.warn("Metric name exceeds the {} codepoint " +
						"length limit and will not be reported: {}",
						IOpipeConstants.NAME_CODEPOINT_LIMIT, xname);
					continue;
				}
				
				// Check if the value is in range
				String svalue;
				if (metric.hasString())
				{
					svalue = metric.stringValue();
					
					if (!__ActiveExecution__.__isValueInLimit(svalue))
					{
						Logger.warn("Metric value exceeds the {} codepoint " +
							"length limit and will not be reported: {}",
							IOpipeConstants.VALUE_CODEPOINT_LIMIT, xname);
						continue;
					}	
				}
				else
					svalue = null;
				
				// Write data
				gen.writeStartObject();
				
				gen.write("name", xname);
				
				if (svalue != null)
					gen.write("s", svalue);
				if (metric.hasLong())
					gen.write("n", metric.longValue());
				
				gen.writeEnd();
			}
			
			// End of metrics
			gen.writeEnd();
			
			// Copy the performance entries which have been measured
			gen.writeStartArray("performanceEntries");
			PerformanceEntry[] perfs = this.getPerformanceEntries();
			for (int i = 0, n = perfs.length; i < n; i++)
			{
				PerformanceEntry perf = perfs[i];
				
				gen.writeStartObject();
				
				gen.write("name",
					Objects.toString(perf.name(), "unknown"));
				gen.write("startTime",
					(double)(perf.startNanoTime() - starttimemononanos) / 1_000_000.0D);
				gen.write("duration",
					(double)perf.durationNanoTime() / 1_000_000.0D);
				gen.write("entryType",
					Objects.toString(perf.type(), "unknown"));
				gen.write("timestamp", perf.startTimeMillis());
				
				gen.writeEnd();
			}
			
			// End of entries
			gen.writeEnd();
			
			// Are there any labels to be added?
			gen.writeStartArray("labels");
			String[] labels = this.getLabels();
			for (int i = 0, n = labels.length; i < n; i++)
			{
				String label = labels[i];
				if (__ActiveExecution__.__isNameInLimit(label))
					gen.write(label);
				
				// Emit warning
				else
					Logger.warn("Label exceeds the {} codepoint limit and " +
						"will not be reported: {}",
						IOpipeConstants.NAME_CODEPOINT_LIMIT, label);
			}
			
			// End of labels
			gen.writeEnd();
			
			// Record plugins which are being used
			Map<Class<? extends IOpipePluginExecution>, IOpipePluginExecution>
				active = this._active;
			__Plugins__.__Info__ plugins[] = this.service._plugins.__info();
			if (plugins.length > 0)
			{
				gen.writeStartArray("plugins");
				
				for (__Plugins__.__Info__ i : plugins)
				{
					gen.writeStartObject();
					
					gen.write("name", i.name());
					
					String ve = i.version();
					if (ve != null)
						gen.write("version", ve);
					
					String hp = i.homepage();
					if (hp != null)
						gen.write("homepage", hp);
					
					boolean pluginenabled;
					gen.write("enabled", (pluginenabled = i.isEnabled()));
					
					// The plugin may specify some extra data to be added to
					// properties in the plugin field, however only add that
					// information if it was specified accordingly and the
					// plugin was enabled
					if (pluginenabled)
					{
						// If a plugin was executed then it will have a state
						// to which to obtain information from
						IOpipePluginExecution iope;
						synchronized (active)
						{
							iope = active.get(i.executionClass());
						}
						
						// If it does define an extra object then record all
						// of the fields
						JsonObject extraobject = (iope == null ? null :
							iope.extraReport());
						if (extraobject != null)
							for (Map.Entry<String, JsonValue> e :
								extraobject.entrySet())
								gen.write(e.getKey(), e.getValue());
					}
					
					gen.writeEnd();
				}
				
				gen.writeEnd();
			}
			
			// Write duration last so that all the overhead is recoreded as
			// much as possible
			long duration = System.nanoTime() - starttimemononanos;
			if (duration >= 0)
				gen.write("duration", duration);
			
			// Finished
			gen.writeEnd();
			gen.flush();
		}
		catch (JsonException e)
		{
			throw new RemoteException("Could not build request", e);
		}

		return new RemoteRequest(RemoteBody.MIMETYPE_JSON, out.toString());
	}
	
	/**
	 * Finishes the requests.
	 *
	 * @return The resulting request.
	 * @since 2018/11/20
	 */
	final RemoteRequest __finish()
	{
		// The stream where the JSON is placed
		PrintStream ps = this._ps;
		
		// Measure system information, needed to start some things
		SystemMeasurement sysinfo = SystemMeasurement.measure();
		SystemMeasurement.Stat stat = sysinfo.stat;
		
		// Ending Timestamp
		ps.print("\"timestampEnd\":");
		ps.print(System.currentTimeMillis());
		ps.print(",");
		
		// Disk
		ps.print("\"disk\":{");
		{
			SystemMeasurement.Disk tempdir = sysinfo.tempdir;
			
			// Total MiB
			ps.print("\"
		}
		ps.print("},");
		
		throw new Error("TODO");
		
		SystemMeasurement.Disk tempdir = sysinfo.tempdir;
		gen.writeStartObject("disk");
		gen.write("totalMiB", tempdir.totalmib);
		gen.write("usedMiB", tempdir.usedmib);
		gen.write("usedPercentage", tempdir.usedpercent * 100.0);
		gen.writeEnd();
	}
}

