package com.iopipe.plugin.eventinfo;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

/**
 * This class implements the decoder for API Gateway events.
 *
 * @since 2018/04/22
 */
public final class APIGatewayDecoder
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
		
		APIGatewayProxyRequestEvent v = (APIGatewayProxyRequestEvent)__v;
		
		__a.accept("httpMethod",
			v.getHttpMethod());
		__a.accept("path",
			v.getPath());
		__a.accept("requestContext.accountId",
			v.getRequestContext().getAccountId());
		__a.accept("requestContext.httpMethod",
			v.getRequestContext().getHttpMethod());
		__a.accept("requestContext.identity.userAgent",
			v.getRequestContext().getIdentity().getUserAgent());
		__a.accept("requestContext.requestId",
			v.getRequestContext().getRequestId());
		__a.accept("requestContext.resourcePath",
			v.getRequestContext().getResourcePath());
		__a.accept("requestContext.stage",
			v.getRequestContext().getStage());
		__a.accept("resource",
			v.getResource());
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/04/22
	 */
	@Override
	public final Class<?> decodes()
	{
		return APIGatewayProxyRequestEvent.class;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/04/23
	 */
	@Override
	public final String eventType()
	{
		return "apiGateway";
	}
}

