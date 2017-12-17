package com.iopipe.mock;

import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

/**
 * This provides a fake context for testing which provides a basic information
 * set for execution.
 *
 * @since 2017/12/13
 */
public final class MockContext
	implements Context
{
	/** The duration of contexts in milliseconds. */
	public static final int CONTEXT_DURATION_MS =
		3_000;
	
	/** The duration of contexts in nanoseconds. */
	public static final long CONTEXT_DURATION_NS =
		CONTEXT_DURATION_MS * 1_000_000L;
	
	/** The name of the function being executed. */
	protected final String functionname;
	
	/** The start time of this context. */
	protected final long starttime =
		System.nanoTime();
	
	/**
	 * Initializes the context with the given parameters.
	 *
	 * @param __funcname The name of the function being invoked.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/13
	 */
	public MockContext(String __funcname)
		throws NullPointerException
	{
		if (__funcname == null)
			throw new NullPointerException();
		
		this.functionname = __funcname;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/13
	 */
	@Override
	public final String getAwsRequestId()
	{
		return "mockawsrequestid";
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/13
	 */
	@Override
	public final ClientContext getClientContext()
	{
		// This is only valid if the context is called from the mobile SDK
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/13
	 */
	@Override
	public final String getFunctionName()
	{
		return this.functionname;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/13
	 */
	@Override
	public final String getFunctionVersion()
	{
		return "1.0";
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/13
	 */
	@Override
	public final CognitoIdentity getIdentity()
	{
		// This is only valid if the context is called from the mobile SDK
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/13
	 */
	@Override
	public final String getInvokedFunctionArn()
	{
		return "mockinvokedfunctionarn";
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/13
	 */
	@Override
	public final LambdaLogger getLogger()
	{
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/13
	 */
	@Override
	public final String getLogGroupName()
	{
		return "mockloggroupname";
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/13
	 */
	@Override
	public final String getLogStreamName()
	{
		return "mocklogstreamname";
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/13
	 */
	@Override
	public final int getMemoryLimitInMB()
	{
		return Integer.MAX_VALUE;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/13
	 */
	@Override
	public final int getRemainingTimeInMillis()
	{
		long left = (CONTEXT_DURATION_NS -
			(System.nanoTime() - this.starttime)) / 1_000_000L;
		if (left < 0)
			return 0;
		else if (left >= Integer.MAX_VALUE)
			return Integer.MAX_VALUE;
		return (int)left;
	}
}

