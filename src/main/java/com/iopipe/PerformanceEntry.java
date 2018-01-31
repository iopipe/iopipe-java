package com.iopipe;

import java.util.Objects;

/**
 * This interface is used as a base for performance entries and is used to
 * allow access to the common attributes for each entry.
 *
 * All performance entries must be {@link Comparable}, the sort order is the
 * start time in monotonic nanoseconds.
 *
 * @since 2018/01/19
 */
public interface PerformanceEntry
	extends Comparable<PerformanceEntry>
{
	/**
	 * Returns the name of the performance entry.
	 *
	 * @return The performance entry name.
	 * @since 2018/01/19
	 */
	public abstract String name();
	
	/**
	 * Returns the starting time of this performance entry according to the
	 * high-precision monotonic clock.
	 *
	 * This must have a value compatible with {@link System#nanoTime()}.
	 *
	 * @return The start time of the entry using the monotic clock.
	 * @since 2018/01/19
	 */
	public abstract long startNanoTime();
	
	/**
	 * Returns the starting time of this performance entry according to the
	 * system clock.
	 *
	 * This must have a value compatible with
	 * {@link System#currentTimeMillis()}.
	 *
	 * @return The start time of the entry according to the system clock.
	 * @since 2018/01/19
	 */
	public abstract long startTimeMillis();
	
	/**
	 * Returns the type of performance entry this is.
	 *
	 * @return The type of performance entry.
	 * @since 2018/01/19
	 */
	public abstract String type();
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/19
	 */
	@Override
	public default int compareTo(PerformanceEntry __o)
		throws NullPointerException
	{
		if (__o == null)
			throw new NullPointerException();
		
		long diff = this.startNanoTime() - __o.startNanoTime();
		if (diff < 0)
			return (int)Math.max(diff, Integer.MIN_VALUE);
		else if (diff > 0)
			return (int)Math.min(diff, Integer.MAX_VALUE);
		
		// Compare by name so that two entries do not occur at the same point
		return Objects.toString(this.name(), "").compareTo(
			Objects.toString(__o.name(), ""));
	}
	
	/**
	 * Returns the duration of this entry in the timeframe of the
	 * high-precision monotonic clock.
	 *
	 * This must have a value compatible with {@link System#nanoTime()}.
	 *
	 * @return The duration of the entry using the monotic clock.
	 * @since 2018/01/19
	 */
	public default long durationNanoTime()
	{
		return Math.abs(this.endNanoTime() - this.startNanoTime());
	}
	
	/**
	 * Returns the end time of this performance entry according to the
	 * high-precision monotonic clock.
	 *
	 * This must have a value compatible with {@link System#nanoTime()}.
	 *
	 * @return The start time of the entry using the monotic clock.
	 * @since 2018/01/19
	 */
	public default long endNanoTime()
	{
		return this.startNanoTime();
	}
	
	/**
	 * Returns the end time of this performance entry according to the
	 * system clock.
	 *
	 * This must have a value compatible with
	 * {@link System#currentTimeMillis()}.
	 *
	 * @return The end time of the entry according to the system clock.
	 * @since 2018/01/19
	 */
	public default long endTimeMillis()
	{
		return this.startTimeMillis();
	}
}

