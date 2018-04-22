package com.iopipe.plugin.eventinfo;

import com.iopipe.IOpipeExecution;
import com.iopipe.plugin.IOpipePluginExecution;

/**
 * This is the trace plugin which is used to track specific marks and measure
 * performance.
 *
 * @since 2018/04/22
 */
public class EventInfoExecution
	implements IOpipePluginExecution
{
	/** The execution to track. */
	protected final IOpipeExecution execution;
	
	/** Decoders to use for events. */
	protected final EventInfoDecoders decoders;
	
	/**
	 * Initializes the plugin state for a single execution.
	 *
	 * @param __exec The execution to record metrics into and where to get
	 * the event source from.
	 * @param __ds The decoders to use for events.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/04/22
	 */
	public EventInfoExecution(IOpipeExecution __exec, EventInfoDecoders __ds)
		throws NullPointerException
	{
		if (__exec == null || __ds == null)
			throw new NullPointerException();
		
		this.execution = __exec;
		this.decoders = __ds;
	}
}

