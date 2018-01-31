package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;

/**
 * Tests that an invalid token does not work.
 *
 * @since 2018/01/26
 */
class __DoInvalidToken__
	extends Single
{
	protected final BooleanValue executedmethod =
		new BooleanValue("executedmethod");
	
	/** Successful execution of method even though it will fail. */
	protected final BooleanValue noerror =
		new BooleanValue("noerror");
	
	/** The server did not allow us to push an event. */
	protected final BooleanValue remoterecvfailed =
		new BooleanValue("remoterecvfailed");
	
	/**
	 * Initializes the test.
	 *
	 * @param __e The owning engine.
	 * @since 2018/01/26
	 */
	public __DoInvalidToken__(Engine __e)
	{
		super(__e, "invalidtoken");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/26
	 */
	@Override
	public void end()
	{
		super.assertTrue(this.executedmethod);
		super.assertTrue(this.noerror);
		super.assertTrue(this.remoterecvfailed);
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
		
		// Purposefully use an invalid token
		__cb.setProjectToken(MockConnection.INVALID_TOKEN);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/26
	 */
	@Override
	public void remoteRequest(RemoteRequest __r)
	{
		// Even though the request will fail, it still must be sent without
		// error
		if (null == __Utils__.hasError(__r))
			this.noerror.set(true);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/26
	 */
	@Override
	public void remoteResult(RemoteResult __r)
	{
		if (!__Utils__.isResultOkay(__r))
			this.remoterecvfailed.set(true);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/26
	 */
	@Override
	public void run(IOpipeExecution __e)
		throws Throwable
	{
		this.executedmethod.set(true);
	}
}

