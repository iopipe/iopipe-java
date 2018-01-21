package com.iopipe;

import com.iopipe.plugin.IOpipePlugin;
import com.iopipe.plugin.IOpipePluginExecution;
import com.iopipe.plugin.IOpipePluginPreExecutable;
import com.iopipe.plugin.IOpipePluginPostExecutable;
import java.lang.ref.Reference;

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
	public final IOpipePluginExecution execute(Reference<IOpipeExecution> __e)
		throws NullPointerException
	{
		if (__e == null)
			throw new NullPointerException();
		
		return new TestExecution(__e.get());
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
		return "testplugin";
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
			customMetric("pre-execute", "test");
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
			customMetric("post-execute", 2.0D);
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

