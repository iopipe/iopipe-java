package com.iopipe.plugin.trace;

import com.iopipe.PerformanceEntry;

/**
 * This is used to measure the time between two marks.
 *
 * @since 2018/01/20
 */
public final class TraceMeasurementMark
	implements PerformanceEntry
{
	/** The name of this trace. */
	protected final String name;
	
	/** The first mark. */
	protected final TraceMark a;
	
	/** The second mark. */
	protected final TraceMark b;
	
	/**
	 * Initializes the trace mark measurement.
	 *
	 * @param __name The name of this trace.
	 * @param __a The first mark.
	 * @param __b The second mark.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/20
	 */
	public TraceMeasurementMark(String __name, TraceMark __a, TraceMark __b)
		throws NullPointerException
	{
		if (__name == null || __a == null || __b == null)
			throw new NullPointerException();
		
		this.name = __name;
		if (__a.compareTo(__b) < 0)
		{
			this.a = __a;
			this.b = __b;
		}
		else
		{
			this.a = __b;
			this.b = __a;
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/20
	 */
	@Override
	public long endNanoTime()
	{
		return this.b.endNanoTime();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/20
	 */
	@Override
	public long endTimeMillis()
	{
		return this.b.endTimeMillis();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/20
	 */
	@Override
	public boolean equals(Object __o)
	{
		if (__o == this)
			return true;
		
		if (!(__o instanceof TraceMeasurementMark))
			return false;
		
		TraceMeasurementMark o = (TraceMeasurementMark)__o;
		return this.name.equals(o.name) &&
			this.a.equals(o.a) &&
			this.b.equals(o.b);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/20
	 */
	@Override
	public int hashCode()
	{
		return this.name.hashCode() ^
			this.a.hashCode() ^
			this.b.hashCode();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/20
	 */
	@Override
	public String name()
	{
		return this.name;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/20
	 */
	@Override
	public long startNanoTime()
	{
		return this.a.startNanoTime();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/20
	 */
	@Override
	public long startTimeMillis()
	{
		return this.a.startTimeMillis();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/20
	 */
	@Override
	public String type()
	{
		return "measurement";
	}
}

