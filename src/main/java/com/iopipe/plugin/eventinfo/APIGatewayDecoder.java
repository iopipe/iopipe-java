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
	
	/**
	 * {@inheritDoc}
	 * @since 2018/04/29
	 */
	@Override
	public final Rule[] rules()
	{
		return Rule.rules(
			new Rule<APIGatewayProxyRequestEvent>("httpMethod",
				APIGatewayProxyRequestEvent::getHttpMethod),
			new Rule<APIGatewayProxyRequestEvent>("path",
				APIGatewayProxyRequestEvent::getPath),
			new Rule<APIGatewayProxyRequestEvent>("requestContext.accountId",
				(__o) -> __o.getRequestContext().getAccountId()),
			new Rule<APIGatewayProxyRequestEvent>("requestContext.httpMethod",
				(__o) -> __o.getRequestContext().getHttpMethod()),
			new Rule<APIGatewayProxyRequestEvent>("requestContext.identity.userAgent",
				(__o) -> __o.getRequestContext().getIdentity().getUserAgent()),
			new Rule<APIGatewayProxyRequestEvent>("requestContext.requestId",
				(__o) -> __o.getRequestContext().getRequestId()),
			new Rule<APIGatewayProxyRequestEvent>("requestContext.resourcePath",
				(__o) -> __o.getRequestContext().getResourcePath()),
			new Rule<APIGatewayProxyRequestEvent>("requestContext.stage",
				(__o) -> __o.getRequestContext().getStage()),
			new Rule<APIGatewayProxyRequestEvent>("resource",
				APIGatewayProxyRequestEvent::getResource));
	}
}

