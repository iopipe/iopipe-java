package com.iopipe;

/**
 * This is used when a request being executed has timed out.
 *
 * @since 2017/12/16
 */
public class IOPipeTimeOutException
	extends RuntimeException
{
	/**
	 * Initializes the exception with the given message.
	 *
	 * @param __m The message to use.
	 * @since 2017/12/16
	 */
	public IOPipeTimeOutException(String __m)
	{
		super(__m);
	}
}

