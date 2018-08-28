package com.iopipe.generic;

/**
 * This is thrown when the entry point is not valid.
 *
 * @since 2018/08/14
 */
public class InvalidEntryPointException
	extends RuntimeException
{
	/**
	 * Initialize the exception with no message or cause.
	 *
	 * @since 2018/08/14
	 */
	public InvalidEntryPointException()
	{
	}
	
	/**
	 * Initialize the exception with a message and no cause.
	 *
	 * @param __m The message.
	 * @since 2018/08/14
	 */
	public InvalidEntryPointException(String __m)
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
	public InvalidEntryPointException(String __m, Throwable __c)
	{
		super(__m, __c);
	}
	
	/**
	 * Initialize the exception with no message and with a cause.
	 *
	 * @param __c The cause.
	 * @since 2018/08/14
	 */
	public InvalidEntryPointException(Throwable __c)
	{
		super(__c);
	}
}

