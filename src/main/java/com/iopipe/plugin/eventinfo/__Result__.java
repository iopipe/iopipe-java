package com.iopipe.plugin.eventinfo;

import com.iopipe.CustomMetric;

/**
 * When event info has finished decoding something, this contains all of the
 * needed information.
 *
 * @since 2018/07/17
 */
final class __Result__
{
	/** The decoder information, needed for event info. */
	public final EventInfoDecoder _decoder;
	
	/** Custom metrics. */
	public final CustomMetric[] _metrics;
	
	/**
	 * Initializes the result.
	 *
	 * @param __d The decoder used.
	 * @param __m The metrics used, a copy is not made.
	 * @since 2018/07/17
	 */
	__Result__(EventInfoDecoder __d, CustomMetric[] __m)
	{
		this._decoder = __d;
		this._metrics = __m;
	}
}
 
