package com.iopipe;

import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import com.iopipe.plugin.trace.TraceExecution;
import com.iopipe.plugin.trace.TraceMeasurement;
import com.iopipe.plugin.trace.TracePlugin;
import com.iopipe.plugin.trace.TraceUtils;
import java.util.Map;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

/**
 * This tests the trace plugin to ensure that it operates and generates trace
 * results.
 *
 * @since 2018/02/07
 */
class __DoProfilerPlugin__
	extends Single
{
	/** Sent with no exception? */
	protected final BooleanValue noerror =
		new BooleanValue("noerror");
		
	/** Got a result from the server okay? */
	protected final BooleanValue remoterecvokay =
		new BooleanValue("remoterecvokay");
	
	/** Was the profiler plugin specified? */
	protected final BooleanValue profilerpluginspecified =
		new BooleanValue("profilerpluginspecified");
	
	/**
	 * Constructs the test.
	 *
	 * @param __e The owning engine.
	 * @since 2018/02/07
	 */
	__DoProfilerPlugin__(Engine __e)
	{
		super(__e, "profilerplugin");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/02/07
	 */
	@Override
	public void end()
	{
		super.assertTrue(this.remoterecvokay);
		super.assertTrue(this.noerror);
		super.assertTrue(this.profilerpluginspecified);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/02/07
	 */
	@Override
	public void modifyConfig(IOpipeConfigurationBuilder __cb)
		throws NullPointerException
	{
		if (__cb == null)
			throw new NullPointerException();
		
		__cb.setPluginEnabled("profiler", true);
		
		// Use a long timeout so more work can be done!
		__cb.setTimeOutWindow(30_000);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/02/07
	 */
	@Override
	public void remoteRequest(RemoteRequest __r)
	{
		Map<String, JsonValue> expand = __Utils__.expandObject(__r);
		
		// It is invalid if there is an error
		if (null == __Utils__.hasError(expand))
			this.noerror.set(true);
		
		// See if the trace plugin was specified
		for (int i = 0; i >= 0; i++)
		{
			JsonValue v = expand.get(".plugins[" + i + "].name");
			if (v == null)
				break;
			
			if (__Utils__.isEqual(v, "profiler"))
				this.profilerpluginspecified.set(true);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/02/07
	 */
	@Override
	public void remoteResult(RemoteResult __r)
	{
		if (__Utils__.isResultOkay(__r))
			this.remoterecvokay.set(true);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/02/07
	 */
	@Override
	public void run(IOpipeExecution __e)
		throws Throwable
	{
		// Do something long that can be profiled
		try
		{
			Thread.sleep(10000);
		}
		catch (InterruptedException e)
		{
		}
	}
}

