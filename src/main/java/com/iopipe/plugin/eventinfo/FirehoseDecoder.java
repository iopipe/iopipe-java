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
	 * @since 2018/05/02
	 */
	@Override
	public void accept(ValueAcceptor __a, Object __v)
		throws NullPointerException
	{
		if (__a == null || __v == null)
			throw new NullPointerException();
		
		KinesisFirehoseEvent v = (KinesisFirehoseEvent)__v;
		
		__a.accept("deliveryStreamArn",
			v.getDeliveryStreamArn());
		__a.accept("region",
			v.getRegion());
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/04/23
	 */
	@Override
	public final String[] decodes()
	{
		return new String[]
			{
				"com.amazonaws.services.lambda.runtime.events.KinesisFirehoseEvent",
			};
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
}

