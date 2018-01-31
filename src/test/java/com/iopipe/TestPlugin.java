package com.iopipe;

import com.iopipe.plugin.IOpipePlugin;
import com.iopipe.plugin.IOpipePluginExecution;
import com.iopipe.plugin.IOpipePluginPreExecutable;
import com.iopipe.plugin.IOpipePluginPostExecutable;

/**
 * This is a test plugin which exists within the test system.
 *
 * @since 2018/01/20
 */
public class TestPlugin
	implements IOpipePlugin, IOpipePluginPreExecutable,
		IOpipePluginPostExecutable
{
	/**
	 * {@inheritDoc}
	 * @since 2018/01/20
	 */
	@Override
	public final IOpipePluginExecution execute(IOpipeExecution __e)
		throws NullPointerException
	{
		if (__e == null)
			throw new NullPointerException();
		
		return new TestExecution(__e);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/20
	 */
	@Override
	public final Class<? extends IOpipePluginExecution> executionClass()
	{
		return TestExecution.class;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/20
	 */
	@Override
	public final String homepage()
	{
		return "https://github.com/iopipe/iopipe-java-core";
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/20
	 */
	@Override
	public final String name()
	{
		return "test";
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/20
	 */
	@Override
	public final void preExecute(IOpipePluginExecution __e)
		throws NullPointerException
	{
		if (__e == null)
			throw new NullPointerException();
			
		((TestExecution)__e).execution().measurement().
			customMetric("pre", "pre");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/20
	 */
	@Override
	public final void postExecute(IOpipePluginExecution __e)
		throws NullPointerException
	{
		if (__e == null)
			throw new NullPointerException();
			
		((TestExecution)__e).execution().measurement().
			customMetric("post", "post");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/20
	 */
	@Override
	public final String version()
	{
		return "1.0.0";
	}
}

