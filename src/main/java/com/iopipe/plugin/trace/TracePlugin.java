package com.iopipe.plugin.trace;

import java.lang.ref.Reference;
import com.iopipe.IOpipeExecution;
import com.iopipe.IOpipeMeasurement;
import com.iopipe.plugin.IOpipePlugin;
import com.iopipe.plugin.IOpipePluginExecution;

/**
 * This is the trace plugin which is used to track specific marks and measure
 * performance.
 *
 * @since 2018/01/20
 */
public class TracePlugin
	implements IOpipePlugin
{
	/**
	 * {@inheritDoc}
	 * @since 2018/01/20
	 */
	@Override
	public IOpipePluginExecution execute(Reference<IOpipeExecution> __e,
		boolean __enabled)
	{
		if (__e == null)
			throw new NullPointerException();
		
		return new TraceExecution(__e.get().measurement(), __enabled);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/20
	 */
	@Override
	public Class<? extends IOpipePluginExecution> executionClass()
	{
		return TraceExecution.class;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/20
	 */
	@Override
	public String homepage()
	{
		return "https://github.com/iopipe/iopipe-java-core";
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/20
	 */
	@Override
	public String name()
	{
		return "trace";
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/20
	 */
	@Override
	public String version()
	{
		return "0.1.0";
	}
}

