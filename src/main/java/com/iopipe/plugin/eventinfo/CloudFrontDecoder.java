package com.iopipe.plugin.eventinfo;

import com.amazonaws.services.lambda.runtime.events.CloudFrontEvent;
import java.util.List;
import java.util.Map;

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
	 * @since 2018/05/02
	 */
	@Override
	public void accept(ValueAcceptor __a, Object __v)
		throws NullPointerException
	{
		if (__a == null || __v == null)
			throw new NullPointerException();
		
		CloudFrontEvent v = (CloudFrontEvent)__v;
		
		// There must be at least one record
		List<CloudFrontEvent.Record> records = v.getRecords();
		if (records == null || records.size() <= 0)
			return;
		CloudFrontEvent.Record record = records.get(0);
		
		// All keys use this
		CloudFrontEvent.CF cf = record.getCf();
		if (cf == null)
			return;
		
		// Record single event
		CloudFrontEvent.Config config = cf.getConfig();
		if (config != null)
			__a.accept("Records[0].cf.config.distributionId",
				config.getDistributionId());
		
		// Remaining requests
		CloudFrontEvent.Request request = cf.getRequest();
		if (request != null)
		{
			__a.accept("Records[0].cf.request.clientIp",
				request.getClientIp());
			
			Map<String, List<CloudFrontEvent.Header>> headers =
				request.getHeaders();
			if (headers != null)
			{
				List<CloudFrontEvent.Header> hosts = headers.get("host");
				if (hosts != null && hosts.size() >= 1)
					__a.accept("Records[0].cf.request.headers.host[0].value",
						hosts.get(0).getValue());
				
				List<CloudFrontEvent.Header> useragents =
					headers.get("user-agent");
				if (useragents != null && useragents.size() >= 1)
					__a.accept("Records[0].cf.request.headers." +
						"[\"user-agent\"][0].value",
						useragents.get(0).getValue());
						
			}
			
			__a.accept("Records[0].cf.request.method",
				request.getMethod());
			__a.accept("Records[0].cf.request.uri",
				request.getUri());
		}
	}
	
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

