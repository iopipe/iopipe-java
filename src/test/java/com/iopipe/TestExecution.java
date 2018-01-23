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
	
	/** Is this plugin enabled? */
	final boolean _enabled;
	
	/**
	 * Initializes the test execution.
	 *
	 * @param __e The execution to modify.
	 * @param __enabled Is this plugin enabled?
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/20
	 */
	public TestExecution(IOpipeExecution __e, boolean __enabled)
		throws NullPointerException
	{
		if (__e == null)
			throw new NullPointerException();
		
		this.execution = __e;
		this._enabled = __enabled;
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

