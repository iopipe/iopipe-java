package com.iopipe.plugin.trace;

import com.iopipe.IOpipeConstants;
import com.iopipe.PerformanceEntry;

/**
 * This represents a mark which indicates a single point in time during
 * execution.
 *
 * @since 2018/01/19
 */
public final class TraceMark
	implements PerformanceEntry
{
	/** The name of this trace. */
	protected final String name;
	
	/** The nano time this mark was created, offset by the load time. */
	protected final long nanotime =
		System.nanoTime() - IOpipeConstants.LOAD_TIME_NANOS;
	
	/** The system time this mark was created. */
	protected final long timemillis =
		System.currentTimeMillis();
	
	/**
	 * Initializes the trace mark.
	 *
	 * @param __name The name of the mark.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/19
	 */
	public TraceMark(String __name)
		throws NullPointerException
	{
		if (__name == null)
			throw new NullPointerException();
		
		this.name = __name;
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
		
		if (!(__o instanceof TraceMark))
			return false;
		
		TraceMark o = (TraceMark)__o;
		return this.name.equals(o.name) &&
			this.nanotime == o.nanotime &&
			this.timemillis == o.timemillis;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/20
	 */
	@Override
	public int hashCode()
	{
		return this.name.hashCode() ^
			Long.hashCode(this.nanotime) ^
			Long.hashCode(this.timemillis);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/19
	 */
	@Override
	public String name()
	{
		return this.name;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/19
	 */
	@Override
	public long startNanoTime()
	{
		return this.nanotime;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/19
	 */
	@Override
	public long startTimeMillis()
	{
		return this.timemillis;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/19
	 */
	@Override
	public String type()
	{
		return "mark";
	}
}

