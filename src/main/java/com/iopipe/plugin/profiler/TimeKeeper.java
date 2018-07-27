package com.iopipe.plugin.profiler;

import java.util.concurrent.atomic.AtomicLong;

/**
 * This keeps track of the various times.
 *
 * @since 2018/02/20
 */
public final class TimeKeeper
{
	/** Absolute time. */
	private final AtomicLong _abs =
		new AtomicLong();
	
	/** Time spent at the top of the stack. */
	private final AtomicLong _self =
		new AtomicLong();
	
	/**
	 * Returns the absolute time.
	 *
	 * @return The absolute time.
	 * @since 2018/02/20
	 */
	public final long absolute()
	{
		return this._abs.get();
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
		this._abs.getAndAdd(__v);
		if (__self)
			this._self.getAndAdd(__v);
	}
	
	/**
	 * Returns the self time.
	 *
	 * @return The self time.
	 * @since 2018/02/20
	 */
	public final long self()
	{
		return this._self.get();
	}
}
