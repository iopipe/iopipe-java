package com.iopipe;

import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import com.iopipe.IOpipeMeasurement;
import java.util.Map;
import javax.json.JsonString;
import javax.json.JsonNumber;
import javax.json.JsonValue;

/**
 * This performs the test of the labels which may be added to a report.
 *
 * @since 2018/04/11
 */
class __DoLabel__
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
	
	/** Is this label supposed to appear in the dashboard? */
	protected final boolean doshow;
	
	/** The label to add. */
	protected final String label;
	
	/**
	 * Constructs the test.
	 *
	 * @param __e The owning engine.
	 * @param __show Show this in the dashboard.
	 * @param __label The label to add.
	 * @since 2018/04/11
	 */
	__DoLabel__(Engine __e, boolean __show, String __label)
	{
		super(__e, "label-" + __show + "-" + __label);
		
		this.doshow = __show;
		this.label = __label;
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
		
		super.assertEquals(this.doshow, this.haslabel);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/04/11
	 */
	@Override
	public void remoteRequest(WrappedRequest __r)
	{
		Map<String, JsonValue> expand = __Utils__.expandObject(__r.request);
		
		// It is invalid if there is an error
		if (null == __Utils__.hasError(expand))
			this.noerror.set(true);
		
		// The label must be added
		for (int i = 0; i >= 0; i++)
		{
			JsonValue jv = expand.get(".labels[" + i + "]");
			
			if (jv == null)
				break;
			
			if (__Utils__.isEqual(jv, this.label))
				this.haslabel.set(true);
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
	 * @since 2018/04/11
	 * @since 2018/01/26
	 */
	@Override
	public void run(IOpipeExecution __e)
		throws Throwable
	{
		__e.label(this.label);
	}
}

