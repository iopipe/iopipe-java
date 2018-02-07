package com.iopipe.plugin.profiler;

import com.iopipe.IOpipeExecution;
import com.iopipe.plugin.IOpipePlugin;
import com.iopipe.plugin.IOpipePluginExecution;
import com.iopipe.plugin.IOpipePluginPostExecutable;
import com.iopipe.plugin.IOpipePluginPreExecutable;

/**
 * This class provides access to the profiler plugin which is used to profile
 * method execution.
 *
 * @since 2018/02/07
 */
public class ProfilerPlugin
	implements IOpipePlugin, IOpipePluginPreExecutable,
		IOpipePluginPostExecutable
{
	/**
	 * {@inheritDoc}
	 * @since 2018/02/07
	 */
	@Override
	public boolean enabledByDefault()
	{
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/02/07
	 */
	@Override
	public String homepage()
	{
		return "https://github.com/iopipe/iopipe-java";
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/02/07
	 */
	@Override
	public IOpipePluginExecution execute(IOpipeExecution __e)
		throws NullPointerException
	{
		throw new Error("TODO");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/02/07
	 */
	@Override
	public Class<? extends IOpipePluginExecution> executionClass()
	{
		return ProfilerExecution.class;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/02/07
	 */
	@Override
	public String name()
	{
		return "profiler";
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/02/07
	 */
	@Override
	public void preExecute(IOpipePluginExecution __e)
		throws NullPointerException
	{
		throw new Error("TODO");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/02/07
	 */
	@Override
	public void postExecute(IOpipePluginExecution __e)
		throws NullPointerException
	{
		throw new Error("TODO");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/02/07
	 */
	@Override
	public String version()
	{
		return "1.0.0";
	}
}

