package com.iopipe;

/**
 * This error is thrown when IOpipe has been misconfigured and it is unable
 * to allow the lambda to continue execution.
 *
 * @since 2018/08/14
 */
public class IOpipeFatalError
	extends Error
{
	/**
	 * Initialize the exception with no message or cause.
	 *
	 * @since 2018/08/14
	 */
	public IOpipeFatalError()
	{
	}
	
	/**
	 * Initialize the exception with a message and no cause.
	 *
	 * @param __m The message.
	 * @since 2018/08/14
	 */
	public IOpipeFatalError(String __m)
	{
		super(__m);
	}
	
	/**
	 * Initialize the exception with a message and cause.
	 *
	 * @param __m The message.
	 * @param __c The cause.
	 * @since 2018/08/14
	 */
	public IOpipeFatalError(String __m, Throwable __c)
	{
		super(__m, __c);
	}
	
	/**
	 * Initialize the exception with no message and with a cause.
	 *
	 * @param __c The cause.
	 * @since 2018/08/14
	 */
	public IOpipeFatalError(Throwable __c)
	{
		super(__c);
	}
}

