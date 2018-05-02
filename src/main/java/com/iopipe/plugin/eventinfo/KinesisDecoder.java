package com.iopipe.plugin.eventinfo;

import com.amazonaws.services.lambda.runtime.events.KinesisEvent;

/**
 * This class implements the decoder for Kinesis events.
 *
 * @since 2018/04/23
 */
public final class KinesisDecoder
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
		
		throw new Error("TODO");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/04/23
	 */
	@Override
	public final Class<?> decodes()
	{
		return KinesisEvent.class;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/04/23
	 */
	@Override
	public final String eventType()
	{
		return "kinesis";
	}
}

