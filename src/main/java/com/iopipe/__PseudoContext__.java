package com.iopipe;

import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

/**
 * Pseudo AWS context if one was not specified, this is derived from
 * environment variables and such.
 *
 * @since 2018/10/17
 */
final class __PseudoContext__
	implements Context
{
	/**
	 * Initializes the pseudo context.
	 *
	 * @since 2018/10/17
	 */
	public __PseudoContext__()
	{
		throw new Error("TODO");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/10/17
	 */
	@Override
	public final String getAwsRequestId()
	{
		throw new Error("TODO");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/10/17
	 */
	@Override
	public final ClientContext getClientContext()
	{
		throw new Error("TODO");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/10/17
	 */
	@Override
	public final String getFunctionName()
	{
		throw new Error("TODO");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/10/17
	 */
	@Override
	public final String getFunctionVersion()
	{
		throw new Error("TODO");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/10/17
	 */
	@Override
	public final CognitoIdentity getIdentity()
	{
		throw new Error("TODO");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/10/17
	 */
	@Override
	public final String getInvokedFunctionArn()
	{
		throw new Error("TODO");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/10/17
	 */
	@Override
	public final LambdaLogger getLogger()
	{
		throw new Error("TODO");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/10/17
	 */
	@Override
	public final String getLogGroupName()
	{
		throw new Error("TODO");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/10/17
	 */
	@Override
	public final String getLogStreamName()
	{
		throw new Error("TODO");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/10/17
	 */
	@Override
	public final int getMemoryLimitInMB()
	{
		throw new Error("TODO");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/10/17
	 */
	@Override
	public final int getRemainingTimeInMillis()
	{
		throw new Error("TODO");
	}
}

