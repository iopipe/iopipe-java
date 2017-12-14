package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class provides access to the IOPipe service and allows for sending
 * metrics to the server.
 *
 * @since 2017/12/13
 */
public final class IOPipeService
{
	/** The configuration used to connect to the service. */
	protected final IOPipeConfiguration config;
	
	/**
	 * Initializes the IOPipe service using the default system configuration.
	 *
	 * @throws IllegalArgumentException If the default parameters are not
	 * valid.
	 * @since 2017/12/13
	 */
	public IOPipeService()
		throws IllegalArgumentException
	{
		this(IOPipeConfiguration.byDefault());
	}
	
	/**
	 * Initializes the IOPipe service using the specified configuration.
	 *
	 * @param __config The configuration to use.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/13
	 */
	public IOPipeService(IOPipeConfiguration __config)
		throws NullPointerException
	{
		if (__config == null)
			throw new NullPointerException();
		
		this.config = __config;
	}
	
	/**
	 * Runs the specified function and gathers metrics during the operation.
	 *
	 * @param __cont The Amazon AWS context this is running under.
	 * @param __func The function to execute.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/13
	 */
	public final void run(Context __cont, Runnable __func)
		throws NullPointerException
	{
		if (__cont == null || __func == null)
			throw new NullPointerException();
		
		this.<Object, Object, Object>run(__cont, null, null, (__a, __b) ->
			{
				__func.run();
				return null;
			});
	}
	
	/**
	 * Runs the specified function and gathers metrics during the operation.
	 *
	 * @param <R> The return value.
	 * @param __cont The Amazon AWS context this is running under.
	 * @param __func The function to execute.
	 * @return The result of the function call.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/13
	 */
	public final <R> R run(Context __cont, Supplier<R> __func)
		throws NullPointerException
	{
		if (__cont == null || __func == null)
			throw new NullPointerException();
		
		return this.<Object, Object, R>run(__cont, null, null,
			(__y, __z) -> __func.get());
	}
	
	/**
	 * Runs the specified function and gathers metrics during the operation.
	 *
	 * @param <A> The input type.
	 * @param <R> The return value.
	 * @param __cont The Amazon AWS context this is running under.
	 * @param __a The first parameter.
	 * @param __func The function to execute.
	 * @return The result of the function call.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/13
	 */
	public final <A, R> R run(Context __cont, A __a, Function<A, R> __func)
		throws NullPointerException
	{
		if (__cont == null || __func == null)
			throw new NullPointerException();
		
		return this.<A, Object, R>run(__cont, __a, null,
			(__y, __z) -> __func.apply(__a));
	}
	
	/**
	 * Runs the specified function and gathers metrics during the operation.
	 *
	 * @param <A> The first parameter type.
	 * @param <B> The second parameter type.
	 * @param <R> The return value.
	 * @param __cont The Amazon AWS context this is running under.
	 * @param __a The first parameter.
	 * @param __b The second parameter.
	 * @param __func The function to execute.
	 * @return The result of the function call.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/13
	 */
	public final <A, B, R> R run(Context __cont, A __a, B __b,
		BiFunction<A, B, R> __func)
		throws NullPointerException
	{
		if (__cont == null || __func == null)
			throw new NullPointerException();
		
		IOPipeConfiguration config = this.config;
		if (!config.isEnabled())
			return __func.apply(__a, __b);
		
		throw new Error("TODO");
	}
}

