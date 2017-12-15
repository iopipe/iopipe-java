package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import java.util.Objects;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

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
		
		JsonObjectBuilder rv = Json.createObjectBuilder();
		__fillCommon(__c, rv);
		
		long duration = __m.getDuration();
		if (duration >= 0)
			rv.add("duration", duration);
		
		Throwable thrown = __m.getThrown();
		if (thrown != null)
		{
			throw new Error("TODO");
		}
		
		return rv.build();
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
		
		JsonObjectBuilder rv = Json.createObjectBuilder();
		__fillCommon(__c, rv);
		
		if (true)
			throw new Error("TODO");
		
		return rv.build();
	}
	
	/**
	 * Fills a common JSON object with parameters to be sent to the remote
	 * server.
	 *
	 * @param __c The execution context.
	 * @param __o The object to write base information into.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/15
	 */
	private static void __fillCommon(IOPipeContext __c, JsonObjectBuilder __o)
		throws NullPointerException
	{
		if (__c == null || __o == null)
			throw new NullPointerException();
		
		__o.add("aws", __generateAws(__c));
		
		if (true)
			throw new Error("TODO");
	}
	
	/**
	 * Generates the Amazon web service report.
	 *
	 * @param __c The context to use.
	 * @return The AWS information object.
	 * @since 2017/12/15
	 */
	private static JsonObject __generateAws(IOPipeContext __c)
		throws NullPointerException
	{
		if (__c == null)
			throw new NullPointerException();
		
		Context awscontext = __c.context();
		JsonObjectBuilder rv = Json.createObjectBuilder();
		
		rv.add("functionName", awscontext.getFunctionName());
		rv.add("functionVersion", awscontext.getFunctionVersion());
		rv.add("awsRequestId", awscontext.getAwsRequestId());
		rv.add("invokedFunctionArn", awscontext.getInvokedFunctionArn());
		rv.add("logGroupName", awscontext.getLogGroupName());
		rv.add("logStreamName", awscontext.getLogStreamName());
		rv.add("memoryLimitInMB", awscontext.getMemoryLimitInMB());
		rv.add("getRemainingTimeInMillis",
			awscontext.getRemainingTimeInMillis());
		rv.add("traceId", Objects.toString(
			System.getenv("_X_AMZN_TRACE_ID"), "unknown"));
		
		return rv.build();
	}
}

