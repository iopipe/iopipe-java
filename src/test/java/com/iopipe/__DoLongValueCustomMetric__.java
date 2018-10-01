package com.iopipe;

import com.iopipe.CustomMetric;
import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import java.util.Collections;
import java.util.Map;
import javax.json.JsonString;
import javax.json.JsonNumber;
import javax.json.JsonValue;

/**
 * Ensures that custom metrics which really long values are not added.
 *
 * @since 2018/05/03
 */
class __DoLongValueCustomMetric__
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
	
	/**
	 * Constructs the test.
	 *
	 * @param __e The owning engine.
	 * @since 2018/05/03
	 */
	__DoLongValueCustomMetric__(Engine __e)
	{
		super(__e, "longcustommetricvalue");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/05/03
	 */
	@Override
	public void end()
	{
		super.assertTrue(this.remoterecvokay);
		super.assertTrue(this.noerror);
		
		super.assertFalse(this.hascustomstring);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/05/03
	 */
	@Override
	public void remoteRequest(WrappedRequest __r)
	{
		StandardPushEvent event = (StandardPushEvent)__r.event;
		
		// It is invalid if there is an error
		if (!event.hasError())
			this.noerror.set(true);
		
		for (CustomMetric m : event.custommetrics.values())
		{
			if (m.hasString())
				this.hascustomstring.set(true);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/05/03
	 */
	@Override
	public void remoteResult(WrappedResult __r)
	{
		if (__Utils__.isResultOkay(__r.result))
			this.remoterecvokay.set(true);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/05/03
	 */
	@Override
	public void run(IOpipeExecution __e)
		throws Throwable
	{
		__e.customMetric("long", String.join("", Collections.nCopies(
			IOpipeConstants.VALUE_CODEPOINT_LIMIT + 32, "a")));
	}
}

