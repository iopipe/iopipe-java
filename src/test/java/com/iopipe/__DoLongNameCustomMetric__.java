package com.iopipe;

import com.iopipe.CustomMetric;
import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import com.iopipe.IOpipeMeasurement;
import java.util.Collections;
import java.util.Map;
import javax.json.JsonString;
import javax.json.JsonNumber;
import javax.json.JsonValue;

/**
 * Ensures that custom metrics which really long names are not added.
 *
 * @since 2018/04/11
 */
class __DoLongNameCustomMetric__
	extends Single
{
	/** Sent with no exception? */
	protected final BooleanValue noerror =
		new BooleanValue("noerror");
		
	/** Got a result from the server okay? */
	protected final BooleanValue remoterecvokay =
		new BooleanValue("remoterecvokay");
	
	/** Is there a custom string? */
	protected final BooleanValue hascustomstring =
		new BooleanValue("hascustomstring");
		
	/** Is there a custom number? */
	protected final BooleanValue hascustomnumber =
		new BooleanValue("hascustomnumber");
	
	/**
	 * Constructs the test.
	 *
	 * @param __e The owning engine.
	 * @since 2018/04/11
	 */
	__DoLongNameCustomMetric__(Engine __e)
	{
		super(__e, "longcustommetric");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/04/11
	 */
	@Override
	public void end()
	{
		super.assertTrue(this.remoterecvokay);
		super.assertTrue(this.noerror);
		
		super.assertFalse(this.hascustomstring);
		super.assertFalse(this.hascustomnumber);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/04/11
	 */
	@Override
	public void remoteRequest(WrappedRequest __r)
	{
		// It is invalid if there is an error
		if (!__r.event.hasError())
			this.noerror.set(true);
		
		for (CustomMetric m : __r.event.customMetrics())
		{
			if (m.hasString())
				this.hascustomstring.set(true);
			
			if (m.hasLong())
				this.hascustomnumber.set(true);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/04/11
	 */
	@Override
	public void remoteResult(WrappedResult __r)
	{
		if (__Utils__.isResultOkay(__r.result))
			this.remoterecvokay.set(true);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/04/11
	 */
	@Override
	public void run(IOpipeExecution __e)
		throws Throwable
	{
		IOpipeMeasurement m = __e.measurement();
		
		m.customMetric(String.join("", Collections.nCopies(
			IOpipeConstants.NAME_CODEPOINT_LIMIT + 32, "a")), "Very long!");
		m.customMetric(String.join("", Collections.nCopies(
			IOpipeConstants.NAME_CODEPOINT_LIMIT + 32, "b")), 12345678);
	}
}

