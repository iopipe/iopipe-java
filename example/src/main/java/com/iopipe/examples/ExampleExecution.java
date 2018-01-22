package com.iopipe.examples;

import com.iopipe.IOpipeExecution;
import com.iopipe.plugin.IOpipePluginExecution;

/**
 * This is an execution for the plugin.
 *
 * @since 2018/01/22
 */
public class ExampleExecution
	implements IOpipePluginExecution
{
	/** The execution to refer to. */
	protected final IOpipeExecution execution;
	
	/** The next message to store. */
	private volatile int _next =
		1;
	
	/**
	 * Initializes the execution.
	 *
	 * @param __e The execution to utilize.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/22
	 */
	public ExampleExecution(IOpipeExecution __e)
		throws NullPointerException
	{
		if (__e == null)
			throw new NullPointerException();
		
		this.execution = __e;
	}
	
	/**
	 * Records a message.
	 *
	 * @param __s The string to record.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/22
	 */
	public void message(String __s)
		throws NullPointerException
	{
		if (__s == null)
			throw new NullPointerException();
		
		this.execution.measurement().customMetric(
			"message-" + this._next++, __s);
	}
}

