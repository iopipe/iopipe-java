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
 * Tests that the trace plugin exists.
 *
 * This ensures that the executed traces are in their given execution order.
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
		
	/** Was a utilities mark made? */
	protected final BooleanValue madeumark =
		new BooleanValue("madeumark");
	
	/** Was a utilities measurement made? */
	protected final BooleanValue madeumeasurement =
		new BooleanValue("madeumeasurement");
	
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
		super.assertEquals(enabled, this.madeumark);
		super.assertEquals(enabled, this.madeumeasurement);
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
	public void remoteRequest(WrappedRequest __r)
	{
		Map<String, JsonValue> expand = __Utils__.expandObject(__r.request);
		
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
		if (__Utils__.isEqual(expand.get(".performanceEntries[0].name"),
				"measurement") &&
			__Utils__.isEqual(expand.get(".performanceEntries[0].entryType"),
				"measurement"))
			this.mademeasurement.set(true);
			
		// Was a mark made?
		if (__Utils__.isEqual(expand.get(".performanceEntries[1].name"),
				"mark") &&
			__Utils__.isEqual(expand.get(".performanceEntries[1].entryType"),
				"mark"))
			this.mademark.set(true);
		
		// Was a utilities measurement made?
		if (__Utils__.isEqual(expand.get(".performanceEntries[2].name"),
				"umeasurement") &&
			__Utils__.isEqual(expand.get(".performanceEntries[2].entryType"),
				"measurement"))
			this.madeumeasurement.set(true);
			
		// Was a utilities mark made?
		if (__Utils__.isEqual(expand.get(".performanceEntries[3].name"),
				"umark") &&
			__Utils__.isEqual(expand.get(".performanceEntries[3].entryType"),
				"mark"))
			this.madeumark.set(true);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/24
	 */
	@Override
	public void remoteResult(WrappedResult __r)
	{
		if (__Utils__.isResultOkay(__r.result))
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
		// Via plugin state
		__e.<TraceExecution>plugin(TraceExecution.class, (__p) ->
			{
				this.tracepluginexecuted.set(true);
				
				// Make a measurement
				try (TraceMeasurement c = __p.measure("measurement"))
				{
					// Small delay to skew time
					try
					{
						Thread.sleep(10);
					}
					catch (InterruptedException e)
					{
					}
					
					// Make a mark
					__p.mark("mark");
				}
			});
		
		// Make a measurement via TraceUtils
		try (TraceMeasurement c = TraceUtils.measure(__e, "umeasurement"))
		{
			// Small delay to skew time
			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException e)
			{
			}
			
			// Make a mark
			TraceUtils.mark(__e, "umark");
		}
	}
}

