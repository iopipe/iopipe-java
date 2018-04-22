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
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This class provides access to information and functionality which is
 * specific to a single execution of a method.
 *
 * Each execution will have a unique instance of this object and as such will
 * be initialized when it is first used.
 *
 * @since 2018/01/19
 */
public final class IOpipeExecution
{
	/** Logging. */
	private static final Logger _LOGGER =
		LogManager.getLogger(IOpipeExecution.class);
	
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
	
	/** The measurement. */
	protected final IOpipeMeasurement measurement;
	
	/** The thread group this execution runs under. */
	protected final ThreadGroup threadgroup;
	
	/** The starting time in milliseconds. */
	protected final long starttimemillis;
	
	/**
	 * The input object to the executing method, may be {@code null} if it
	 * was passed or not used.
	 */
	protected final Object input;
	
	/** Plugins which currently have an active exection state. */
	private final Map<Class<? extends IOpipePluginExecution>,
		IOpipePluginExecution> _active =
		new HashMap<>();
	
	/**
	 * Initializes the execution information.
	 *
	 * @param __sv The service which initialized this.
	 * @param __conf The configuration for this service.
	 * @param __context The context for the execution.
	 * @param __m Measurement which is used to provide access to tracing.
	 * @param __tg The thread group which the execution runs under.
	 * @param __st The start time in the system clock milliseconds.
	 * @param __input The object which was passed to the method being
	 * executed.
	 * @throws NullPointerException On null arguments except for
	 * {@code __passed}.
	 * @since 2018/01/19
	 */
	IOpipeExecution(IOpipeService __sv, IOpipeConfiguration __conf,
		Context __context, IOpipeMeasurement __m, ThreadGroup __tg, long __st,
		Object __input)
		throws NullPointerException
	{
		if (__sv == null || __conf == null || __context == null ||
			__m == null || __tg == null)
			throw new NullPointerException();
		
		this.service = __sv;
		this.config = __conf;
		this.context = __context;
		this.measurement = __m;
		this.threadgroup = __tg;
		this.starttimemillis = __st;
		this.input = __input;
	}
	
	/**
	 * Returns the configuration used to initialize the service.
	 *
	 * @return The service configuration.
	 * @since 2018/01/19
	 */
	public final IOpipeConfiguration config()
	{
		return this.config;
	}
	
	/**
	 * Returns the AWS context.
	 *
	 * @return The AWS context.
	 * @since 2018/01/19
	 */
	public final Context context()
	{
		return this.context;
	}
	
	/**
	 * Adds the specified custom metric with a string value.
	 *
	 * Custom metric names are limited to the length specified in
	 * {@link IOpipeConstants#NAME_CODEPOINT_LIMIT}.
	 *
	 * @param __name The metric name.
	 * @param __sv The string value.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/30
	 */
	public final void customMetric(String __name, String __sv)
		throws NullPointerException
	{
		if (__name == null || __sv == null)
			throw new NullPointerException();
		
		this.measurement.customMetric(__name, __sv);
	}
	
	/**
	 * Adds the specified custom metric with a long value.
	 *
	 * Custom metric names are limited to the length specified in
	 * {@link IOpipeConstants#NAME_CODEPOINT_LIMIT}.
	 *
	 * @param __name The metric name.
	 * @param __lv The long value.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/30
	 */
	public final void customMetric(String __name, long __lv)
		throws NullPointerException
	{
		if (__name == null)
			throw new NullPointerException();
		
		this.measurement.customMetric(__name, __lv);
	}
	
	/**
	 * Returns the object which was used as input for the method being
	 * executed.
	 *
	 * @return The extra object which was passed to the run method.
	 * @since 2018/04/16
	 */
	public final Object input()
	{
		return this.input;
	}
	
	/**
	 * Returns the object which was used as input for the method being
	 * executed.
	 *
	 * @param <T> The type of object to return.
	 * @param __cl The type of object to return.
	 * @return The extra object which was passed to the run method.
	 * @throws ClassCastException If it is not of the passed class type.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/04/16
	 */
	public final <T> T input(Class<T> __cl)
		throws ClassCastException, NullPointerException
	{
		if (__cl == null)
			throw new NullPointerException();
		
		return __cl.cast(this.input);
	}
	
