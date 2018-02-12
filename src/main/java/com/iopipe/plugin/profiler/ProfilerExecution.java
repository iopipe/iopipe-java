package com.iopipe.plugin.profiler;

import com.iopipe.IOpipeExecution;
import com.iopipe.plugin.IOpipePluginExecution;

/**
 * This contains the execution state for the profile plugin. This class is not
 * intended to be used by the user.
 *
 * @since 2018/02/07
 */
public class ProfilerExecution
	implements IOpipePluginExecution
{
	/** The execution state. */
	protected final IOpipeExecution execution;
	
	/** Tracker state. */
	private final __Tracker__ _tracker =
		new __Tracker__();
	
	/** The tread which is pollng for profiling. */
	private volatile Thread _pollthread;
	
	/** The poller for execution. */
	private volatile __Poller__ _poller;
	
	/**
	 * Initializes the profiler state.
	 *
	 * @param __e The execution state.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/02/03
	 */
	public ProfilerExecution(IOpipeExecution __e)
		throws NullPointerException
	{
		if (__e == null)
			throw new NullPointerException();
		
		this.execution = __e;
	}
	
	/**
	 * Post execution.
	 *
	 * @since 2018/02/09
	 */
	final void __post()
	{
		// Tell the poller to stop and interrupt it so it wakes up from any
		// sleep state
		this._poller._stop = true;
		this._pollthread.interrupt();
	}
	
	/**
	 * Pre execution.
	 *
	 * @since 2018/02/09
	 */
	final void __pre()
	{
		// Setup poller which will constantly read thread state
		__Poller__ poller = new __Poller__(this._tracker,
			this.execution.threadGroup());
		this._poller = poller;
		
		// Initialize the polling thread
		Thread pollthread = new Thread(poller);
		pollthread.setDaemon(true);
		this._pollthread = pollthread;
		pollthread.start();
	}
}

