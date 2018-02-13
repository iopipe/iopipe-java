package com.iopipe;

import java.util.Objects;

/**
 * This class represents a single performance entry which is used to record
 * the start time and the potential duration of an event.
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
		
		int rv = Long.compare(this.startns, __o.startns);
		if (rv != 0)
			return rv;
			
		rv = Long.compare(this.startms, __o.startms);
		if (rv != 0)
			return rv;
		
		rv = Long.compare(this.durationns, __o.durationns);
		if (rv != 0)
			return rv;
		
		rv = this.name.compareTo(__o.name);
		if (rv != 0)
			return rv;
		
		return this.type.compareTo(__o.type);
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
	 * {@inheritDoc}
	 * @since 2018/02/13
	 */
	@Override
	public final boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		if (!(__o instanceof PerformanceEntry))
			return false;
		
		return 0 == this.compareTo((PerformanceEntry)__o);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/02/13
	 */
	@Override
	public final int hashCode()
	{
		return this.name.hashCode() ^
			this.type.hashCode() ^
			Long.hashCode(this.startns) ^
			Long.hashCode(this.startms) ^
			Long.hashCode(this.durationns);
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