	/*
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
		
		this.measurement.addLabel(__s);
	}
	
	/**
	 * Returns the measurement recorder.
	 *
	 * @return The measurement recorder.
	 * @since 2018/01/19
	 */
	public final IOpipeMeasurement measurement()
	{
		return this.measurement;
	}
	
	/**
	 * This returns an instance of a plugin based on the class type of its
	 * interface.
	 *
	 * @param <C> The class type of the execution state.
	 * @param __cl The class object of the execution state.
	 * @return The instance of the plugin's execution state.
	 * @throws ClassCastException If the class type is not valid.
	 * @throws NoSuchPluginException If the plugin does not exist.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/20
	 */
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
						"No plugin exists for %s or it is disabled.", __cl));
				
				// It is possible that the plugin does not exist or is
				// disabled, it could be requested multiple times so cache it
				__Plugins__.__Info__ info =  this.service._plugins.__get(__cl);
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
	 * This executes the specified method if the plugin exists, if it does
	 * not exist then it will not be executed.
	 *
	 * @param <C> The class type of the execution state.
	 * @param __cl The class object of the execution state.
	 * @param __func The function to excute if the plugin exists and is valid.
	 * @throws ClassCastException If the class type is not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/23
	 */
	public final <C extends IOpipePluginExecution> void plugin(Class<C> __cl,
		Consumer<C> __func)
		throws ClassCastException, NullPointerException
	{
		if (__cl == null || __func == null)
			throw new NullPointerException();
		
		try
		{
			__func.accept(this.plugin(__cl));
		}
		catch (NoSuchPluginException e)
		{
		}
	}
	
	/**
	 * This searches for the specified plugin if the plugin exists it will
	 * return an instance of {@link AutoCloseable} which may be used with
	 * try-with-resources.
	 *
	 * @param <C> The class type of the execution state.
	 * @param <R> The type of object to return.
	 * @param __cl The class object of the execution state.
	 * @param __func The function to obtain the {@link AutoCloseable} for use
	 * with try-with-resources for.
	 * @return The {@code A} object or {@code null} if the plugin is not valid
	 * or no value was returned.
	 * @throws ClassCastException If the class type is not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/23
	 */
	public final <C extends IOpipePluginExecution, R extends AutoCloseable>
		R plugin(Class<C> __cl, Function<C, R> __func)
		throws ClassCastException, NullPointerException
	{
		if (__cl == null || __func == null)
			throw new NullPointerException();
		
		try
		{
			return __func.apply(this.plugin(__cl));
		}
		catch (NoSuchPluginException e)
		{
			return null;
		}
	}
	
	/**
	 * This searches for the specified plugin if the plugin exists it will
	 * return an instance of {@link AutoCloseable} which may be used with
	 * try-with-resources. An optional secondary argument may be passed to
	 * simplify some operations that take an extra parameter.
	 *
	 * @param <C> The class type of the execution state.
	 * @param <R> The type of object to return.
	 * @param <V> The type of extra value to pass to the function.
	 * @param __cl The class object of the execution state.
	 * @param __func The function to obtain the {@link AutoCloseable} for use
	 * with try-with-resources for.
	 * @param __v The extra value to be passed to the function.
	 * @return The {@code A} object or {@code null} if the plugin is not valid
	 * or no value was returned.
	 * @throws ClassCastException If the class type is not valid.
	 * @throws NullPointerException On null arguments except for {@code __v}.
	 * @since 2018/01/23
	 */
	public final <C extends IOpipePluginExecution, R extends AutoCloseable,
		V> R plugin(Class<C> __cl, BiFunction<C, V, R> __func, V __v)
		throws ClassCastException, NullPointerException
	{
		if (__cl == null || __func == null)
			throw new NullPointerException();
		
		try
		{
			return __func.apply(this.plugin(__cl), __v);
		}
		catch (NoSuchPluginException e)
		{
			return null;
		}
	}
	
	/**
	 * This returns an instance of a plugin based on the class type of its
	 * interface, if the plugin does not exist then {@code null} is returned.
	 *
	 * @param <C> The class type of the execution state.
	 * @param __cl The class object of the execution state.
	 * @return The instance of the plugin's execution state or {@code null}
	 * if no such plugin exists.
	 * @throws ClassCastException If the class type is not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/23
	 */
	public final <C extends IOpipePluginExecution> C optionalPlugin(
		Class<C> __cl)
		throws ClassCastException, NullPointerException
	{
		try
		{
			return this.plugin(__cl);
		}
		catch (NoSuchPluginException e)
		{
			return null;
		}
	}
	
	/**
	 * Returns the service which ran this execution.
	 *
	 * @return The service which ran this execution.
	 * @since 2018/01/19
	 */
	public final IOpipeService service()
	{
		return this.service;
	}
	
	/**
	 * Returns the starting time of the execution on the wall clock.
	 *
	 * @return The starting time in milliseconds.
	 * @since 2018/02/16
	 */
	public final long startTimestamp()
	{
		return this.starttimemillis;
	}
	
	/**
	 * Returns the thread group which this execution is running under.
	 *
	 * @return The thread group of this execution.
	 * @since 2018/02/09
	 */
	public final ThreadGroup threadGroup()
	{
		return this.threadgroup;
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
		IOpipeMeasurement measurement = this.measurement;

		// Snapshot system information
		SystemMeasurement sysinfo = new SystemMeasurement();
		
		// The current timestamp
		long nowtimestamp = System.currentTimeMillis();
		
		StringWriter out = new StringWriter();
		try (JsonGenerator gen = Json.createGenerator(out))
		{
			gen.writeStartObject();

			gen.write("client_id", config.getProjectToken());
			// UNUSED: "projectId": "s"
			gen.write("installMethod",
				Objects.toString(config.getInstallMethod(), "unknown"));

			long duration = measurement.getDuration();
			if (duration >= 0)
				gen.write("duration", duration);

			gen.write("processId", sysinfo.pid);
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

			long totalmem, freemem;
			gen.write("hostname", SystemMeasurement.HOSTNAME);
			gen.write("totalmem", (totalmem = sysinfo.memorytotalkib));
			gen.write("freemem", (freemem = sysinfo.memoryfreekib));
			gen.write("usedmem", totalmem - freemem);

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

				SystemMeasurement.Times times = new SystemMeasurement.Times();
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

				gen.write("VmRSS", sysinfo.vmrsskib);
				gen.write("Threads", sysinfo.threads);
				gen.write("FDSize", sysinfo.fdsize);

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

			Throwable thrown = measurement.getThrown();
			if (thrown != null)
			{
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

			gen.write("coldstart", measurement.isColdStarted());
			
			// Add custom metrics, which multiple threads could be adding at
			// once
			gen.writeStartArray("custom_metrics");
			CustomMetric[] custmetrics = measurement.getCustomMetrics();
			for (int i = 0, n = custmetrics.length; i < n; i++)
			{
				CustomMetric metric = custmetrics[i];
				
				String xname = metric.name();
				if (IOpipeExecution.__isNameInLimit(xname))
				{
					gen.writeStartObject();
					
					gen.write("name", xname);
					
					if (metric.hasString())
						gen.write("s", metric.stringValue());
					if (metric.hasLong())
						gen.write("n", metric.longValue());
					
					gen.writeEnd();
				}
				
				// Emit warning
				else
					_LOGGER.warn("Metric exceeds the {} codepoint limit and " +
						"will not be reported: {}",
						IOpipeConstants.NAME_CODEPOINT_LIMIT, xname);
			}
			
			// End of metrics
			gen.writeEnd();
			
			// Copy the performance entries which have been measured
			gen.writeStartArray("performanceEntries");
			PerformanceEntry[] perfs = measurement.getPerformanceEntries();
			for (int i = 0, n = perfs.length; i < n; i++)
			{
				PerformanceEntry perf = perfs[i];
				
				gen.writeStartObject();
				
				gen.write("name",
					Objects.toString(perf.name(), "unknown"));
				gen.write("startTime",
					(double)perf.startNanoTime() / 1_000_000.0D);
				gen.write("duration",
					(double)perf.durationNanoTime() / 1_000_000.0D);
				gen.write("entryType",
					Objects.toString(perf.type(), "unknown"));
				gen.write("timestamp", nowtimestamp);
				
				gen.writeEnd();
			}
			
			// End of entries
			gen.writeEnd();
			
			// Are there any labels to be added?
			gen.writeStartArray("labels");
			String[] labels = measurement.getLabels();
			for (int i = 0, n = labels.length; i < n; i++)
			{
				String label = labels[i];
				if (IOpipeExecution.__isNameInLimit(label))
					gen.write(label);
				
				// Emit warning
				else
					_LOGGER.warn("Label exceeds the {} codepoint limit and " +
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
}

