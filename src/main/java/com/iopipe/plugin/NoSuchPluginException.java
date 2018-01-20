package com.iopipe.plugin;

/**
 * This is thrown when a plugin was requested, but it does not exist or is
 * disabled.
 *
 * @since 2018/01/20
 */
public class NoSuchPluginException
	extends RuntimeException
{
	/**
	 * Initializes the exception with the given message.
	 *
	 * @param __m The message to use.
	 * @since 2018/01/20
	 */
	public NoSuchPluginException(String __m)
	{
		super(__m);
	}
	
	/**
	 * Initializes the exception with the given message and cause.
	 *
	 * @param __m The message to use.
	 * @param __c The cause of the exception.
	 * @since 2018/01/20
	 */
	public NoSuchPluginException(String __m, Throwable __c)
	{
		super(__m, __c);
	}
}
