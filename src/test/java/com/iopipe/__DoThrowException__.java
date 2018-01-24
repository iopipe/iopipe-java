package com.iopipe;

import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import javax.json.JsonObject;

/**
 * Tests throwing of an exception.
 *
 * @since 2018/01/23
 */
class __DoThrowException__
	extends Single
{
	/** Got mocked request? */
	protected final BooleanValue errorwassent =
		new BooleanValue("errorwassent");
		
	/** Got a result from the server okay? */
	protected final BooleanValue remoterecvokay =
		new BooleanValue("remoterecvokay");
	
	/**
	 * Constructs the test.
	 *
	 * @since 2018/01/23
	 */
	__DoThrowException__(Engine __e)
	{
		super(__e, "thrownexception");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/23
	 */
	@Override
	public void end()
	{
		super.assertTrue(this.remoterecvokay);
		super.assertTrue(this.errorwassent);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/23
	 */
	@Override
	public void remoteRequest(RemoteRequest __r)
	{
		if (null != __RequestUtils__.hasError(__r))
			this.errorwassent.set(true);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/23
	 */
	@Override
	public void remoteResult(RemoteResult __r)
	{
		if (__ResultUtils__.isOkay(__r))
			this.remoterecvokay.set(true);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/23
	 */
	@Override
	public void run(IOpipeExecution __e)
		throws Throwable
	{
		throw new MockException("Mock Exception");
	}
}


