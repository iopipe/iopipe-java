package com.iopipe.plugin.eventinfo;

import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import java.util.List;
import java.util.Objects;

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
	 * @since 2018/05/02
	 */
	@Override
	public void accept(ValueAcceptor __a, Object __v)
		throws NullPointerException
	{
		if (__a == null || __v == null)
			throw new NullPointerException();
		
		SNSEvent v = (SNSEvent)__v;
		
		// Only record the first record
		List<SNSEvent.SNSRecord> records = v.getRecords();
		if (records == null || records.size() <= 0)
			return;
		SNSEvent.SNSRecord record = records.get(0);
		
		__a.accept("Records[0].EventSubscriptionArn",
			record.getEventSubscriptionArn());
		
		SNSEvent.SNS sns = record.getSNS();
		if (sns != null)
		{
			__a.accept("Records[0].Sns.MessageId",
				sns.getMessageId());
			__a.accept("Records[0].Sns.Signature",
				sns.getSignature());
			__a.accept("Records[0].Sns.SignatureVersion",
				sns.getSignatureVersion());
			__a.accept("Records[0].Sns.SigningCertUrl",
				sns.getSigningCertUrl());
			__a.accept("Records[0].Sns.UnsubscribeUrl",
				sns.getUnsubscribeUrl());
			__a.accept("Records[0].Sns.Subject",
				sns.getSubject());
			__a.accept("Records[0].Sns.Timestamp",
				Objects.toString(sns.getTimestamp(), null));
			__a.accept("Records[0].Sns.TopicArn",
				sns.getTopicArn());
			__a.accept("Records[0].Sns.Type",
				sns.getType());
		}
	}
	
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
}

