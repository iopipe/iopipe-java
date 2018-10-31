package com.iopipe;

import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import com.iopipe.IOpipeConfiguration;
import java.util.Map;
import javax.json.JsonString;
import javax.json.JsonNumber;
import javax.json.JsonValue;

/**
 * This performs a test to see if the config file works.
 *
 * @since 2018/10/31
 */
class __DoConfigFileTest__
	extends Single
{
	/** Sent with no exception? */
	protected final BooleanValue noerror =
		new BooleanValue("noerror");
		
	/** Got a result from the server okay? */
	protected final BooleanValue remoterecvokay =
		new BooleanValue("remoterecvokay");
	
	/** Has config option? */
	protected final BooleanValue hasconfig =
		new BooleanValue("hasconfig");
	
	/** Has missing config option? */
	protected final BooleanValue hasnoconfig =
		new BooleanValue("hasnoconfig");
	
	/**
	 * Constructs the test.
	 *
	 * @param __e The owning engine.
	 * @since 2018/10/31
	 */
	__DoConfigFileTest__(Engine __e)
	{
		super(__e, "configfile");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/10/31
	 */
	@Override
	public void end()
	{
		super.assertTrue(this.remoterecvokay);
		super.assertTrue(this.noerror);
		super.assertTrue(this.hasconfig);
		super.assertTrue(this.hasnoconfig);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/10/31
	 */
	@Override
	public void remoteRequest(WrappedRequest __r)
	{
		StandardPushEvent event = (StandardPushEvent)__r.event;
		
		// It is invalid if there is an error
		if (!event.hasError())
			this.noerror.set(true);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/10/31
	 */
	@Override
	public void remoteResult(WrappedResult __r)
	{
		if (__Utils__.isResultOkay(__r.result))
			this.remoterecvokay.set(true);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/10/31
	 */
	@Override
	public void run(IOpipeExecution __e)
		throws Throwable
	{
		// Check config
		if ("squirrel".equals(IOpipeConfiguration.getVariable(
			"com.iopipe.cutestanimal", "IOPIPE_CUTEST_ANIMAL", "nope")))
			this.hasconfig.set(true);
			
		// Check missing config
		if ("no!".equals(IOpipeConfiguration.getVariable(
			"asohdpuiashdpiusdapfiuhdsf", "ASOHDPUIASHDPIUSDAPFIUHDSF", "no!")))
			this.hasnoconfig.set(true);
	}
}

