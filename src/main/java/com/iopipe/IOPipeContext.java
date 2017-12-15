package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import java.util.function.Supplier;

/**
 * This class provides a means of wrapping the IOPipe service and then
 * generating a report for each operation. This class may be called multiple
 * times as needed if the context remains the same throughout multiple
 * invocations.
 *
 * @since 2017/12/14
 */
public final class IOPipeContext
{
	/** The context in which this runs under. */
	protected final Context context;
	
	/** The service configuration. */
	protected final IOPipeConfiguration config;
	
	/**
	 * Initializes this class and wraps the given execution context.
	 *
	 * @param __context The context to manage.
	 * @param __config The configuration for the service.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/14
	 */
	IOPipeContext(Context __context, IOPipeConfiguration __config)
		throws NullPointerException
	{
		if (__context == null || __config == null)
			throw new NullPointerException();
		
		this.context = __context;
		this.config = __config;
	}
	
	/**
	 * Runs the specified function and generates a report.
	 *
	 * @param <R> The value to return.
	 * @param __func The function to call which will get a generated report.
	 * @return The returned value.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/14
	 */
	public final <R> R run(Supplier<R> __func)
		throws NullPointerException
	{
		if (__func == null)
			throw new NullPointerException();
		
		throw new Error("TODO");
	}
}

