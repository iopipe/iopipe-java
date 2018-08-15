package com.iopipe.plugin.trace;

import com.iopipe.IOpipeConstants;
import com.iopipe.IOpipeMeasurement;
import com.iopipe.PerformanceEntry;
import java.util.concurrent.atomic.AtomicBoolean;

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
	implements AutoCloseable
{
	/** Is this measurement to be enabled? */
	protected final boolean enabled;
	
	/** The measurement to record to. */
	protected final IOpipeMeasurement measurement;
	
	/** The name of this trace. */
	protected final String name;
	
	/** The start time of this measurement, used to count duration. */
	protected final long startns;
	
	/** Has this been closed? */
	private final AtomicBoolean _closed =
		new AtomicBoolean();
	
	/**
	 * Initializes the measurement tracking.
	 *
	 * @param __enabled Is this enabled?
	 * @param __m Where measurements are to be placed.
	 * @param __name The name of this trace.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/19
	 */
	public TraceMeasurement(boolean __enabled, IOpipeMeasurement __m,
		String __name)
		throws NullPointerException
	{
		if (__m == null || __name == null)
			throw new NullPointerException();
		
		this.enabled = __enabled;
		this.measurement = __m;
		this.name = __name;
		
		// Initialize start time
		long startns = System.nanoTime();
		this.startns = startns;
		
		// Create initial start mark if this is enabled
		if (__enabled)
			__m.addPerformanceEntry(new PerformanceEntry("start:" + __name,
				"mark", startns, System.currentTimeMillis(), 0));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/19
	 */
	@Override
	public void close()
	{
		if (this.enabled)
			if (this._closed.compareAndSet(false, true))
			{
				
				// There are two end marks, one for the end mark and the
				// actual duration but they end at the same time
				long endns = System.nanoTime(),
					endms = System.currentTimeMillis();
				
				IOpipeMeasurement measurement = this.measurement;
				String name = this.name;
				
				measurement.addPerformanceEntry(new PerformanceEntry(
					"end:" + name, "mark", endns, endms, 0));
				measurement.addPerformanceEntry(new PerformanceEntry(
					"measure:" + name, "measure", endns, endms,
					endns - this.startns));
			}
	}
}

