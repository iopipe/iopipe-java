package com.iopipe.plugin.profiler;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class polls a thread group
 *
 * @since 2018/02/12
 */
final class __Poller__
	implements Runnable
{
	/** The tracker to write to. */
	protected final Tracker tracker;
	
	/** The thread group to poll for events. */
	protected final ThreadGroup group;
	
	/** Should execution stop? */
	final AtomicBoolean _stop =
		new AtomicBoolean();
	
	/**
	 * Initializes the thread poller.
	 *
	 * @param __t The tracker state.
	 * @param __g The group to poll.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/02/12
	 */
	__Poller__(Tracker __t, ThreadGroup __g)
		throws NullPointerException
	{
		if (__t == null || __g == null)
			throw new NullPointerException();
		
		this.tracker = __t;
		this.group = __g;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/02/12
	 */
	@Override
	public void run()
	{
		ThreadGroup group = this.group;
		Tracker tracker = this.tracker;
		AtomicBoolean stop = this._stop;
		
		// Used as temporary storage for active thread enumeration
		Thread[] threads = new Thread[1];
		
		// Used to measure how long a method has been in execution
		long basetime = System.nanoTime(),
			lasttime = basetime;
		
		// Keep polling threads
		int samplerate = ProfilerExecution.SAMPLE_RATE,
			resttime = 0;
		for (;;)
		{
			// Sleep for the sample rate time using the higher precision
			// sleep
			if (resttime > 0)
				try
				{
					Thread.sleep(resttime / 1_000_000,
						resttime % 1_000_000);
				}
				catch (InterruptedException e)
				{
					// Ignore
				}
			
			// Stop polling?
			if (stop.get())
				break;
			
			// Calculate how long the method has been running, this is used
			// to measure real time
			long nowtime = System.nanoTime(),
				runtime = nowtime - basetime;
			int reltime = (int)(nowtime - lasttime);
			lasttime = nowtime;
			
			// Try to resize the array based on the number of active threads
			int guessedactivecount = group.activeCount();
			if (guessedactivecount > threads.length)
				threads = new Thread[guessedactivecount];
			
			// Enumerate all active threads
			int count = group.enumerate(threads, false);
			
			// Handle traces for all threads
			for (int i = 0; i < count; i++)
			{
				Thread thread = threads[i];
				tracker.parseStackTrace(runtime, reltime, thread);
			}
			
			// Rest for a duration so that the next sample is the sampling
			// rate after this one
			resttime = samplerate - (int)(System.nanoTime() - nowtime);
			if (resttime > samplerate)
				resttime = samplerate;
		}
	}
}

