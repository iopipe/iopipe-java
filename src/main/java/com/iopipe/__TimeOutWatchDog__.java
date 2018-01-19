package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This class is used to log a timeout for a single execution of a context.
 *
 * @since 2017/12/20
 */
final class __TimeOutWatchDog__
	implements Runnable
{
	/** Logging. */
	private static final Logger _LOGGER =
		LogManager.getLogger(__TimeOutWatchDog__.class);
	
	/**
	 * The minimum sleep threshold.
	 *
	 * Since system load and such does vary, it is very possible that the
	 * accuracy of the sleep call is not that great for really small durations.
	 * We can just burn away some CPU cycles.
	 */
	private static final int _SLEEP_THRESHOLD =
		10;
	
	/** The service to use when sending reports. */
	protected final IOpipeService service;
	
	/** The configuration used. */
	protected final IOpipeConfiguration config;
	
	/** The context being executed. */
	protected final Context context;
	
	/** The thread which may have a timeout generated for it. */
	protected final Thread sourcethread;
	
	/** The thread which is waiting for timeout. */
	protected final Thread timeoutthread;
	
	/** The timeout window. */
	protected final int windowtime;
	
	/** Has this execution been coldstarted? */
	protected final boolean coldstart;
	
	/** Has execution finished? */
	private final AtomicBoolean _finished =
		new AtomicBoolean();
	
	/** This is set to true when the timeout has been finished. */
	final AtomicBoolean _generated =
		new AtomicBoolean();
	
	/**
	 * Initializes the watch dog.
	 *
	 * @param __sv The service being watched.
	 * @param __context The context to generate timeouts for.
	 * @param __src The source thread of execution.
	 * @param __wt The duration of the timeout window.
	 * @param __cs Is this a cold start and thus the first execution ever
	 * to run on the JVM?
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/20
	 */
	__TimeOutWatchDog__(IOpipeService __sv, Context __context, Thread __src,
		int __wt, boolean __cs)
		throws NullPointerException
	{
		if (__sv == null || __context == null || __src == null)
			throw new NullPointerException();
		
		this.service = __sv;
		this.config = __sv.config();
		this.context = __context;
		this.sourcethread = __src;
		this.windowtime = __wt;
		this.coldstart = __cs;
		
		Thread timeoutthread = new Thread(this,
			"IOpipe-WatchDog-" + System.identityHashCode(__context));
		timeoutthread.setDaemon(true);
		this.timeoutthread = timeoutthread;
		timeoutthread.start();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/20
	 */
	@Override
	public void run()
	{
		_LOGGER.debug("Started watchdog thread.");
		
		Context context = this.context;
		AtomicBoolean finished = this._finished;
		int windowtime = this.windowtime;
		
		for (;;)
		{
			// Sleep to pass the time by because otherwise CPU cycles will
			// just be burnt, but do not sleep for very small values because
			// most OSes
			int remaining = context.getRemainingTimeInMillis() - windowtime;
			if (remaining > _SLEEP_THRESHOLD)
			{
				try
				{
					Thread.sleep(remaining);
				}
			
				// Who dare interrupt my slumber?
				catch (InterruptedException e)
				{
					// Execution finished
					if (finished.get())
						return;
				}
				
				// Woke up from sleep, so the remaining time is completely
				// wrong now
				remaining = context.getRemainingTimeInMillis() - windowtime;
			}
			
			// Timed out
			if (remaining <= 0)
			{
				// A response from the main thread was server was generated
				// Whichever thread sets this variable first will be the one
				// to make the report
				if (this._generated.getAndSet(true))
					return;
				
				IOpipeConfiguration config = this.config;
				Thread sourcethread = this.sourcethread;
				
				_LOGGER.debug("Thread {} timed out.", sourcethread);
				
				// Generate a timeout exception, but for the ease of use in
				// debugging use the stack trace of the thread which timed out
				IOpipeTimeOutException reported = new IOpipeTimeOutException(
					"Execution timed out.");
				reported.setStackTrace(sourcethread.getStackTrace());
				
				// Send report to the service
				IOpipeMeasurement measurement = new IOpipeMeasurement(config,
					context);
				measurement.__setThrown(reported);
				this.service.__sendRequest(measurement.buildRequest());
				
				// Do not need to execute anymore
				return;
			}
		}
	}
	
	/**
	 * Indicates that the method being executed has finished and that the
	 * timeout watcher should stop running.
	 *
	 * @since 2017/12/20
	 */
	final void __finished()
	{
		// First set the execution to finished before waking the thread up
		this._finished.set(true);
		
		// Interrupt the thread so it wakes from its waiting doze
		this.timeoutthread.interrupt();
	}
}

