package com.iopipe;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class is used to keep track of measurements during execution.
 *
 * @since 2017/12/15
 */
public final class IOpipeMeasurement
{
	/**
	 * Performance entries which have been added to the measurement, this
	 * field is locked since multiple threads may be adding entries.
	 */
	private final Set<PerformanceEntry> _perfentries =
		new TreeSet<>();
	
	/** Custom metrics that have been added, locked for thread safety. */
	private final Set<CustomMetric> _custmetrics =
		new TreeSet<>();
	
	/** The exception which may have been thrown. */
	private volatile Throwable _thrown;

	/** The duration of execution in nanoseconds. */
	private volatile long _duration =
		Long.MIN_VALUE;

	/** Is this execution one which is a cold start? */
	private volatile boolean _coldstart;

	/**
	 * Initializes the measurement holder.
	 *
	 * @since 2018/03/15
	 */
	IOpipeMeasurement()
	{
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
			custmetrics.add(__cm);
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
			perfentries.add(__e);
		}
	}
	
	/**
	 * Adds the specified custom metric with a string value.
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
	 * @since 2017/12/15
	 */
	public long getDuration()
	{
		return this._duration;
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
		return this._thrown;
	}
	
	/**
	 * Is this a coldstarted execution?
	 *
	 * @return If this is a coldstarted execution.
	 * @since 2018/03/15
	 */
	public boolean isColdStarted()
	{
		return this._coldstart;
	}

	/**
	 * Sets whether or not the execution was a cold start. A cold start
	 * indicates that the JVM was started fresh and a previous instance is not
	 * being reused.
	 *
	 * @param __cold If {@code true} then the execution follows a cold start.
	 * @since 2017/12/20
	 */
	void __setColdStart(boolean __cold)
	{
		this._coldstart = __cold;
	}

	/**
	 * Sets the duration of execution.
	 *
	 * @param __ns The execution duration in nanoseconds.
	 * @since 2017/12/15
	 */
	void __setDuration(long __ns)
	{
		this._duration = __ns;
	}

	/**
	 * Sets the throwable generated during execution.
	 *
	 * @param __t The generated throwable.
	 * @since 2017/12/15
	 */
	void __setThrown(Throwable __t)
	{
		this._thrown = __t;
	}
}
