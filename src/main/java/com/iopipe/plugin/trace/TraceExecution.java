package com.iopipe.plugin.trace;

import com.iopipe.IOpipeExecution;
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
	/**
	 * Initializes the trace execution instance.
	 *
	 * @since 2018/01/20
	 */
	TraceExecution()
	{
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
	 * @deprecated This method is not recommended to be used, use
	 * {@link TraceUtils#measure(String)} instead.
	 * @since 2018/01/23
	 */
	@Deprecated
	public TraceMeasurement measure(String __name)
		throws NullPointerException
	{
		if (__name == null)
			throw new NullPointerException();
		
		return TraceUtils.measure(__name);
	}
}

