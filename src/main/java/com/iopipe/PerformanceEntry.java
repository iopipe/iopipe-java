package com.iopipe;

import java.util.Objects;

/**
 * This class represents a single performance entry which defines
 *
 * @since 2018/01/19
 */
public final class PerformanceEntry
	implements Comparable<PerformanceEntry>
{
	/** The name of this entry. */
	protected final String name;
	
	/** The type of entry this is. */
	protected final String type;
	
	/** The start time in nanoseconds. */
	protected final long startns;
	
	/** The start time in milliseconds on the system clock. */
	protected final long startms;
	
	/** The duration of the entry in nanoseconds. */
	protected final long durationns;
	
	/**
	 * Initializes the performance entry.
	 *
	 * @param __name The name of this entry.
	 * @param __type The type of entry this is.
	 * @param __startns The start time in nanoseconds.
	 * @param __startms The start time in milliseconds on the system clock.
	 * @param __durationns The duration of the entry in nanoseconds.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/02/13
	 */
	public PerformanceEntry(String __name, String __type, long __startns,
		long __startms, long __durationns)
		throws NullPointerException
	{
		if (__name == null || __type == null)
			throw new NullPointerException();
		
		this.name = __name;
		this.type = __type;
		this.startns = __startns;
		this.startms = __startms;
		this.durationns = Math.max(0, __durationns);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/19
	 */
	@Override
	public final int compareTo(PerformanceEntry __o)
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
	public final long durationNanoTime()
	{
		return this.durationns;
	}
	
	/**
	 * Returns the name of the performance entry.
	 *
	 * @return The performance entry name.
	 * @since 2018/01/19
	 */
	public final String name()
	{
		return this.name;
	}
	
	/**
	 * Returns the starting time of this performance entry according to the
	 * high-precision monotonic clock.
	 *
	 * This must have a value compatible with {@link System#nanoTime()}.
	 *
	 * @return The start time of the entry using the monotic clock.
	 * @since 2018/01/19
	 */
	public final long startNanoTime()
	{
		return this.startns;
	}
	
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
	public final long startTimeMillis()
	{
		return this.startms;
	}
	
	/**
	 * Returns the type of performance entry this is.
	 *
	 * @return The type of performance entry.
	 * @since 2018/01/19
	 */
	public final String type()
	{
		return this.type;
	}
}

