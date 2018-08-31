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
		
		return new TraceMeasurement(__exec.<TraceExecution>optionalPlugin(
			TraceExecution.class) != null, __exec, __name);
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
	 * The execution is derived from the current execution.
	 *
	 * @param __name The name of the measurement.
	 * @return The measurement which was added to the report,
	 * {@code null} is returned if the plugin is not enabled
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/30
	 */
	public static TraceMeasurement measure(String __name)
		throws NullPointerException
	{
		if (__name == null)
			throw new NullPointerException();
		
		IOpipeExecution exec = IOpipeExecution.currentExecution();
		if (exec == null)
			return null;
		
		return TraceUtils.measure(exec, __name);
	}
}

