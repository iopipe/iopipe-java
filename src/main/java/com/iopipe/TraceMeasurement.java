package com.iopipe;

/**
 * This class measures a duration of time and is intended to be used with
 * try-with-resources to measure how long a given block of code takes to
 * execute.
 *
 * The measurement may only be closed once.
 *
 * @since 2018/01/19
 */
public final class TraceMeasurement
	implements AutoCloseable, TracePerformanceEntry
{
	/** The measurement where the measurement will be recorded into. */
	protected final IOpipeMeasurement measurement;
	
	/** The name of this trace. */
	protected final String name;
	
	/**
	 * Initializes the trace
	 *
	 * @param __m Where the measurement will be placed.
	 * @param __name The name of this trace.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/19
	 */
	public TraceMeasurement(IOpipeMeasurement __m, String __name)
		throws NullPointerException
	{
		if (__m == null || __name == null)
			throw new NullPointerException();
		
		this.measurement = __m;
		this.name = __name;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/19
	 */
	@Override
	public void close()
	{
		throw new Error("TODO");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/19
	 */
	@Override
	public long endNanoTime()
	{
		throw new Error("TODO");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/19
	 */
	@Override
	public long endTimeMillis()
	{
		throw new Error("TODO");
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

