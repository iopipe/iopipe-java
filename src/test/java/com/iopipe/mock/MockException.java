package com.iopipe.mock;

/**
 * This is a mock exception which is not really an error but is used for
 * debugging purposes.
 *
 * @since 2017/12/15
 */
public final class MockException
	extends RuntimeException
{
	/**
	 * Initializes with no message or cause.
	 *
	 * @since 2017/12/15
	 */
	public MockException()
	{
	}
	
	/**
	 * Initializes with a message and no cause.
	 *
	 * @param __m The reason to use.
	 * @since 2017/12/15
	 */
	public MockException(String __m)
	{
		super(__m);
	}
	
	/**
	 * Initializes with a cause and no message.
	 *
	 * @param __c The cause of the exception.
	 * @since 2017/12/15
	 */
	public MockException(Throwable __c)
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
	public MockException(String __m, Throwable __c)
	{
		super(__m, __c);
	}
}

