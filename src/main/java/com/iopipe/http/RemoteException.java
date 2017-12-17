package com.iopipe.http;

/**
 * This is thrown when there is an error connecting to, sending to, or
 * receiving from a remote connection.
 *
 * @since 2017/12/17
 */
public class RemoteException
	extends RuntimeException
{
	/**
	 * Initializes the exception with the given message.
	 *
	 * @param __m The message to use.
	 * @since 2017/12/17
	 */
	public RemoteException(String __m)
	{
		super(__m);
	}
	
	/**
	 * Initializes the exception with the given message and cause.
	 *
	 * @param __m The message to use.
	 * @param __c The cause of the exception.
	 * @since 2017/12/17
	 */
	public RemoteException(String __m, Throwable __c)
	{
		super(__m, __c);
	}
}
