package com.iopipe;

import java.lang.ref.Reference;

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
	/** Reference to the execution. */
	protected final Reference<IOpipeExecution> ref;
	
	/**
	 * Initializes the forwarder for measurements.
	 *
	 * @param __ref The refernece to the owning execution.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/08/31
	 */
	IOpipeMeasurement(Reference<IOpipeExecution> __ref)
		throws NullPointerException
	{
		if (__ref == null)
			throw new NullPointerException();
		
		this.ref = __ref;
	}
	
	/**
	 * Adds a single custom metric to the report.
	 *
	 * @param __cm The custom metric to add.
	 * @throws NullPointerException On null arguments.
	 * @deprecated Use {@link IOpipeExecution.customMetric(CustomMetric)}
	 * instead.
	 * @since 2018/01/20
	 */
	@Deprecated
	public void addCustomMetric(CustomMetric __cm)
		throws NullPointerException
	{
		if (__cm == null)
			throw new NullPointerException();
		
		IOpipeExecution exec = this.ref.get();
		if (exec != null)
			exec.customMetric(__cm);
	}
	
	/**
	 * Adds multiple custom metrics in a single bulk operation.
	 *
	 * Parameters which are {@code null} are ignored.
	 *
	 * @param __cms The custom metrics to add.
	 * @deprecated Use {@link IOpipeExecution.customMetrics(CustomMetric[])}
	 * instead.
	 * @since 2018/04/24
	 */
	@Deprecated
	public void addCustomMetrics(CustomMetric... __cms)
	{
		// Do nothing
		if (__cms == null)
			return;
		
		IOpipeExecution exec = this.ref.get();
		if (exec != null)
			exec.customMetrics(__cms);
	}
	
	/**
	 * Adds a single label which will be passed in the report.
	 *
	 * Labels are limited to the length specified in
	 * {@link IOpipeConstants#NAME_CODEPOINT_LIMIT}.
	 *
	 * @param __s The label to add.
	 * @throws NullPointerException On null arguments.
	 * @deprecated Use {@link IOpipeExecution.label(String)}
	 * instead.
	 * @since 2018/04/11
	 */
	@Deprecated
	public void addLabel(String __s)
		throws NullPointerException
	{
		if (__s == null)
			throw new NullPointerException();
		
		IOpipeExecution exec = this.ref.get();
		if (exec != null)
			exec.label(__s);
	}
	
	/**
	 * Adds a single performance entry to the report.
	 *
	 * @param __e The entry to add to the report.
	 * @throws NullPointerException On null arguments.
	 * @deprecated Use {@link IOpipeExecution.addPerformanceEntry(PerformanceEntry)}
	 * instead.
	 * @since 2018/01/19
	 */
	@Deprecated
	public void addPerformanceEntry(PerformanceEntry __e)
		throws NullPointerException
	{
		if (__e == null)
			throw new NullPointerException();
		
		IOpipeExecution exec = this.ref.get();
		if (exec != null)
			exec.addPerformanceEntry(__e);
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
	 * @deprecated Use {@link IOpipeExecution.customMetric(String, String)}
	 * instead.
	 * @since 2018/01/20
	 */
	@Deprecated
	public final void customMetric(String __name, String __sv)
		throws NullPointerException
	{
		if (__name == null || __sv == null)
			throw new NullPointerException();
		
		IOpipeExecution exec = this.ref.get();
		if (exec != null)
			exec.customMetric(__name, __sv);
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
	 * @deprecated Use {@link IOpipeExecution.customMetric(String, long)}
	 * instead.
	 * @since 2018/01/20
	 */
	@Deprecated
	public final void customMetric(String __name, long __lv)
		throws NullPointerException
	{
		if (__name == null)
			throw new NullPointerException();
		
		IOpipeExecution exec = this.ref.get();
		if (exec != null)
			exec.customMetric(__name, __lv);
	}
	
	/**
	 * Returns a copy of the custom metrics which were measured.
	 *
	 * @return The custom metrics which were measured.
	 * @deprecated Use {@link IOpipeExecution.getCustomMetrics()}
	 * instead.
	 * @since 2018/03/15
	 */
	@Deprecated
	public CustomMetric[] getCustomMetrics()
	{
		IOpipeExecution exec = this.ref.get();
		if (exec != null)
			return exec.getCustomMetrics();
		return new CustomMetric[0];
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
	 * @deprecated Use {@link IOpipeExecution.useLabels()}
	 * instead.
	 * @since 2018/04/11
	 */
	@Deprecated
	public String[] getLabels()
	{
		IOpipeExecution exec = this.ref.get();
		if (exec != null)
			return exec.getLabels();
		return new String[0];
	}
	
	/**
	 * Returns a copy of the performance entries which were measured.
	 *
	 * @return The performance entries which were measured.
	 * @deprecated Use {@link IOpipeExecution.getPerformanceEntries()}
	 * instead.
	 * @since 2018/03/15
	 */
	@Deprecated
	public PerformanceEntry[] getPerformanceEntries()
	{
		IOpipeExecution exec = this.ref.get();
		if (exec != null)
			return exec.getPerformanceEntries();
		return new PerformanceEntry[0];
	}

	/**
	 * Returns the thrown throwable.
	 *
	 * @return Always returns {@code null}.
	 * @deprecated This information is not known until execution has finished
	 * and as such will always return {@code null}.
	 * @since 2017/12/15
	 */
	@Deprecated
	public Throwable getThrown()
	{
		return null;
	}
	
	/**
	 * Is this a coldstarted execution?
	 *
	 * @return If this is a coldstarted execution.
	 * @deprecated Use {@link IOpipeExecution.isColdStarted()} instead.
	 * @since 2018/03/15
	 */
	@Deprecated
	public boolean isColdStarted()
	{
		IOpipeExecution exec = this.ref.get();
		if (exec != null)
			return exec.isColdStarted();
		return false;
	}
}
