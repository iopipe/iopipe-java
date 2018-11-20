package com.iopipe.plugin.eventinfo;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.event.S3EventNotification;
import java.util.List;
import java.util.Objects;

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
	 * @since 2018/05/02
	 */
	@Override
	public void accept(ValueAcceptor __a, Object __v)
		throws NullPointerException
	{
		if (__a == null || __v == null)
			throw new NullPointerException();
		
		S3EventNotification v = (S3EventNotification)__v;
		
		// Is there going to be any at all?
		List<S3EventNotification.S3EventNotificationRecord> records =
			v.getRecords();
		if (records == null || records.size() <= 0)
			return;
		S3EventNotification.S3EventNotificationRecord record = records.get(0);
		
		__a.accept("Records[0].awsRegion",
			record.getAwsRegion());
		__a.accept("Records[0].eventName",
			record.getEventName());
		__a.accept("Records[0].eventTime",
			Objects.toString(record.getEventTime(), null));
		
		S3EventNotification.RequestParametersEntity rparms =
			record.getRequestParameters();
		if (rparms != null)
		{
			__a.accept("Records[0].requestParameters.sourceIPAddress",
				rparms.getSourceIPAddress());
		}
		
		S3EventNotification.ResponseElementsEntity rese =
			record.getResponseElements();
		if (rese != null)
		{
			__a.accept("Records[0].responseElements[\"x-amz-id-2\"]",
				rese.getxAmzId2());
			__a.accept("Records[0].responseElements[\"x-amz-request-id\"]",
				rese.getxAmzRequestId());
		}
		
		S3EventNotification.S3Entity sthree = record.getS3();
		if (sthree != null)
		{
			S3EventNotification.S3BucketEntity bucket = sthree.getBucket();
			if (bucket != null)
			{
				__a.accept("Records[0].s3.bucket.arn",
					bucket.getArn());
				__a.accept("Records[0].s3.bucket.name",
					bucket.getName());
			}
			
			S3EventNotification.S3ObjectEntity object = sthree.getObject();
			if (object != null)
			{
				__a.accept("Records[0].s3.object.key",
					object.getKey());
				__a.accept("Records[0].s3.object.sequencer",
					object.getSequencer());
				__a.accept("Records[0].s3.object.size",
					object.getSize());
			}
		}
		
		S3EventNotification.UserIdentityEntity uie = record.getUserIdentity();
		if (uie != null)
		{
			__a.accept("Records[0].userIdentity.principalId",
				uie.getPrincipalId());
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
				"com.amazonaws.services.lambda.runtime.events.S3Event",
				"com.amazonaws.services.s3.event.S3EventNotification",
			};
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
}

