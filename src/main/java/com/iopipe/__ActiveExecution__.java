package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import com.iopipe.http.RemoteBody;
import com.iopipe.http.RemoteException;
import com.iopipe.http.RemoteRequest;
import com.iopipe.plugin.IOpipePlugin;
import com.iopipe.plugin.IOpipePluginExecution;
import com.iopipe.plugin.NoSuchPluginException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;
import org.pmw.tinylog.Logger;

/**
 * This is an execution which has an actual effect.
 *
 * @since 2018/08/27
 */
final class __ActiveExecution__
	extends IOpipeExecution
{
	/** Is this a Linux system? */
	private static final boolean _IS_LINUX =
		"linux".compareToIgnoreCase(
			System.getProperty("os.name", "unknown")) == 0;
	
	/** The service which invoked the method. */
	protected final IOpipeService service;
	
	/** The configuration. */
	protected final IOpipeConfiguration config;
	
	/** The context. */
	protected final Context context;
	
	/** The starting time in milliseconds. */
	protected final long starttimemillis;
	
	/** The starting time in monotonic nanoseconds. */
	protected final long starttimemononanos;
	
	/**
	 * The input object to the executing method, may be {@code null} if it
	 * was passed or not used.
	 */
	protected final Object input;
	
	/** Plugins which currently have an active exection state. */
	private final Map<Class<? extends IOpipePluginExecution>,
		IOpipePluginExecution> _active =
		new HashMap<>();
	
	/** The exception which may have been thrown. */
	private final AtomicReference<Throwable> _thrown =
		new AtomicReference<>();
	
	/**
	 * Performance entries which have been added to the measurement, this
	 * field is locked since multiple threads may be adding entries.
	 */
	private final Set<PerformanceEntry> _perfentries =
		new LinkedHashSet<>();
	
	/** Custom metrics that have been added, locked for thread safety. */
	private final Set<CustomMetric> _custmetrics =
		new LinkedHashSet<>();
	
	/** Labels which have been added, locked for threading. */
	private final Set<String> _labels =
		new LinkedHashSet<>();
	
	/**
	 * Initializes the execution information.
	 *
	 * @param __sv The service which initialized this.
	 * @param __conf The configuration for this service.
	 * @param __context The context for the execution.
	 * @param __tg The thread group which the execution runs under.
	 * @param __st The start time in the system clock milliseconds.
	 * @param __input The object which was passed to the method being
	 * executed.
	 * @param __sns The start time in monotonic nanoseconds.
	 * @param __cold Has this been coldstarted?
	 * @throws NullPointerException On null arguments except for
	 * {@code __passed}.
	 * @since 2018/01/19
	 */
	__ActiveExecution__(IOpipeService __sv, IOpipeConfiguration __conf,
		Context __context, long __st,
		Object __input, long __sns, boolean __cold)
		throws NullPointerException
	{
		super(__cold);
		
		if (__sv == null || __conf == null || __context == null)
			throw new NullPointerException();
		
		this.service = __sv;
		this.config = __conf;
		this.context = __context;
		this.starttimemillis = __st;
		this.input = __input;
		this.starttimemononanos = __sns;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/19
	 */
	@Override
	public final void addPerformanceEntry(PerformanceEntry __e)
		throws NullPointerException
	{
		if (__e == null)
			throw new NullPointerException();
		
		// Multiple threads could be adding entries
		Set<PerformanceEntry> perfentries = this._perfentries;
		synchronized (perfentries)
		{
			// Performance entry was defined, so just say that the plugin was
			// used for tracing data
			this.label("@iopipe/plugin-trace");
			
			perfentries.add(__e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/19
	 */
	@Override
	public final IOpipeConfiguration config()
	{
		return this.config;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/19
	 */
	@Override
	public final Context context()
	{
		return this.context;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/20
	 */
	@Override
	public final void customMetric(CustomMetric __cm)
		throws NullPointerException
	{
		if (__cm == null)
			throw new NullPointerException();
		
		// Multiple threads can add metrics at one time
		Set<CustomMetric> custmetrics = this._custmetrics;
		synchronized (custmetrics)
		{
			if (!__cm.name().startsWith("@iopipe/"))
				this.label("@iopipe/metrics");
			
			custmetrics.add(__cm);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/30
	 */
	@Override
	public final void customMetric(String __name, String __sv)
		throws NullPointerException
	{
		if (__name == null || __sv == null)
			throw new NullPointerException();
		
		this.customMetric(new CustomMetric(__name, __sv));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/30
	 */
	@Override
	public final void customMetric(String __name, long __lv)
		throws NullPointerException
	{
		if (__name == null)
			throw new NullPointerException();
		
		this.customMetric(new CustomMetric(__name, __lv));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/03/15
	 */
	@Override
	public final CustomMetric[] getCustomMetrics()
	{
		Collection<CustomMetric> custmetrics = this._custmetrics;
		synchronized (custmetrics)
		{
			return custmetrics.<CustomMetric>toArray(
				new CustomMetric[custmetrics.size()]);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/04/11
	 */
	@Override
	public final String[] getLabels()
	{
		Set<String> labels = this._labels;
		synchronized (labels)
		{
			return labels.<String>toArray(new String[labels.size()]);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/03/15
	 */
	@Override
	public final PerformanceEntry[] getPerformanceEntries()
	{
		Collection<PerformanceEntry> perfentries = this._perfentries;
		synchronized (perfentries)
		{
			return perfentries.<PerformanceEntry>toArray(
				new PerformanceEntry[perfentries.size()]);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/04/16
	 */
	@Override
	public final Object input()
	{
		return this.input;
	}

	/**
	 * Adds a single label which will be passed in the report.
	 *
	 * Labels are limited to the length specified in
	 * {@link IOpipeConstants#NAME_CODEPOINT_LIMIT}.
	 *
	 * @param __s The label to add.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/04/11
	*/
	public final void label(String __s)
		throws NullPointerException
	{
		if (__s == null)
			throw new NullPointerException();

		// Add it
		Set<String> labels = this._labels;
		synchronized (labels)
		{
			labels.add(__s);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/20
	 */
	@Override
	public final <C extends IOpipePluginExecution> C plugin(Class<C> __cl)
		throws ClassCastException, NoSuchPluginException, NullPointerException
	{
		if (__cl == null)
			throw new NullPointerException();
		
		// Lock due to multiple threads
		Map<Class<? extends IOpipePluginExecution>, IOpipePluginExecution>
			active = this._active;
		synchronized (active)
		{
			// Need to create the plugin if it does not exist
			IOpipePluginExecution rv = active.get(__cl);
			if (rv == null)
			{
				// Was pre-cached to not exist
				if (active.containsKey(__cl))
					throw new NoSuchPluginException(String.format(
						"No plugin exists for %s, it is disabled, or it " +
						"failed to be initialized.", __cl));
				
				// If the plugin fails to initialize due to some exception
				// instead of just trying again treat it as if it were
				// disabled.
				__Plugins__.__Info__ info;
				try
				{
					info = this.service._plugins.__get(__cl);
				}
				catch (Exception e)
				{
					info = null;
					
					// Just log it
					Logger.error(e, "Failed to initialize plugin.");
				}
				
				// It is possible that the plugin does not exist or is
				// disabled, it could be requested multiple times so cache it
				// Or initialization failed
				if (info == null || !info.isEnabled())
				{
					active.put(__cl, null);
					throw new NoSuchPluginException(String.format(
						"No plugin exists for %s or it is disabled.", __cl));
				}
				
				// Initialize the plugin's execution state
				rv = info.plugin().execute(this);
				if (rv == null)
				{
					active.put(__cl, null);
					throw new NoSuchPluginException(String.format(
						"Could create execution instance for plugin.", __cl));
				}
				active.put(__cl, rv);
			}
			
			return  __cl.cast(rv);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/19
	 */
	@Override
	public final IOpipeService service()
	{
		return this.service;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/09/24
	 */
	@Override
	public final IOpipeSigner signer(String __ext)
	{
		Context context = this.context;
		return new IOpipeSigner(
			__ext,
			context.getInvokedFunctionArn(),
			context.getAwsRequestId(),
			this.startTimestamp(),
			this.config());
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/02/16
	 */
	@Override
	public final long startTimestamp()
	{
		return this.starttimemillis;
	}

	/**
	 * Builds the request which is sent to the remote service.
	 *
	 * @return The remote request to send to the service.
	 * @throws RemoteException If the request could not be built.
	 * @since 2017/12/17
	 */
	final RemoteRequest __buildRequest()
		throws RemoteException
	{
		Context aws = this.context;
		IOpipeConfiguration config = this.config;

		// Snapshot system information
		SystemMeasurement sysinfo = SystemMeasurement.measure();
		
		// The current timestamp
		long nowtimestamp = System.currentTimeMillis(),
			starttimemononanos = this.starttimemononanos;
		
		StringWriter out = new StringWriter();
		try (JsonGenerator gen = Json.createGenerator(out))
		{
			gen.writeStartObject();

			gen.write("client_id", config.getProjectToken());
			// UNUSED: "projectId": "s"
			gen.write("installMethod",
				Objects.toString(config.getInstallMethod(), "unknown"));

			SystemMeasurement.Stat stat = sysinfo.stat;
			
			gen.write("processId", __Shared__._PROCESS_ID.toString());
			gen.write("timestamp", this.starttimemillis);
			gen.write("timestampEnd", nowtimestamp);
			
			// AWS Context information
			gen.writeStartObject("aws");

			gen.write("functionName", aws.getFunctionName());
			gen.write("functionVersion", aws.getFunctionVersion());
			gen.write("awsRequestId", aws.getAwsRequestId());
			gen.write("invokedFunctionArn", aws.getInvokedFunctionArn());
			gen.write("logGroupName", aws.getLogGroupName());
			gen.write("logStreamName", aws.getLogStreamName());
			gen.write("memoryLimitInMB", aws.getMemoryLimitInMB());
			gen.write("getRemainingTimeInMillis",
				aws.getRemainingTimeInMillis());
			gen.write("traceId", Objects.toString(
				System.getenv("_X_AMZN_TRACE_ID"), "unknown"));

			gen.writeEnd();

			// Memory Usage -- UNUSED
			/*gen.writeStartObject("memory");

			gen.write("rssMiB", );
			gen.write("totalMiB", );
			gen.write("rssTotalPercentage", );

			gen.writeEnd();*/
			
			// Disk usage
			
			SystemMeasurement.Disk tempdir = sysinfo.tempdir;
			gen.writeStartObject("disk");
			gen.write("totalMiB", tempdir.totalmib);
			gen.write("usedMiB", tempdir.usedmib);
			gen.write("usedPercentage", tempdir.usedpercent * 100.0);
			gen.writeEnd();

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

			gen.write("coldstart", this.isColdStarted());
			
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
	 * Checks if the given string is within the name limit before it is
	 * reported.
	 *
	 * @param __s The name to check.
	 * @return If the name is short enough to be included.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/04/11
	 */
	private static final boolean __isNameInLimit(String __s)
		throws NullPointerException
	{
		if (__s == null)
			throw new NullPointerException();
		
		return __s.codePointCount(0, __s.length()) <
			IOpipeConstants.NAME_CODEPOINT_LIMIT;
	}
	
	/**
	 * Checks if the given string is within the value limit before it is
	 * reported.
	 *
	 * @param __s The name to check.
	 * @return If the name is short enough to be included.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/05/03
	 */
	private static final boolean __isValueInLimit(String __s)
		throws NullPointerException
	{
		if (__s == null)
			throw new NullPointerException();
		
		return __s.codePointCount(0, __s.length()) <
			IOpipeConstants.VALUE_CODEPOINT_LIMIT;
	}
	
	/**
	 * Sets the throwable generated during execution.
	 *
	 * @param __t The generated throwable, this may only be set once.
	 * @since 2017/12/15
	 */
	void __setThrown(Throwable __t)
	{
		this._thrown.compareAndSet(null, __t);
	}
}

