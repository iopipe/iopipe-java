package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.iopipe.generic.EntryPoint;
import com.iopipe.generic.GenericAWSRequestStreamHandler;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import com.iopipe.IOpipeMeasurement;

/**
 * This checks to make sure that recursive calls operate correctly.
 *
 * @since 2018/08/17
 */
class __DoGenericStreamHandler__
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
	__DoGenericStreamHandler__(Engine __e)
	{
		super(__e, "genericstreamhandler");
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
		ByteArrayInputStream bin = new ByteArrayInputStream(
			"SQUIRRELS".getBytes());
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		
		// Run output
		new GenericAWSRequestStreamHandler(
			new EntryPoint(Handler.class, "handleRequest")).
			handleRequest(bin, bout, __e.context());
		
		// Check output
		bout.flush();
		__e.label(new String(bout.toByteArray()));
	}
	
	/**
	 * Handler for requests.
	 *
	 * @since 2018/08/17
	 */
	public static final class Handler
		implements RequestStreamHandler
	{
		/**
		 * {@inheritDoc}
		 * @since 2018/08/16
		 */
		@Override
		public final void handleRequest(InputStream __in,
			OutputStream __out, Context __ctx)
			throws IOException
		{
			for (;;)
			{
				int c = __in.read();
				
				if (c < 0)
					break;
				
				if (c >= 'A' && c <= 'Z')
					c = (c - 'A') + 'a';
				__out.write(c);
			}
		}
	}
}

