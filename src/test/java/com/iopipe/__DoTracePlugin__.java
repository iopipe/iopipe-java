package com.iopipe;

import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import com.iopipe.plugin.trace.TraceExecution;
import com.iopipe.plugin.trace.TraceMeasurement;
import com.iopipe.plugin.trace.TracePlugin;
import java.util.Map;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

/**
 * Tests that the trace plugin exists.
 *
 * @since 2018/01/24
 */
class __DoTracePlugin__
	extends Single
{
	/** Is the plugin enabled? */
	protected final boolean enabled;
		
	/** Sent with no exception? */
	protected final BooleanValue noerror =
		new BooleanValue("noerror");
		
	/** Got a result from the server okay? */
	protected final BooleanValue remoterecvokay =
		new BooleanValue("remoterecvokay");
		
	/** Was the trace plugin body executed? */
	protected final BooleanValue tracepluginexecuted =
		new BooleanValue("tracepluginexecuted");
		
	/** Was the trace plugin specified? */
	protected final BooleanValue tracepluginspecified =
		new BooleanValue("tracepluginspecified");
		
	/** Was a mark made? */
	protected final BooleanValue mademark =
		new BooleanValue("mademark");
	
	/** Was a measurement made? */
	protected final BooleanValue mademeasurement =
		new BooleanValue("mademeasurement");
	
	/**
	 * Constructs the test.
	 *
	 * @param __e The owning engine.
	 * @param __enabled Is the plugin enabled?
	 * @since 2018/01/24
	 */
	__DoTracePlugin__(Engine __e, boolean __enabled)
	{
		super(__e, "traceplugin-" + (__enabled ? "enabled" : "disabled"));
		
		this.enabled = __enabled;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/24
	 */
	@Override
	public void end()
	{
		super.assertTrue(this.remoterecvokay);
		super.assertTrue(this.noerror);
		super.assertTrue(this.tracepluginspecified);
		
		// Depends on the enabled state of the plugin
		boolean enabled = this.enabled;
		
		super.assertEquals(enabled, this.tracepluginexecuted);
		super.assertEquals(enabled, this.mademark);
		super.assertEquals(enabled, this.mademeasurement);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/24
	 */
	@Override
	public void modifyConfig(IOpipeConfigurationBuilder __cb)
		throws NullPointerException
	{
		if (__cb == null)
			throw new NullPointerException();
		
		__cb.setPluginEnabled("trace", this.enabled);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/24
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
			
			if (__Utils__.isEqual(v, "trace"))
				this.tracepluginspecified.set(true);
		}
		
		// Was a measurement made?
		if (__Utils__.isEqual(expand.get(
			".performanceEntries[0].entryType"), "measurement"))
			this.mademeasurement.set(true);
			
		// Was a mark made?
		if (__Utils__.isEqual(expand.get(
			".performanceEntries[1].entryType"), "mark"))
			this.mademark.set(true);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/24
	 */
	@Override
	public void remoteResult(RemoteResult __r)
	{
		if (__Utils__.isResultOkay(__r))
			this.remoterecvokay.set(true);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/24
	 */
	@Override
	public void run(IOpipeExecution __e)
		throws Throwable
	{
		__e.<TraceExecution>plugin(TraceExecution.class, (__p) ->
			{
				this.tracepluginexecuted.set(true);
				
				// Make a measurement
				try (TraceMeasurement c = __p.measure("measurement"))
				{
					// Make a mark
					__p.mark("mark");
				}
			});
	}
}

