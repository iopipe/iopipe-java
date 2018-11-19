package com.iopipe.plugin.eventinfo;

import com.iopipe.CustomMetric;
import com.iopipe.IOpipeExecution;
import com.iopipe.plugin.IOpipePluginExecution;
import java.util.concurrent.atomic.AtomicReference;

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
		// Do not try anything if there is no object to decode
		IOpipeExecution execution = this.execution;
		Object input = execution.input();
		if (input == null)
			return;
		
		// Get decoder for this object
		EventInfoDecoder decoder = this.decoders.getDecoder(input.getClass());
		if (decoder == null)
			return;
		
		// Parse the event
		ValueAcceptor va = new ValueAcceptor(decoder.eventType());
		try
		{
			decoder.accept(va, input);
		}
		
		// Failed to decode
		catch (Throwable e)
		{
			return;
		}
		
		// Add all custom metrics
		execution.customMetrics(va.get());
		execution.label("@iopipe/plugin-event-info");
		execution.label("@iopipe/" + decoder.slugifiedEventType());
	}
}

