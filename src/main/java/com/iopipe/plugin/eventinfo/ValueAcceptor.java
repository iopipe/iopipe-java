package com.iopipe.plugin.eventinfo;

import com.iopipe.CustomMetric;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to accept values which should be stored in custom
 * metrics in the generated report.
 *
 * @since 2018/05/02
 */
public final class ValueAcceptor
{
	/** The event type used. */
	protected final String eventtype;
	
	/** The key prefix. */
	protected final String prefix;
	
	/** The target list for recorded metrics. */
	private final List<CustomMetric> _metrics =
		new ArrayList<>();
	
	/**
	 * Initializes the value acceptor.
	 *
	 * @param __et The event type used.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/05/02
	 */
	public ValueAcceptor(String __et)
		throws NullPointerException
	{
		if (__et == null)
			throw new NullPointerException();
		
		this.eventtype = __et;
		
		// Generate prefix
		this.prefix = "@iopipe/event-info." + __et + ".";
		
		// Record initial metric
		this._metrics.add(
			new CustomMetric("@iopipe/event-info.eventType", __et));
	}
	
	/**
	 * Accepts the given key and value.
	 *
	 * @param __key The key.
	 * @param __val The value.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/05/02
	 */
	public final void accept(String __key, String __val)
		throws NullPointerException
	{
		if (__key == null || __val == null)
			throw new NullPointerException();
		
		this._metrics.add(new CustomMetric(this.prefix + __key, __val));
	}
	
	/**
	 * Accepts the given key and value.
	 *
	 * @param __key The key.
	 * @param __val The value.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/05/02
	 */
	public final void accept(String __key, long __val)
		throws NullPointerException
	{
		if (__key == null)
			throw new NullPointerException();
		
		this._metrics.add(new CustomMetric(this.prefix + __key, __val));
	}
	
	/**
	 * Returns all of the generated custom metrics.
	 *
	 * @return The array of created custom metrics.
	 * @since 2018/05/02
	 */
	public final CustomMetric[] get()
	{
		List<CustomMetric> metrics = this._metrics;
		return metrics.<CustomMetric>toArray(new CustomMetric[metrics.size()]);
	}
}
