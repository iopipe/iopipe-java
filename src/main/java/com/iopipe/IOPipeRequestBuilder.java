package com.iopipe;

import javax.json.JsonObject;

/**
 * This class contains methods which are used to build requests which would
 * be sent to a server.
 *
 * @since 2017/12/15
 */
public final class IOPipeRequestBuilder
{
	/**
	 * Not used.
	 *
	 * @since 2017/12/15
	 */
	private IOPipeRequestBuilder()
	{
	}
	
	/**
	 * Builds a report with the specified metrics.
	 *
	 * @param __c The context to report for.
	 * @param __m The result of execution.
	 * @return The value to be sent to the server.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/15
	 */
	public static JsonObject ofMetrics(IOPipeContext __c, IOPipeMetrics __m)
		throws NullPointerException
	{
		if (__c == null || __m == null)
			throw new NullPointerException();
		
		throw new Error("TODO");
	}
	
	/**
	 * Builds a report of a timeout.
	 *
	 * @param __c The context which timed out.
	 * @param __count The number of methods 
	 * @return The value to be sent to the server.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/15
	 */
	public static JsonObject ofTimeout(IOPipeContext __c, int __count)
		throws NullPointerException
	{
		if (__c == null)
			throw new NullPointerException();
		
		throw new Error("TODO");
	}
}

