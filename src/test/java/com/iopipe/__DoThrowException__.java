package com.iopipe;

import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.json.JsonObject;

/**
 * Tests an empty method which does nothing.
 *
 * @since 2018/01/23
 */
class __DoThrowException__
	extends Single
{
	/** Got mocked request? */
	protected final AtomicBoolean errorwassent =
		new AtomicBoolean();
		
	/** Got a result from the server okay? */
	protected final AtomicBoolean remoterecvokay =
		new AtomicBoolean();
	
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
	public void endCommon()
	{
		super.assertTrue(this.remoterecvokay.get(), "remoterecvokay");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/23
	 */
	@Override
	public void endMocked()
	{
		super.assertTrue(this.errorwassent.get(), "errorwassent");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/23
	 */
	@Override
	public void endRemote()
	{
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


