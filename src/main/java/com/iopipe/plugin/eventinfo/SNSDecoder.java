package com.iopipe.plugin.eventinfo;

import com.amazonaws.services.lambda.runtime.events.SNSEvent;

/**
 * This class implements the decoder for SNS events.
 *
 * @since 2018/04/23
 */
public final class SNSDecoder
	implements EventInfoDecoder
{
	/**
	 * {@inheritDoc}
	 * @since 2018/04/23
	 */
	@Override
	public final Class<?> decodes()
	{
		return SNSEvent.class;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/04/23
	 */
	@Override
	public final String eventType()
	{
		return "sns";
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

