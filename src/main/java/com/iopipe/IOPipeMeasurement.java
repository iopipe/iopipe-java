package com.iopipe;

import com.iopipe.http.RemoteRequest;

/**
 * This class is used to keep track of measurements during execution.
 *
 * @since 2017/12/15
 */
public final class IOPipeMeasurement
{
	/** The context this is taking the measurement for. */
	protected final IOPipeContext context;
	
	/** The exception which may have been thrown. */
	private volatile Throwable _thrown;
	
	/** The duration of execution in nanoseconds. */
	private volatile long _duration =
		Long.MIN_VALUE;
	
	/**
	 * Initializes the measurement holder.
	 *
	 * @param __c The context this holds measurements for.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/17
	 */
	public IOPipeMeasurement(IOPipeContext __c)
		throws NullPointerException
	{
		if (__c == null)
			throw new NullPointerException();
		
		this.context = __c;
	}
	
	/**
	 * Builds the request which is sent to the remote service.
	 *
	 * @return The remote request to send to the service.
	 * @since 2017/12/17
	 */
	public RemoteRequest buildRequest()
	{
		throw new Error("TODO");
	}
	
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

