package com.iopipe;

/**
 * This class represents an exception which was wrapped by IOpipe.
 *
 * @since 2018/08/16
 */
public class IOpipeWrappedException
	extends RuntimeException
{
	/**
	 * Initialize the exception with no message or cause.
	 *
	 * @since 2018/08/16
	 */
	public IOpipeWrappedException()
	{
	}
	
	/**
	 * Initialize the exception with a message and no cause.
	 *
	 * @param __m The message.
	 * @since 2018/08/16
	 */
	public IOpipeWrappedException(String __m)
	{
		super(__m);
	}
	
	/**
	 * Initialize the exception with a message and cause.
	 *
	 * @param __m The message.
	 * @param __c The cause.
	 * @since 2018/08/16
	 */
	public IOpipeWrappedException(String __m, Throwable __c)
	{
		super(__m, __c);
	}
	
	/**
	 * Initialize the exception with no message and with a cause.
	 *
	 * @param __c The cause.
	 * @since 2018/08/16
	 */
	public IOpipeWrappedException(Throwable __c)
	{
		super(__c);
	}
}

