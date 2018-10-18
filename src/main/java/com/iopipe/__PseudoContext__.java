package com.iopipe;

import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import java.util.Objects;

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
		// This is only valid if the context is called from the mobile SDK
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/10/17
	 */
	@Override
	public final String getFunctionName()
	{
		return __env("AWS_LAMBDA_FUNCTION_NAME", "null");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/10/17
	 */
	@Override
	public final String getFunctionVersion()
	{
		return __env("AWS_LAMBDA_FUNCTION_VERSION", "null");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/10/17
	 */
	@Override
	public final CognitoIdentity getIdentity()
	{
		// This is only valid if the context is called from the mobile SDK
		return null;
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
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/10/17
	 */
	@Override
	public final String getLogGroupName()
	{
		return __env("AWS_LAMBDA_LOG_GROUP_NAME", "null");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/10/17
	 */
	@Override
	public final String getLogStreamName()
	{
		return __env("AWS_LAMBDA_LOG_STREAM_NAME", "null");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/10/17
	 */
	@Override
	public final int getMemoryLimitInMB()
	{
		// Current VM max memory used, if no maximum memory is defined then
		// just report the memory the VM is using itself
		long runmax = Runtime.getRuntime().maxMemory();
		if (runmax <= 0 || runmax == Long.MAX_VALUE)
			runmax = Runtime.getRuntime().totalMemory();
		
		// Convert this to MiB
		int vmmax = (int)Math.min(Integer.MAX_VALUE, runmax / 1048576);
		
		// The environment variable could be an invalid integer
		try
		{
			return Integer.parseInt(__env("AWS_LAMBDA_FUNCTION_MEMORY_SIZE",
				Integer.toString(vmmax)));
		}
		catch (NumberFormatException e)
		{
			return vmmax;
		}
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
	
	/**
	 * Gets the given environment variable or returns {@code __v} if it does
	 * not exist.
	 *
	 * @param __k The key to get.
	 * @param __v The default value to use.
	 * @return The value for the given key or the default value.
	 * @since 2018/10/18
	 */
	private static final String __env(String __k, String __v)
	{
		if (__k == null)
			return __v;
		
		try
		{
			String rv = System.getenv(__k);
			if (rv != null)
				return rv;
			return __v;
		}
		catch (SecurityException e)
		{
			return __v;
		}
	}
}

