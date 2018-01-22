package com.iopipe.examples;

import com.iopipe.IOpipeExecution;
import com.iopipe.plugin.IOpipePlugin;
import com.iopipe.plugin.IOpipePluginExecution;
import java.lang.ref.Reference;

/**
 * This is an example plugin, which is simple for example purposes.
 *
 * @since 2018/01/22
 */
public class ExamplePlugin
	implements IOpipePlugin
{
	/**
	 * {@inheritDoc}
	 * @since 2018/01/22
	 */
	@Override
	public final IOpipePluginExecution execute(Reference<IOpipeExecution> __e)
		throws NullPointerException
	{
		if (__e == null)
			throw new NullPointerException();
		
		return new ExampleExecution(__e.get());
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/22
	 */
	@Override
	public final Class<? extends IOpipePluginExecution> executionClass()
	{
		return ExampleExecution.class;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/22
	 */
	@Override
	public final String homepage()
	{
		return "https://github.com/iopipe/iopipe-java-core";
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/22
	 */
	@Override
	public final String name()
	{
		return "exampleplugin";
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/22
	 */
	@Override
	public final String version()
	{
		return "1.0.0";
	}
}

