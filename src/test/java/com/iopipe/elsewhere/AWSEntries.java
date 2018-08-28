package com.iopipe.elsewhere;

import com.amazonaws.services.lambda.runtime.Context;
import com.iopipe.IOpipeExecution;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * Checks that all of the AWS entry points are valid.
 *
 * @since 2018/08/24
 */
public class AWSEntries
{
	/**
	 * Type 0 entry.
	 *
	 * @return A value.
	 * @since 2018/08/24
	 */
	public Object instance0()
	{
		IOpipeExecution.currentExecution().label("instance0");
		return "squirrels";
	}
	
	/**
	 * Type 1 entry.
	 *
	 * @param __a Parameter.
	 * @return A value.
	 * @since 2018/08/24
	 */
	public Object instance1(Object __a)
	{
		IOpipeExecution.currentExecution().label("instance1");
		return __a;
	}
	
	/**
	 * Type 2 entry.
	 *
	 * @param __a Parameter.
	 * @param __c Context.
	 * @return A value.
	 * @since 2018/08/24
	 */
	public Object instance2(Object __a, Context __c)
	{
		IOpipeExecution.currentExecution().label("instance2");
		return __a;
	}
	
	/**
	 * Type 3 entry.
	 *
	 * @param __i Input.
	 * @param __o Output.
	 * @throws IOException On read/write errors.
	 * @since 2018/08/24
	 */
	public void instance3(InputStream __i, OutputStream __o)
		throws IOException
	{
		IOpipeExecution.currentExecution().label("instance3");
		AWSEntries.__copy(__i, __o);
	}
	
	/**
	 * Type 4 entry.
	 *
	 * @param __i Input.
	 * @param __o Output.
	 * @throws IOException On read/write errors.
	 * @since 2018/08/24
	 */
	public void instance4(InputStream __i, OutputStream __o, Context __c)
		throws IOException
	{
		IOpipeExecution.currentExecution().label("instance4");
		AWSEntries.__copy(__i, __o);
	}
	
	/**
	 * Type 5 entry.
	 *
	 * @param __exec Execution state.
	 * @param __a Parameter.
	 * @return A value.
	 * @since 2018/08/24
	 */
	public Object instance5(IOpipeExecution __exec, Object __a)
	{
		__exec.label("instance5");
		return __a;
	}
	
	/**
	 * Type 6 entry.
	 *
	 * @param __exec Execution state
	 * @param __i Input.
	 * @param __o Output.
	 * @throws IOException On read/write errors.
	 * @since 2018/08/24
	 */
	public void instance6(IOpipeExecution __exec, InputStream __i,
		OutputStream __o)
		throws IOException
	{
		__exec.label("instance6");
		AWSEntries.__copy(__i, __o);
	}
	
	/**
	 * Type 0 entry.
	 *
	 * @return A value.
	 * @since 2018/08/24
	 */
	public static Object static0()
	{
		IOpipeExecution.currentExecution().label("static0");
		return "squirrels";
	}
	
	/**
	 * Type 1 entry.
	 *
	 * @param __a Parameter.
	 * @return A value.
	 * @since 2018/08/24
	 */
	public static Object static1(Object __a)
	{
		IOpipeExecution.currentExecution().label("static1");
		return __a;
	}
	
	/**
	 * Type 2 entry.
	 *
	 * @param __a Parameter.
	 * @param __c Context.
	 * @return A value.
	 * @since 2018/08/24
	 */
	public static Object static2(Object __a, Context __c)
	{
		IOpipeExecution.currentExecution().label("static2");
		return __a;
	}
	
	/**
	 * Type 3 entry.
	 *
	 * @param __i Input.
	 * @param __o Output.
	 * @throws IOException On read/write errors.
	 * @since 2018/08/24
	 */
	public static void static3(InputStream __i, OutputStream __o)
		throws IOException
	{
		IOpipeExecution.currentExecution().label("static3");
		AWSEntries.__copy(__i, __o);
	}
	
	/**
	 * Type 4 entry.
	 *
	 * @param __i Input.
	 * @param __o Output.
	 * @throws IOException On read/write errors.
	 * @since 2018/08/24
	 */
	public static void static4(InputStream __i, OutputStream __o, Context __c)
		throws IOException
	{
		IOpipeExecution.currentExecution().label("static4");
		AWSEntries.__copy(__i, __o);
	}
	
	/**
	 * Type 5 entry.
	 *
	 * @param __exec Execution state.
	 * @param __a Parameter.
	 * @return A value.
	 * @since 2018/08/24
	 */
	public static Object static5(IOpipeExecution __exec, Object __a)
	{
		__exec.label("static5");
		return __a;
	}
	
	/**
	 * Type 6 entry.
	 *
	 * @param __exec Execution state
	 * @param __i Input.
	 * @param __o Output.
	 * @throws IOException On read/write errors.
	 * @since 2018/08/24
	 */
	public static void static6(IOpipeExecution __exec, InputStream __i,
		OutputStream __o)
		throws IOException
	{
		__exec.label("static6");
		AWSEntries.__copy(__i, __o);
	}
	
	/**
	 * Copies from the input to the output.
	 *
	 * @param __i The input.
	 * @param __o The output.
	 * @throws IOException On read/write errors.
	 * @since 2018/08/24
	 */
	private static void __copy(InputStream __i, OutputStream __o)
		throws IOException
	{
		for (;;)
		{
			int c = __i.read();
			
			if (c < 0)
				break;
			
			__o.write(c);
		}
	}
}

