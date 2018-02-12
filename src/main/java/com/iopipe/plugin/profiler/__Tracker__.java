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
	 * @param __abs The absolute time since the start of execution in
	 * nanoseconds.
	 * @param __rel The relative time since the last trace.
	 * @param __trace The stack trace to handle.
	 * @since 2018/02/12
	 */
	final void __parseStackTrace(long __abs, int __rel,
		StackTraceElement... __trace)
	{
		if (__trace == null)
			__trace = new StackTraceElement[0];
		
		System.err.printf("%8d %8d - %s%n", __abs, __rel,
			Arrays.<StackTraceElement>asList(__trace));
	}
}

