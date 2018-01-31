package com.iopipe.examples;

import com.iopipe.IOpipeExecution;
import com.iopipe.plugin.IOpipePluginExecution;

/**
 * This stores the state for this plugin within a single execution of the
 * example plugin. Internally the only state this stores is the message
 * number. This is created by the example plugin.
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

