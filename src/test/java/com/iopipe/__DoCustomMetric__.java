package com.iopipe;

import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import com.iopipe.IOpipeMeasurement;
import java.util.Map;
import javax.json.JsonString;
import javax.json.JsonNumber;
import javax.json.JsonValue;

/**
 * Tests that custom metrics are recorded properly.
 *
 * @since 2018/01/26
 */
class __DoCustomMetric__
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
	 * @since 2018/01/26
	 */
	__DoCustomMetric__(Engine __e)
	{
		super(__e, "custommetric");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/26
	 */
	@Override
	public void end()
	{
		super.assertTrue(this.remoterecvokay);
		super.assertTrue(this.noerror);
		
		super.assertTrue(this.hascustomstring);
		super.assertTrue(this.hascustomnumber);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/26
	 */
	@Override
	public void remoteRequest(RemoteRequest __r)
	{
		Map<String, JsonValue> expand = __Utils__.expandObject(__r);
		
		// It is invalid if there is an error
		if (null == __Utils__.hasError(expand))
			this.noerror.set(true);
		
		for (int i = 0; i < 2; i++)
		{
			JsonValue sv = expand.get(".custom_metrics[" + i + "].s");
			JsonValue nv = expand.get(".custom_metrics[" + i + "].n");
			
			if (sv != null)
				this.hascustomstring.set(true);
			
			if (nv != null)
				this.hascustomnumber.set(true);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/26
	 */
	@Override
	public void remoteResult(RemoteResult __r)
	{
		if (__Utils__.isResultOkay(__r))
			this.remoterecvokay.set(true);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/26
	 */
	@Override
	public void run(IOpipeExecution __e)
		throws Throwable
	{
		IOpipeMeasurement m = __e.measurement();
		
		m.customMetric("string", "Squirrels are cute!");
		m.customMetric("number", 6012716073268438380L);
	}
}

