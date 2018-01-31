package com.iopipe.plugin.trace;

import com.iopipe.IOpipeExecution;

/**
 * This is a convenience class which contains static methods for creating marks
 * and measurements without needing to obtain the instance of the tracing
 * plugin execution state and performing executions on that.
 *
 * @since 2018/01/30
 */
public final class TraceUtils
{
	/**
	 * Not used.
	 *
	 * @since 2018/01/30
	 */
	private TraceUtils()
	{
	}
	
	/**
	 * Creates a new mark which represents a single point in time and adds it
	 * to the report if the plugin is enabled.
	 *
	 * @param __exec The single execution state.
	 * @param __name The name of the mark to create.
	 * @return The created mark or {@code null} if the plugin is not enabled.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/30
	 */
	public static TraceMark mark(IOpipeExecution __exec, String __name)
		throws NullPointerException
	{
		if (__exec == null || __name == null)
			throw new NullPointerException();
		
		TraceExecution x = __exec.<TraceExecution>optionalPlugin(
			TraceExecution.class);
		if (x != null)
			return x.mark(__name);
		return null;
	}
	
	/**
	 * Creates a new instance of a class which is used to measure how long
	 * a block of code has executed for. The returned object is
	 * {@link AutoCloseable} and it is highly recommended to use
	 * try-with-resources when utilizing it. When the method
	 * {@link AutoCloseable#close()} is called the measurement will be
	 * recorded. The measurement is returned and added to the report if
	 * the plugin is enabled.
	 *
	 * @param __exec The single execution state.
	 * @param __name The name of the measurement.
	 * @return The measurement which was added to the report,
	 * {@code null} is returned if the plugin is not enabled
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/30
	 */
	public static TraceMeasurement measure(IOpipeExecution __exec,
		String __name)
		throws NullPointerException
	{
		if (__exec == null || __name == null)
			throw new NullPointerException();
		
		TraceExecution x = __exec.<TraceExecution>optionalPlugin(
			TraceExecution.class);
		if (x != null)
			return x.measure(__name);
		return null;
	}
	
	/**
	 * Creates a measurement between the two marks, the measurement is
	 * returned and added to the report if the plugin is enabled..
	 *
	 * @param __exec The single execution state.
	 * @param __name The name of the measurement.
	 * @param __a The first mark.
	 * @param __b The second mark.
	 * @return A performance entry which defines a measurement between
	 * the two marks, {@code null} is returned if the plugin is not enabled.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/30
	 */
	public static TraceMeasurementMark measure(IOpipeExecution __exec,
		String __name, TraceMark __a, TraceMark __b)
		throws NullPointerException
	{
		// Do not check __a and __b for null because if a previous execution
		// returned null then no mark would have been returned.
		if (__exec == null || __name == null)
			throw new NullPointerException();
		
		TraceExecution x = __exec.<TraceExecution>optionalPlugin(
			TraceExecution.class);
		if (x != null)
			return x.measure(__name, __a, __b);
		return null;
	}
}

