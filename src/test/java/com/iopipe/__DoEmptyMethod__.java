package com.iopipe;

import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
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
	protected final BooleanValue executedit =
		new BooleanValue("executedit");
		
	/** Got mocked request? */
	protected final BooleanValue errorwasnotsent =
		new BooleanValue("errorwasnotsent");
		
	/** Got a result from the server okay? */
	protected final BooleanValue remoterecvokay =
		new BooleanValue("remoterecvokay");
	
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
	public void end()
	{
		super.assertTrue(this.executedit);
		super.assertTrue(this.remoterecvokay);
		super.assertTrue(this.errorwasnotsent);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/23
	 */
	@Override
	public void remoteRequest(WrappedRequest __r)
	{
		StandardPushEvent event = (StandardPushEvent)__r.event;
		
		if (!event.hasError())
			this.errorwasnotsent.set(true);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/23
	 */
	@Override
	public void remoteResult(WrappedResult __r)
	{
		if (__Utils__.isResultOkay(__r.result))
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


