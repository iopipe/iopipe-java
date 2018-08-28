package com.iopipe;

import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import com.iopipe.IOpipeMeasurement;

/**
 * This checks to make sure that recursive calls operate correctly.
 *
 * @since 2018/08/17
 */
class __DoRecursive__
	extends Single
{
	/** Sent with no exception? */
	protected final BooleanValue noerror =
		new BooleanValue("noerror");
		
	/** Got a result from the server okay? */
	protected final BooleanValue remoterecvokay =
		new BooleanValue("remoterecvokay");
	
	/** Does the label exist? */
	protected final BooleanValue haslabel =
		new BooleanValue("haslabel");
	
	/**
	 * Constructs the test.
	 *
	 * @param __e The owning engine.
	 * @since 2018/08/17
	 */
	__DoRecursive__(Engine __e)
	{
		super(__e, "recursive");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/08/17
	 */
	@Override
	public void end()
	{
		super.assertTrue(this.remoterecvokay);
		super.assertTrue(this.noerror);
		
		super.assertTrue(this.haslabel);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/08/17
	 */
	@Override
	public void remoteRequest(WrappedRequest __r)
	{
		StandardPushEvent event = (StandardPushEvent)__r.event;
		
		// It is invalid if there is an error
		if (!event.hasError())
			this.noerror.set(true);
		
		// The label must be added
		if (event.labels.contains("squirrels"))
			this.haslabel.set(true);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/08/17
	 */
	@Override
	public void remoteResult(WrappedResult __r)
	{
		if (__Utils__.isResultOkay(__r.result))
			this.remoterecvokay.set(true);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/08/17
	 */
	@Override
	public void run(IOpipeExecution __e)
		throws Throwable
	{
		IOpipeService.instance().run(__e.context(), (__x) ->
			{
				__x.label("squirrels");
				return null;
			});
	}
}

