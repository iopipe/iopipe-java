package com.iopipe.plugin.eventinfo;

import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;

/**
 * This class implements the decoder for Scheduled events.
 *
 * @since 2018/04/23
 */
public final class ScheduledDecoder
	implements EventInfoDecoder
{
	/**
	 * {@inheritDoc}
	 * @since 2018/04/23
	 */
	@Override
	public final Class<?> decodes()
	{
		return ScheduledEvent.class;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/04/23
	 */
	@Override
	public final String eventType()
	{
		return "scheduled";
	}
}

