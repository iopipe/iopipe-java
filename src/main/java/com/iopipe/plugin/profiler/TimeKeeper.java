package com.iopipe.plugin.profiler;

/**
 * This keeps track of the various times.
 *
 * @since 2018/02/20
 */
public final class TimeKeeper
{
	/** Absolute time. */
	private volatile long _abs;
	
	/** Time spent at the top of the stack. */
	private volatile long _self;
	
	/**
	 * Returns the absolute time.
	 *
	 * @return The absolute time.
	 * @since 2018/02/20
	 */
	public final long absolute()
	{
		return this._abs;
	}
	
	/**
	 * Adds time to the timer.
	 *
	 * @param __self Include self time?
	 * @param __v The time to add.
	 * @since 2018/02/20
	 */
	public final void addTime(boolean __self, long __v)
	{
		this._abs += __v;
		if (__self)
			this._self += __v;
	}
	
	/**
	 * Returns the self time.
	 *
	 * @return The self time.
	 * @since 2018/02/20
	 */
	public final long self()
	{
		return this._self;
	}
}
