package com.iopipe.plugin.eventinfo;

import com.amazonaws.services.lambda.runtime.events.CloudFrontEvent;

/**
 * This class implements the decoder for cloud front events.
 *
 * @since 2018/04/23
 */
public final class CloudFrontDecoder
	implements EventInfoDecoder
{
	/**
	 * {@inheritDoc}
	 * @since 2018/04/23
	 */
	@Override
	public final Class<?> decodes()
	{
		return CloudFrontEvent.class;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/04/23
	 */
	@Override
	public final String eventType()
	{
		return "cloudFront";
	}
}

