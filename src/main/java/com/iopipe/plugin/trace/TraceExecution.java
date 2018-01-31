package com.iopipe.plugin.trace;

import com.iopipe.IOpipeExecution;
import com.iopipe.IOpipeMeasurement;
import com.iopipe.plugin.IOpipePluginExecution;

/**
 * This is the trace plugin which is used to track specific marks and measure
 * performance.
 *
 * @since 2018/01/20
 */
public class TraceExecution
	implements IOpipePluginExecution
{
	/** The measurement to record to. */
	protected final IOpipeMeasurement measurement;
	
	/**
	 * Initializes the trace execution instance.
	 *
	 * @param __m The measurement to record to.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/20
	 */
	public TraceExecution(IOpipeMeasurement __m)
		throws NullPointerException
	{
		if (__m == null)
			throw new NullPointerException();
		
		this.measurement = __m;
	}
	
	/**
	 * Creates a new mark which represents a single point in time and adds it
	 * to the report.
	 *
	 * @param __name The name of the mark to create.
	 * @return The created mark.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/23
	 */
	public TraceMark mark(String __name)
		throws NullPointerException
	{
		if (__name == null)
			throw new NullPointerException();
		
		TraceMark rv = new TraceMark(__name);
		this.measurement.addPerformanceEntry(rv);
		return rv;
	}
	
	/**
	 * Creates a new instance of a class which is used to measure how long
	 * a block of code has executed for. The returned object is
	 * {@link AutoCloseable} and it is highly recommended to use
	 * try-with-resources when utilizing it. When the method
	 * {@link AutoCloseable#close()} is called the measurement will be
	 * recorded.
	 *
	 * @param __name The name of the measurement.
	 * @return NullPointerException On null arguments.
	 * @since 2018/01/23
	 */
	public TraceMeasurement measure(String __name)
		throws NullPointerException
	{
		if (__name == null)
			throw new NullPointerException();
		
		TraceMeasurement rv = new TraceMeasurement(__name);
		this.measurement.addPerformanceEntry(rv);
		return rv;
	}
	
	/**
	 * Creates a measurement between the two marks.
	 *
	 * @param __name The name of the measurement.
	 * @param __a The first mark.
	 * @param __b The second mark.
	 * @return A performance entry which defines a measurement between
	 * the two marks.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/23
	 */
	public TraceMeasurementMark measure(String __name, TraceMark __a,
		TraceMark __b)
		throws NullPointerException
	{
		if (__a == null || __b == null)
			throw new NullPointerException();
		
		TraceMeasurementMark rv = new TraceMeasurementMark(__name, __a, __b);
		this.measurement.addPerformanceEntry(rv);
		return rv;
	}
}

