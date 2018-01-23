package com.iopipe;

import com.iopipe.IOpipeExecution;
import com.iopipe.plugin.IOpipePluginExecution;

/**
 * This is an execution for the plugin.
 *
 * @since 2018/01/20
 */
public class TestExecution
	implements IOpipePluginExecution
{
	/** The execution to refer to. */
	protected final IOpipeExecution execution;
	
	/**
	 * Initializes the test execution.
	 *
	 * @param __e The execution to modify.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/20
	 */
	public TestExecution(IOpipeExecution __e)
		throws NullPointerException
	{
		if (__e == null)
			throw new NullPointerException();
		
		this.execution = __e;
	}
	
	/**
	 * Returns the execution.
	 *
	 * @return The execution.
	 * @since 2018/01/20
	 */
	public final IOpipeExecution execution()
	{
		return this.execution;
	}
}

