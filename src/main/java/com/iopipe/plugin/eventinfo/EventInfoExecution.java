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
	
	/** Results of the plugin execution. */
	private final AtomicReference<__Result__> _result =
		new AtomicReference<>();
	
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
		AtomicReference<__Result__> result = this._result;
		
		// Try to get the object before locking on it
		__Result__ post = result.get();
		if (post == null)
			synchronized (result)
			{
				// After lock, try to get it again
				post = result.get();
				if (post == null)
				{
					// Wait for a short duration and give the report thread
					// a final chance to complete decoding
					try
					{
						result.wait(20L);
					}
					catch (InterruptedException e)
					{
					}
					
					// Just implicitely get it
					post = result.get();
				}
			}
		
		// No object was returned so do nothing
		if (post == null)
			return;
		
		// Add all custom metrics
		IOpipeExecution execution = this.execution;
		execution.measurement().addCustomMetrics(post._metrics);
		execution.label("@iopipe/plugin-event-info");
		execution.label("@iopipe/" + post._decoder.slugifiedEventType());
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
		// Setup thread which runs in the background which decodes the object
		// that was input
		Thread worker = new Thread(new __Worker__(this.execution.input(),
			this._result, this.decoders), "IOpipe-EventInfoWorker");
		worker.setDaemon(true);
		
		// Start it
		worker.start();
	}
	
	/**
	 * This class contains the worker which runs the decoder and reports
	 *
	 * @since 2018/04/23
	 */
	private static final class __Worker__
		implements Runnable
	{
		/** The object to generate a report for. */
		protected final Object object;
		
		/** Where the report will go. */
		protected final AtomicReference<__Result__> result;
		
		/** Decoders to use to parse the object with. */
		protected final EventInfoDecoders decoders;
		
		/**
		 * Initializes the worker.
		 *
		 * @param __o The object to work with.
		 * @param __r Where the result is stored, this will be locked on.
		 * @param __d Decoders to use to parse objects.
		 * @throws NullPointerException If no result destination or decoders
		 * were specified.
		 * @since 2018/04/24
		 */
		private __Worker__(Object __o, AtomicReference<__Result__> __r,
			EventInfoDecoders __d)
			throws NullPointerException
		{
			if (__r == null)
				throw new NullPointerException();
			
			this.object = __o;
			this.result = __r;
			this.decoders = __d;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2018/04/23
		 */
		@Override
		public final void run()
		{
			// Determine the custom metrics to use for the event
			EventInfoDecoder[] decoder = new EventInfoDecoder[1];
			CustomMetric[] metrics = this.decoders.decode(this.object,
				decoder);
			if (metrics == null)
				metrics = new CustomMetric[0];
			
			// Store result
			AtomicReference<__Result__> result = this.result;
			result.set(new __Result__(decoder[0], metrics));
			
			// Notify any threads that are waiting on this thread that a
			// result was just made available
			synchronized (result)
			{
				result.notify();
			}
		}
	}
}

