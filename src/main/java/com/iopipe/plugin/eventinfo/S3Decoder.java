package com.iopipe.plugin.eventinfo;

import com.amazonaws.services.lambda.runtime.events.S3Event;

/**
 * This class implements the decoder for S3 events.
 *
 * @since 2018/04/23
 */
public final class S3Decoder
	implements EventInfoDecoder
{
	/**
	 * {@inheritDoc}
	 * @since 2018/04/23
	 */
	@Override
	public final Class<?> decodes()
	{
		return S3Event.class;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/04/23
	 */
	@Override
	public final String eventType()
	{
		return "s3";
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

