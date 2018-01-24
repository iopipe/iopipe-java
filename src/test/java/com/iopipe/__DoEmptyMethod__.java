package com.iopipe;

import com.iopipe.http.RemoteRequest;
import javax.json.JsonObject;

/**
 * Tests an empty method which does nothing.
 *
 * @since 2018/01/23
 */
class __DoEmptyMethod__
	extends Single
{
	/**
	 * Constructs the test.
	 *
	 * @since 2018/01/23
	 */
	__DoEmptyMethod__(Engine __e)
	{
		super(__e, "emptymethod");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/23
	 */
	@Override
	public void mockedRequest(RemoteRequest __r)
	{
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/23
	 */
	@Override
	public void run(IOpipeExecution __e)
		throws Throwable
	{
	}
}


