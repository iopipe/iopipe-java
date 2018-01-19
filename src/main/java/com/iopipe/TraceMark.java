package com.iopipe;

/**
 * This represents a mark which indicates a single point in time during
 * execution.
 *
 * @since 2018/01/19
 */
public final class TraceMark
	implements TracePerformanceEntry
{
	/** The name of this trace. */
	protected final String name;
	
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
	 * @since 2018/01/19
	 */
	@Override
	public String name()
	{
		throw new Error("TODO");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/19
	 */
	@Override
	public long startNanoTime()
	{
		throw new Error("TODO");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/19
	 */
	@Override
	public long startTimeMillis()
	{
		throw new Error("TODO");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/19
	 */
	@Override
	public String type()
	{
		throw new Error("TODO");
	}
}

