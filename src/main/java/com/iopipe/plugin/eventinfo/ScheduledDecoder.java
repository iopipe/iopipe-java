package com.iopipe.plugin.eventinfo;

import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import java.util.List;
import java.util.Objects;

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
	 * @since 2018/05/02
	 */
	@Override
	public void accept(ValueAcceptor __a, Object __v)
		throws NullPointerException
	{
		if (__a == null || __v == null)
			throw new NullPointerException();
		
		ScheduledEvent v = (ScheduledEvent)__v;
		
		__a.accept("account",
			v.getAccount());
		__a.accept("id",
			v.getId());
		__a.accept("region",
			v.getRegion());
		
		List<String> resources = v.getResources();
		if (resources != null && resources.size() >= 1)
		{
			__a.accept("resources[0]",
				resources.get(0));
		}
		
		__a.accept("time",
			Objects.toString(v.getTime(), null));
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
				"com.amazonaws.services.lambda.runtime.events.ScheduledEvent",
			};
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

