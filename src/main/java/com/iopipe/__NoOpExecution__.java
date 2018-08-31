package com.iopipe;

import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.iopipe.plugin.IOpipePluginExecution;
import com.iopipe.plugin.NoSuchPluginException;

/**
 * This is an execution which does nothing.
 *
 * @since 2018/08/27
 */
final class __NoOpExecution__
	extends IOpipeExecution
{
	/** The starting time in milliseconds. */
	protected final long starttimemillis =
		System.currentTimeMillis();
	
	/**
	 * Initializes the noop execution.
	 *
	 * @param __cold Has this been coldstarted?
	 * @since 2018/08/27
	 */
	__NoOpExecution__(boolean __cold)
	{
		super(__cold);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/08/27
	 */
	@Override
	public final IOpipeConfiguration config()
	{
		return IOpipeConfiguration.DISABLED_CONFIG;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/08/27
	 */
	@Override
	public final Context context()
	{
		return new __Context__();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/08/27
	 */
	@Override
	public final Object input()
	{
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/08/27
	 */
	@Override
	public final <C extends IOpipePluginExecution> C plugin(Class<C> __cl)
		throws ClassCastException, NoSuchPluginException, NullPointerException
	{
		if (__cl == null)
			throw new NullPointerException();
		
		throw new NoSuchPluginException("IOpipe is not currently active.");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/08/27
	 */
	@Override
	public final IOpipeService service()
	{
		return IOpipeService.instance();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/08/28
	 */
	@Override
	public final long startTimestamp()
	{
		return this.starttimemillis;
	}
	
	/**
	 * A fake context just so that no null objects are used.
	 *
	 * @since 2018/08/27
	 */
	static final class __Context__
		implements Context
	{
		/**
		 * {@inheritDoc}
		 * @since 2018/08/27
		 */
		@Override
		public final String getAwsRequestId()
		{
			return "00000000-0000-0000-0000-000000000000";
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2018/08/27
		 */
		@Override
		public final ClientContext getClientContext()
		{
			return null;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2018/08/27
		 */
		@Override
		public final String getFunctionName()
		{
			return "unknownfunctionname";
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2018/08/27
		 */
		@Override
		public final String getFunctionVersion()
		{
			return "unknownversion";
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2018/08/27
		 */
		@Override
		public final CognitoIdentity getIdentity()
		{
			return null;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2018/08/27
		 */
		@Override
		public final String getInvokedFunctionArn()
		{
			return "unknownfunctionarn";
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2018/08/27
		 */
		@Override
		public final LambdaLogger getLogger()
		{
			return null;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2018/08/27
		 */
		@Override
		public final String getLogGroupName()
		{
			return "unknownloggroup";
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2018/08/27
		 */
		@Override
		public final String getLogStreamName()
		{
			return "unknownlogstream";
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2018/08/27
		 */
		@Override
		public final int getMemoryLimitInMB()
		{
			return 1024;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2018/08/27
		 */
		@Override
		public final int getRemainingTimeInMillis()
		{
			// 8 hours
			return 28800000;
		}
	}
}

