package com.iopipe.plugin.eventinfo;

import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import java.util.List;

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
		
		KinesisEvent v = (KinesisEvent)__v;
		
		List<KinesisEvent.KinesisEventRecord> records = v.getRecords();
		if (records == null)
			return;
		
		int n = records.size();
		__a.accept("Records.length", n);
		
		// Report the first one
		if (n >= 1)
		{
			KinesisEvent.KinesisEventRecord record = records.get(0);
			
			__a.accept("Records[0].awsRegion",
				record.getAwsRegion());
			__a.accept("Records[0].eventSourceARN",
				record.getEventSourceARN());
		}
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
				"com.amazonaws.services.lambda.runtime.events.KinesisEvent",
			};
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

