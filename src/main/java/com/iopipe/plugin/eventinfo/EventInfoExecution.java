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
	
	/**
	 * Waits for the event info parsing thread to finish parsing the event
	 * type and registers all of the custom metrics used.
	 *
	 * @since 2018/04/24
	 */
	final void __post()
	{
		throw new Error("TODO");
	}
	
	/**
	 * Starts a thread which runs in the background that parses the input
	 * object and creates all of the custom metrics to be added to the report.
	 * This is done in another thread so that processing the input does not
	 * cause the executing method to block since that should be done as
	 * quickly as possible.
	 *
	 * @since 2018/04/024
	 */
	final void __pre()
	{
		throw new Error("TODO");
	}
}

