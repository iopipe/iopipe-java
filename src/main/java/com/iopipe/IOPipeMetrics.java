package com.iopipe;

/**
 * This class is used to keep track of metrics used during execution.
 *
 * @since 2017/12/15
 */
public final class IOPipeMetrics
{
	/** The exception which may have been thrown. */
	private volatile Throwable _thrown;
	
	/** The duration of execution in nanoseconds. */
	private volatile long _duration =
		Long.MIN_VALUE;
	
	/**
	 * Sets the duration of execution.
	 *
	 * @param __d The execution duration in nanoseconds.
	 * @since 2017/12/15
	 */
	public void setDuration(long __ns)
	{
		this._duration = __ns;
	}
	
	/**
	 * Sets the throwable generated during execution.
	 *
	 * @param __t The generated throwable.
	 * @since 2017/12/15
	 */
	public void setThrown(Throwable __t)
	{
		this._thrown = __t;
	}
}

