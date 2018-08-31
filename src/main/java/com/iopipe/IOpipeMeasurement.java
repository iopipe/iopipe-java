package com.iopipe;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class is used to keep track of measurements during execution.
 *
 * @deprecated The methods here have been deprecated, measurements are now
 * fully managed by the execution state. While this class exists all calls are
 * forwarded to {@link IOpipeExecution}.
 * @see IOpipeExecution
 * @since 2017/12/15
 */
@Deprecated
public final class IOpipeMeasurement
{
	/** Is this execution one which is a cold start? */
	private final boolean coldstart;
	
	/**
	 * Performance entries which have been added to the measurement, this
	 * field is locked since multiple threads may be adding entries.
	 */
	private final Set<PerformanceEntry> _perfentries =
		new TreeSet<>();
	
	/** Custom metrics that have been added, locked for thread safety. */
	private final Set<CustomMetric> _custmetrics =
		new TreeSet<>();
	
	/** Labels which have been added, locked for threading. */
	private final Set<String> _labels =
		new LinkedHashSet<>();
	
	/** The exception which may have been thrown. */
	private final AtomicReference<Throwable> _thrown =
		new AtomicReference<>();

	/**
	 * Initializes the measurement holder.
	 *
	 * @param __cs Has this been coldstarted?
	 * @since 2018/03/15
	 */
	IOpipeMeasurement(boolean __cs)
	{
		this.coldstart = __cs;
	}
	
	/**
	 * Adds a single custom metric to the report.
	 *
	 * @param __cm The custom metric to add.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/20
	 */
	public void addCustomMetric(CustomMetric __cm)
		throws NullPointerException
	{
		if (__cm == null)
			throw new NullPointerException();
		
		// Multiple threads can add metrics at one time
		Set<CustomMetric> custmetrics = this._custmetrics;
		synchronized (custmetrics)
		{
			if (!__cm.name().startsWith("@iopipe/"))
				this.addLabel("@iopipe/metrics");
			
			custmetrics.add(__cm);
		}
	}
	
	/**
	 * Adds multiple custom metrics in a single bulk operation.
	 *
	 * Parameters which are {@code null} are ignored.
	 *
	 * @param __cms The custom metrics to add.
	 * @since 2018/04/24
	 */
	public void addCustomMetrics(CustomMetric... __cms)
	{
		// Do nothing
		if (__cms == null)
			return;
		
		// Bulk add all the custom metrics under a single lock
		Set<CustomMetric> custmetrics = this._custmetrics;
		synchronized (custmetrics)
		{
			for (CustomMetric cm : __cms)
				if (cm != null)
				{
					if (!cm.name().startsWith("@iopipe/"))
						this.addLabel("@iopipe/metrics");
					
					custmetrics.add(cm);
				}
		}
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
	public void addLabel(String __s)
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
	 * Adds a single performance entry to the report.
	 *
	 * @param __e The entry to add to the report.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/19
	 */
	public void addPerformanceEntry(PerformanceEntry __e)
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
			this.addLabel("@iopipe/plugin-trace");
			
			perfentries.add(__e);
		}
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
	 * @since 2018/01/20
	 */
	public final void customMetric(String __name, String __sv)
		throws NullPointerException
	{
		if (__name == null || __sv == null)
			throw new NullPointerException();
		
		this.addCustomMetric(new CustomMetric(__name, __sv));
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
	 * @since 2018/01/20
	 */
	public final void customMetric(String __name, long __lv)
		throws NullPointerException
	{
		if (__name == null)
			throw new NullPointerException();
		
		this.addCustomMetric(new CustomMetric(__name, __lv));
	}
	
	/**
	 * Returns a copy of the custom metrics which were measured.
	 *
	 * @return The custom metrics which were measured.
	 * @since 2018/03/15
	 */
	public CustomMetric[] getCustomMetrics()
	{
		Collection<CustomMetric> custmetrics = this._custmetrics;
		synchronized (custmetrics)
		{
			return custmetrics.<CustomMetric>toArray(
				new CustomMetric[custmetrics.size()]);
		}
	}

	/**
	 * Returns the execution duration.
	 *
	 * @return The execution duration, if this is negative then it is not
	 * valid.
	 * @deprecated This value is only set when the invocation has finished and
	 * the report is to be generated, so it always will return
	 * {@link Long#MIN_VALUE}.
	 * @since 2017/12/15
	 */
	@Deprecated
	public long getDuration()
	{
		return Long.MIN_VALUE;
	}
	
	/**
	 * Returns all of the labels which have been declared during the
	 * execution.
	 *
	 * @return The labels which have been declared during execution.
	 * @since 2018/04/11
	 */
	public String[] getLabels()
	{
		Set<String> labels = this._labels;
		synchronized (labels)
		{
			return labels.<String>toArray(new String[labels.size()]);
		}
	}
	
	/**
	 * Returns a copy of the performance entries which were measured.
	 *
	 * @return The performance entries which were measured.
	 * @since 2018/03/15
	 */
	public PerformanceEntry[] getPerformanceEntries()
	{
		Collection<PerformanceEntry> perfentries = this._perfentries;
		synchronized (perfentries)
		{
			return perfentries.<PerformanceEntry>toArray(
				new PerformanceEntry[perfentries.size()]);
		}
	}

	/**
	 * Returns the thrown throwable.
	 *
	 * @return The throwable which was thrown or {@code null} if none was
	 * thrown.
	 * @since 2017/12/15
	 */
	public Throwable getThrown()
	{
		return this._thrown.get();
	}
	
	/**
	 * Is this a coldstarted execution?
	 *
	 * @return If this is a coldstarted execution.
	 * @since 2018/03/15
	 */
	public boolean isColdStarted()
	{
		return this.coldstart;
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
