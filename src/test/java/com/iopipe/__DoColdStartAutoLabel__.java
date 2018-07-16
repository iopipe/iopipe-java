package com.iopipe;

import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import com.iopipe.IOpipeMeasurement;
import java.util.Map;
import javax.json.JsonString;
import javax.json.JsonNumber;
import javax.json.JsonValue;

/**
 * Tests that coldstarts are given an auto label.
 *
 * @since 2018/07/16
 */
class __DoColdStartAutoLabel__
	extends Single
{
	/** Sent with no exception? */
	protected final BooleanValue noerror =
		new BooleanValue("noerror");
		
	/** Got a result from the server okay? */
	protected final BooleanValue remoterecvokay =
		new BooleanValue("remoterecvokay");
	
	/** Has coldstart? */
	protected final BooleanValue hascoldstart =
		new BooleanValue("hascoldstart");
	
	/** Does the label exist? */
	protected final BooleanValue haslabel =
		new BooleanValue("haslabel");
	
	/**
	 * Constructs the test.
	 *
	 * @param __e The owning engine.
	 * @since 2018/07/16
	 */
	__DoColdStartAutoLabel__(Engine __e)
	{
		super(__e, "coldstartautolabel");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/07/16
	 */
	@Override
	public void end()
	{
		super.assertTrue(this.remoterecvokay);
		super.assertTrue(this.noerror);
		
		super.assertTrue(this.hascoldstart);
		super.assertTrue(this.haslabel);
		
		// These conditions must be the same
		super.assertEquals(this.hascoldstart.get(), this.haslabel);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/07/16
	 */
	@Override
	public void remoteRequest(WrappedRequest __r)
	{
		StandardPushEvent event = (StandardPushEvent)__r.event;
		
		// It is invalid if there is an error
		if (!event.hasError())
			this.noerror.set(true);
		
		// Must be a cold start, for sanity
		if (event.coldstart)
			this.hascoldstart.set(true);
		
		// The label must be added
		if (event.labels.contains("@iopipe/coldstart"))
			this.haslabel.set(true);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/07/16
	 */
	@Override
	public void remoteResult(WrappedResult __r)
	{
		if (__Utils__.isResultOkay(__r.result))
			this.remoterecvokay.set(true);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/07/16
	 */
	@Override
	public void run(IOpipeExecution __e)
		throws Throwable
	{
	}
}

