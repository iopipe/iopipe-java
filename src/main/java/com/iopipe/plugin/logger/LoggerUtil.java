package com.iopipe.plugin.logger;

import com.iopipe.IOpipeExecution;
import com.iopipe.plugin.NoSuchPluginException;

/**
 * These are helper methods to add log messages to the current execution
 * without needing to access it from other code.
 *
 * @since 2018/09/26
 */
public final class LoggerUtil
{
	/**
	 * Not used.
	 *
	 * @since 2018/09/26
	 */
	private LoggerUtil()
	{
	}
	
	/**
	 * Logs the given message.
	 *
	 * @param __v The logging level.
	 * @param __n The name of the log source.
	 * @param __c The message used.
	 * @since 2018/09/26
	 */
	public static final void log(Enum<?> __v, String __n, char[] __c)
	{
		LoggerExecution exec = LoggerUtil.__exec();
		if (exec != null)
			exec.log(__v, __n, __c);
	}
	
	/**
	 * Logs the given message.
	 *
	 * @param __utcms The current time in UTC milliseconds.
	 * @param __v The logging level.
	 * @param __n The name of the log source.
	 * @param __c The message used.
	 * @since 2018/09/26
	 */
	public static final void log(long __utcms, Enum<?> __v, String __n, char[] __c)
	{
		LoggerExecution exec = LoggerUtil.__exec();
		if (exec != null)
			exec.log(__utcms, __v, __n, __c);
	}
	
	/**
	 * Logs the given message.
	 *
	 * @param __v The logging level.
	 * @param __n The name of the log source.
	 * @param __c The message used.
	 * @param __o The offset.
	 * @param __l The length.
	 * @throws IndexOutOfBoundsException If the offset and/or length are
	 * negative or exceed the array bounds.
	 * @since 2018/09/26
	 */
	public static final void log(Enum<?> __v, String __n, char[] __c, int __o, int __l)
		throws IndexOutOfBoundsException
	{
		LoggerExecution exec = LoggerUtil.__exec();
		if (exec != null)
			exec.log(__v, __n, __c, __o, __l);
	}
	
	/**
	 * Logs the given message.
	 *
	 * @param __utcms The current time in UTC milliseconds.
	 * @param __v The logging level.
	 * @param __n The name of the log source.
	 * @param __c The message used.
	 * @param __o The offset.
	 * @param __l The length.
	 * @throws IndexOutOfBoundsException If the offset and/or length are
	 * negative or exceed the array bounds.
	 * @since 2018/09/26
	 */
	public static final void log(long __utcms, Enum<?> __v, String __n, char[] __c, int __o,
		int __l)
		throws IndexOutOfBoundsException
	{
		LoggerExecution exec = LoggerUtil.__exec();
		if (exec != null)
			exec.log(__utcms, __v, __n, __c, __o, __l);
	}
	
	/**
	 * Logs the given message.
	 *
	 * @param __v The logging level.
	 * @param __n The name of the log source.
	 * @param __msg The message used.
	 * @since 2018/09/26
	 */
	public static final void log(Enum<?> __v, String __n, CharSequence __msg)
	{
		LoggerExecution exec = LoggerUtil.__exec();
		if (exec != null)
			exec.log(__v, __n, __msg);
	}
	
	/**
	 * Logs the given message.
	 *
	 * @param __utcms The current time in UTC milliseconds.
	 * @param __v The logging level.
	 * @param __msg The message used.
	 * @since 2018/09/26
	 */
	public static final void log(long __utcms, Enum<?> __v, String __n, CharSequence __msg)
	{
		LoggerExecution exec = LoggerUtil.__exec();
		if (exec != null)
			exec.log(__utcms, __v, __n, __msg);
	}
	
	/**
	 * Logs the given message.
	 *
	 * @param __v The logging level.
	 * @param __c The message used.
	 * @since 2018/09/26
	 */
	public static final void log(String __v, String __n, char[] __c)
	{
		LoggerExecution exec = LoggerUtil.__exec();
		if (exec != null)
			exec.log(__v, __n, __c);
	}
	
	/**
	 * Logs the given message.
	 *
	 * @param __utcms The current time in UTC milliseconds.
	 * @param __v The logging level.
	 * @param __c The message used.
	 * @since 2018/09/26
	 */
	public static final void log(long __utcms, String __v, String __n, char[] __c)
	{
		LoggerExecution exec = LoggerUtil.__exec();
		if (exec != null)
			exec.log(__utcms, __v, __n, __c);
	}
	
	/**
	 * Logs the given message.
	 *
	 * @param __v The logging level.
	 * @param __c The message used.
	 * @param __o The offset.
	 * @param __l The length.
	 * @throws IndexOutOfBoundsException If the offset and/or length are
	 * negative or exceed the array bounds.
	 * @since 2018/09/26
	 */
	public static final void log(String __v, String __n, char[] __c, int __o, int __l)
		throws IndexOutOfBoundsException
	{
		LoggerExecution exec = LoggerUtil.__exec();
		if (exec != null)
			exec.log(__v, __n, __c, __o, __l);
	}
	
	/**
	 * Logs the given message.
	 *
	 * @param __utcms The current time in UTC milliseconds.
	 * @param __v The logging level.
	 * @param __c The message used.
	 * @param __o The offset.
	 * @param __l The length.
	 * @throws IndexOutOfBoundsException If the offset and/or length are
	 * negative or exceed the array bounds.
	 * @since 2018/09/26
	 */
	public static final void log(long __utcms, String __v, String __n, char[] __c, int __o,
		int __l)
		throws IndexOutOfBoundsException
	{
		LoggerExecution exec = LoggerUtil.__exec();
		if (exec != null)
			exec.log(__utcms, __v, __n, __c, __o, __l);
	}
	
	/**
	 * Logs the given message.
	 *
	 * @param __v The logging level.
	 * @param __msg The message used.
	 * @since 2018/09/26
	 */
	public static final void log(String __v, String __n, CharSequence __msg)
	{
		LoggerExecution exec = LoggerUtil.__exec();
		if (exec != null)
			exec.log(__v, __n, __msg);
	}
	
	/**
	 * Logs the given message.
	 *
	 * @param __utcms The current time in UTC milliseconds.
	 * @param __v The logging level.
	 * @param __msg The message used.
	 * @since 2018/09/26
	 */
	public static final void log(long __utcms, String __v, String __n, CharSequence __msg)
	{
		LoggerExecution exec = LoggerUtil.__exec();
		if (exec != null)
			exec.log(__utcms, __v, __n, __msg);
	}
	
	/**
	 * Returns the logger execution if the plugin is enabled.
	 *
	 * @return The execution or {@code null} if the plugin is not enabled or
	 * this is not wrapped by IOpipe.
	 * @since 2018/09/26
	 */
	private static final LoggerExecution __exec()
	{
		IOpipeExecution exec = IOpipeExecution.currentExecution();
		if (exec == null)
			return null;
		
		try
		{
			return exec.<LoggerExecution>plugin(LoggerExecution.class);
		}
		catch (ClassCastException|NoSuchPluginException e)
		{
			return null;
		}
	}
}

