package com.iopipe.generic;

import com.amazonaws.services.lambda.runtime.Context;
import java.lang.invoke.MethodHandle;

/**
 * This class is used to adapt method handles from one call type to another
 * by just using a forwarding method call.
 *
 * @since 2018/08/24
 */
class __AWSAdapters__
{
	/**
	 * Not used.
	 *
	 * @since 2018/08/24
	 */
	private __AWSAdapters__()
	{
	}
	
	/**
	 * Forwards to a type 0 static method.
	 *
	 * @param __pass The target handle.
	 * @param __a Argument A.
	 * @param __b Argument B.
	 * @param __c The context.
	 * @return The result of execution.
	 * @throws Throwable On any exception.
	 * @since 2018/08/24
	 */
	static Object __type0Static(MethodHandle __pass, Object __a, Object __b,
		Context __c)
		throws Throwable
	{
		return __pass.invoke();
	}
	
	/**
	 * Forwards to a type 0 instance method.
	 *
	 * @param __pass The target handle.
	 * @param __i The object instance.
	 * @param __a Argument A.
	 * @param __b Argument B.
	 * @param __c The context.
	 * @return The result of execution.
	 * @throws Throwable On any exception.
	 * @since 2018/08/24
	 */
	static Object __type0Instance(MethodHandle __pass, Object __i, Object __a,
		Object __b, Context __c)
		throws Throwable
	{
		return __pass.invoke(__i);
	}
}

