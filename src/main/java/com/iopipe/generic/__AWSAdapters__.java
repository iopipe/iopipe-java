package com.iopipe.generic;

import com.amazonaws.services.lambda.runtime.Context;
import com.iopipe.IOpipeExecution;
import java.io.InputStream;
import java.io.OutputStream;
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
	 * Forwards to a type 0 instance method.
	 *
	 * @param __pass The target handle.
	 * @param __i The object instance.
	 * @param __a Argument A.
	 * @param __c The context.
	 * @return The result of execution.
	 * @throws Throwable On any exception.
	 * @since 2018/08/24
	 */
	static Object __type0Instance(MethodHandle __pass, Object __i, Object __a,
		Context __c)
		throws Throwable
	{
		return __pass.invoke(__i);
	}
	
	/**
	 * Forwards to a type 0 static method.
	 *
	 * @param __pass The target handle.
	 * @param __a Argument A.
	 * @param __c The context.
	 * @return The result of execution.
	 * @throws Throwable On any exception.
	 * @since 2018/08/24
	 */
	static Object __type0Static(MethodHandle __pass, Object __a, Context __c)
		throws Throwable
	{
		return __pass.invoke();
	}
	
	/**
	 * Forwards to a type 1 instance method.
	 *
	 * @param __pass The target handle.
	 * @param __i The object instance.
	 * @param __a Argument A.
	 * @param __c The context.
	 * @return The result of execution.
	 * @throws Throwable On any exception.
	 * @since 2018/08/24
	 */
	static Object __type1Instance(MethodHandle __pass, Object __i, Object __a,
		Context __c)
		throws Throwable
	{
		return __pass.invoke(__i, __a);
	}
	
	/**
	 * Forwards to a type 1 static method.
	 *
	 * @param __pass The target handle.
	 * @param __a Argument A.
	 * @param __c The context.
	 * @return The result of execution.
	 * @throws Throwable On any exception.
	 * @since 2018/08/24
	 */
	static Object __type1Static(MethodHandle __pass, Object __a, Context __c)
		throws Throwable
	{
		return __pass.invoke(__a);
	}
	
	/**
	 * Forwards to type 3 instance method.
	 *
	 * @param __pass The target handle.
	 * @param __i The object instance.
	 * @param __in The input stream.
	 * @param __out The output stream.
	 * @param __c The context.
	 * @throws Throwable On any exception.
	 * @since 2018/08/24
	 */
	static void __type3Instance(MethodHandle __pass, Object __i,
		InputStream __in, OutputStream __out, Context __c)
		throws Throwable
	{
		__pass.invoke(__i, __in, __out);
	}
	
	/**
	 * Forwards to type 3 instance method.
	 *
	 * @param __pass The target handle.
	 * @param __in The input stream.
	 * @param __out The output stream.
	 * @param __c The context.
	 * @throws Throwable On any exception.
	 * @since 2018/08/24
	 */
	static void __type3Static(MethodHandle __pass,
		InputStream __in, OutputStream __out, Context __c)
		throws Throwable
	{
		__pass.invoke(__in, __out);
	}
	
	/**
	 * Forwards to a type 5 instance method.
	 *
	 * @param __pass The target handle.
	 * @param __i The object instance.
	 * @param __a Argument A.
	 * @param __c The context.
	 * @return The result of execution.
	 * @throws Throwable On any exception.
	 * @since 2018/08/24
	 */
	static Object __type5Instance(MethodHandle __pass, Object __i, Object __a,
		Context __c)
		throws Throwable
	{
		return __pass.invoke(__i, IOpipeExecution.currentExecution(), __a);
	}
	
	/**
	 * Forwards to a type 5 static method.
	 *
	 * @param __pass The target handle.
	 * @param __a Argument A.
	 * @param __c The context.
	 * @return The result of execution.
	 * @throws Throwable On any exception.
	 * @since 2018/08/24
	 */
	static Object __type5Static(MethodHandle __pass, Object __a, Context __c)
		throws Throwable
	{
		return __pass.invoke(IOpipeExecution.currentExecution(), __a);
	}
	
	/**
	 * Forwards to type 6 instance method.
	 *
	 * @param __pass The target handle.
	 * @param __i The object instance.
	 * @param __in The input stream.
	 * @param __out The output stream.
	 * @param __c The context.
	 * @throws Throwable On any exception.
	 * @since 2018/08/24
	 */
	static void __type6Instance(MethodHandle __pass, Object __i,
		InputStream __in, OutputStream __out, Context __c)
		throws Throwable
	{
		__pass.invoke(__i, IOpipeExecution.currentExecution(), __in, __out);
	}
	
	/**
	 * Forwards to type 6 instance method.
	 *
	 * @param __pass The target handle.
	 * @param __in The input stream.
	 * @param __out The output stream.
	 * @param __c The context.
	 * @throws Throwable On any exception.
	 * @since 2018/08/24
	 */
	static void __type6Static(MethodHandle __pass,
		InputStream __in, OutputStream __out, Context __c)
		throws Throwable
	{
		__pass.invoke(IOpipeExecution.currentExecution(), __in, __out);
	}
}

