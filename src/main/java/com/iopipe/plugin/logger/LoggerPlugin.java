package com.iopipe.plugin.logger;

import com.iopipe.IOpipeExecution;
import com.iopipe.plugin.IOpipePlugin;
import com.iopipe.plugin.IOpipePluginExecution;
import com.iopipe.plugin.IOpipePluginPostExecutable;

/**
 * This class contains the definition of the logger plugin.
 *
 * @since 2018/09/24
 */
public final class LoggerPlugin
	implements IOpipePlugin
{
	/**
	 * {@inheritDoc}
	 * @since 2018/09/24
	 */
	@Override
	public final IOpipePluginExecution execute(IOpipeExecution __e)
		throws NullPointerException
	{
		if (__e == null)
			throw new NullPointerException();
		
		throw new Error("TODO");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/09/24
	 */
	@Override
	public final Class<? extends IOpipePluginExecution> executionClass()
	{
		return LoggerExecution.class;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/09/24
	 */
	@Override
	public final String name()
	{
		return "logger";
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/09/24
	 */
	@Override
	public final String version()
	{
		return "1.9.0";
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/09/24
	 */
	@Override
	public final boolean enabledByDefault()
	{
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/09/24
	 */
	@Override
	public final String homepage()
	{
		return "https://github.com/iopipe/iopipe-java/";
	}
}

