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
	 * Creates a new instance of a class which is used to measure how long
	 * a block of code has executed for. The returned object is
	 * {@link AutoCloseable} and it is highly recommended to use
	 * try-with-resources when utilizing it although it is not required.
	 * When the method
	 * {@link AutoCloseable#close()} is called the measurement will be
	 * recorded.
	 *
	 * @param __name The name of the measurement.
	 * @return The measurement which was added to the report.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/23
	 */
	public TraceMeasurement measure(String __name)
		throws NullPointerException
	{
		if (__name == null)
			throw new NullPointerException();
		
		return new TraceMeasurement(true, this.measurement, __name);
	}
}

