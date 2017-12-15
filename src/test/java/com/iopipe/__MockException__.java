package com.iopipe;

/**
 * This is a mock exception which is not really an error but is used for
 * debugging purposes.
 *
 * @since 2017/12/15
 */
public final class __MockException__
	extends RuntimeException
{
	/**
	 * Initializes with no message or cause.
	 *
	 * @since 2017/12/15
	 */
	public __MockException__()
	{
	}
	
	/**
	 * Initializes with a message and no cause.
	 *
	 * @param __m The reason to use.
	 * @since 2017/12/15
	 */
	public __MockException__(String __m)
	{
		super(__m);
	}
	
	/**
	 * Initializes with a cause and no message.
	 *
	 * @param __c The cause of the exception.
	 * @since 2017/12/15
	 */
	public __MockException__(Throwable __c)
	{
		super(__c);
	}
	
	/**
	 * Initializes with a message and cause.
	 *
	 * @param __m The reason to use.
	 * @param __c The cause of the exception.
	 * @since 2017/12/15
	 */
	public __MockException__(String __m, Throwable __c)
	{
		super(__m, __c);
	}
}

