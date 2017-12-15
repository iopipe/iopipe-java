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
	 * Returns the execution duration.
	 *
	 * @return The execution duration, if this is negative then it is not
	 * valid.
	 * @since 2017/12/15
	 */
	public long getDuration()
	{
		return this._duration;
	}
	
	/**
	 * Returns the thrown throwable.
	 *
	 * @return The throwable which was thrown or {@code null} if none was
	 * thrown.
	 * @since 2017/12/15
	 */
	public Throwable getThrown()
	{
		return this._thrown;
	}
	
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

