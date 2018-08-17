package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.iopipe.generic.EntryPoint;
import com.iopipe.generic.GenericAWSRequestHandler;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import com.iopipe.IOpipeMeasurement;

/**
 * Checks to ensure the generic handler works.
 *
 * @since 2018/08/17
 */
class __DoGenericHandler__
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
	__DoGenericHandler__(Engine __e)
	{
		super(__e, "generichandler");
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
		// Run output
		__e.label((String)(new GenericAWSRequestHandler(
			new EntryPoint(Handler.class, "handleRequest")).
			handleRequest("SQUIRRELS", __e.context())));
	}
	
	/**
	 * Handler for requests.
	 *
	 * @since 2018/08/17
	 */
	public static final class Handler
		implements RequestHandler<String, String>
	{
		/**
		 * {@inheritDoc}
		 * @since 2018/08/17
		 */
		@Override
		public final String handleRequest(String __in, Context __ctx)
		{
			return __in.toLowerCase();
		}
	}
}

