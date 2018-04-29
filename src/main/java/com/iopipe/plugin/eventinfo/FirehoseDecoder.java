package com.iopipe.plugin.eventinfo;

import com.amazonaws.services.lambda.runtime.events.KinesisFirehoseEvent;

/**
 * This class implements the decoder for Kinesis Firehose events.
 *
 * @since 2018/04/23
 */
public final class FirehoseDecoder
	implements EventInfoDecoder
{
	/**
	 * {@inheritDoc}
	 * @since 2018/04/23
	 */
	@Override
	public final Class<?> decodes()
	{
		return KinesisFirehoseEvent.class;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/04/23
	 */
	@Override
	public final String eventType()
	{
		return "firehose";
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/04/29
	 */
	@Override
	public final Rule[] rules()
	{
		return new Rule[]{
			};
	}
}

