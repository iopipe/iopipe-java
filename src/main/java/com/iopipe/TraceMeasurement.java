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
	
	/** The nano time this measurement was started. */
	protected final long startnanotime =
		System.nanoTime();
	
	/** The system time this measurement was started. */
	protected final long starttimemillis =
		System.currentTimeMillis();
	
	/** Has this been closed? */
	private volatile boolean _closed;
	
	/** The ending nano time when this was closed. */
	private volatile long _endnanotime =
		System.nanoTime();
	
	/** The ending system time when this was closed. */
	private volatile long _endtimemillis =
		System.currentTimeMillis();
	
	/**
	 * Initializes the trace measurement.
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
		if (!this._closed)
		{
			this._closed = true;
			
			this._endnanotime = System.nanoTime();
			this._endtimemillis = System.currentTimeMillis();
			
			this.measurement.addPerformanceEntry(this);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/19
	 */
	@Override
	public long endNanoTime()
	{
		return this._endnanotime;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/19
	 */
	@Override
	public long endTimeMillis()
	{
		return this._endtimemillis;
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
		
		if (!(__o instanceof TraceMeasurement))
			return false;
		
		TraceMeasurement o = (TraceMeasurement)__o;
		return this.name.equals(o.name) &&
			this.startnanotime == o.startnanotime &&
			this.starttimemillis == o.starttimemillis &&
			this._endnanotime == o._endnanotime &&
			this._endtimemillis == o._endtimemillis;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/20
	 */
	@Override
	public int hashCode()
	{
		return this.name.hashCode() ^
			Long.hashCode(this.startnanotime) ^
			Long.hashCode(this.starttimemillis) ^
			Long.hashCode(this._endnanotime) ^
			Long.hashCode(this._endtimemillis);
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
		return this.startnanotime;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/19
	 */
	@Override
	public long startTimeMillis()
	{
		return this.starttimemillis;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/19
	 */
	@Override
	public String type()
	{
		return "measurement";
	}
}
