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
class __DoEmptyMethod__
	extends Single
{
	/** Was the function executed? */
	protected final AtomicBoolean executedit =
		new AtomicBoolean();
		
	/** Got mocked request? */
	protected final AtomicBoolean errorwasnotsent =
		new AtomicBoolean();
		
	/** Got a result from the server okay? */
	protected final AtomicBoolean remoterecvokay =
		new AtomicBoolean();
	
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
	public void endCommon()
	{
		super.assertTrue(this.executedit.get(), "executedit");
		super.assertTrue(this.remoterecvokay.get(), "remoterecvokay");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/23
	 */
	@Override
	public void endMocked()
	{
		super.assertTrue(this.errorwasnotsent.get(), "errorwasnotsent");
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
		if (null == __RequestUtils__.hasError(__r))
			this.errorwasnotsent.set(true);
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
		this.executedit.set(true);
	}
}


