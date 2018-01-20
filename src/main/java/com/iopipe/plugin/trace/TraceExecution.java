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
	 * Returns the measurement.
	 *
	 * @return The measurement.
	 * @since 2018/01/20
	 */
	public IOpipeMeasurement measurement()
	{
		return this.measurement;
	}
}

