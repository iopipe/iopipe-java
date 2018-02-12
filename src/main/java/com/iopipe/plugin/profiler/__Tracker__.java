package com.iopipe.plugin.profiler;

import java.util.Arrays;

/**
 * This class keeps track of executions which have occured.
 *
 * @since 2018/02/12
 */
final class __Tracker__
{
	/**
	 * Parses and keeps track of the specified stack trace.
	 *
	 * @param __rt The number of nanoseconds into the execution when this
	 * trace occurred.
	 * @param __trace The stack trace to handle.
	 * @since 2018/02/12
	 */
	final void __parseStackTrace(long __rt, StackTraceElement... __trace)
	{
		if (__trace == null)
			__trace = new StackTraceElement[0];
		
		System.err.println(Arrays.<StackTraceElement>asList(__trace));
	}
}

