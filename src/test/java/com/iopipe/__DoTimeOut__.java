package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;

/**
 * Tests that timeout occurs.
 *
 * @since 2018/01/26
 */
class __DoTimeOut__
	extends Single
{
	/** The extra amount of time to sleep to log timeout. */
	private static final int _EXTRA_SLEEP_TIME =
		500;
	
	/** Requests transmitted. */
	protected final IntegerValue xmitcount =
		new IntegerValue("xmitcount");
	
	/** Has an error value been sent? */
	protected final BooleanValue haserror =
		new BooleanValue("haserror");
		
	/** Got a result from the server okay? */
	protected final BooleanValue remoterecvokay =
		new BooleanValue("remoterecvokay");
	
	/**
	 * Initializes the test.
	 *
	 * @param __e The owning engine.
	 * @since 2018/01/26
	 */
	public __DoTimeOut__(Engine __e)
	{
		super(__e, "timeout");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/26
	 */
	@Override
	public void end()
	{
		super.assertTrue(this.remoterecvokay);
		
		super.assertTrue(this.haserror);
		super.assertEquals(1, this.xmitcount);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/26
	 */
	@Override
	public void modifyConfig(IOpipeConfigurationBuilder __cb)
		throws NullPointerException
	{
		if (__cb == null)
			throw new NullPointerException();
		
		// Do not wait a long time for the default timeout window to expire
		// just make it shorter
		__cb.setTimeOutWindow(1000);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/26
	 */
	@Override
	public void remoteRequest(WrappedRequest __r)
	{
		// Increment count
		int now = this.xmitcount.incrementAndGet();
		
		// Only consider the first request
		if (now == 1)
		{
			// It must have an error condition
			if (null != __Utils__.hasError(__r.request))
				this.haserror.set(true);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/26
	 */
	@Override
	public void remoteResult(WrappedResult __r)
	{
		if (__Utils__.isResultOkay(__r.result))
			this.remoterecvokay.set(true);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/26
	 */
	@Override
	public void run(IOpipeExecution __e)
		throws Throwable
	{
		// Wait until the remaining time to expire
		for (;;)
		{
			// Determine how long to sleep for
			Context c = __e.context();
			int sleepdur = c.getRemainingTimeInMillis();
			
			// Finished sleeping
			if (sleepdur <= 0)
				break;
			
			// Sleep
			try
			{
				Thread.sleep(sleepdur + _EXTRA_SLEEP_TIME);
			}
			catch (InterruptedException e)
			{
			}
		}
		
		// For for some more to make sure it does actually time out
		try
		{
			Thread.sleep(_EXTRA_SLEEP_TIME);
		}
		catch (InterruptedException e)
		{
		}
	}
}

