package com.iopipe.plugin.eventinfo;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import java.util.List;
import java.util.Map;

/**
 * This decoded SQS events.
 *
 * @since 2018/08/02
 */
public final class SQSDecoder
	implements EventInfoDecoder
{
	/**
	 * {@inheritDoc}
	 * @since 2018/08/02
	 */
	@Override
	public final void accept(ValueAcceptor __a, Object __v)
		throws NullPointerException
	{
		if (__a == null || __v == null)
			throw new NullPointerException();
		
		SQSEvent e = (SQSEvent)__v;
		
		// No records used
		List<SQSEvent.SQSMessage> records = e.getRecords();
		if (records == null || records.isEmpty())
			return;
		
		SQSEvent.SQSMessage record = records.get(0);
		
		__a.accept("awsRegion",
			record.getAwsRegion());
		__a.accept("eventSourceARN",
			record.getEventSourceArn());
		__a.accept("md5OfBody",
			record.getMd5OfBody());
		__a.accept("messageId",
			record.getMessageId());
		__a.accept("receiptHandle",
			record.getReceiptHandle());
		
		Map<String, String> attr = record.getAttributes();
		if (attr != null)
		{
			__a.accept("attributes.ApproximateFirstReceiveTimestamp",
				attr.get("ApproximateFirstReceiveTimestamp"));
			__a.accept("attributes.ApproximateReceiveCount",
				attr.get("ApproximateReceiveCount"));
			__a.accept("attributes.SenderId",
				attr.get("SenderId"));
			__a.accept("attributes.SentTimestamp",
				attr.get("SentTimestamp"));
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/08/02
	 */
	@Override
	public final String[] decodes()
	{
		return new String[]
			{
				"com.amazonaws.services.lambda.runtime.events.SQSEvent",
			};
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/08/02
	 */
	@Override
	public final String eventType()
	{
		return "sqs";
	}
}

