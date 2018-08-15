package com.iopipe;

import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import com.iopipe.plugin.trace.TraceExecution;
import com.iopipe.plugin.trace.TraceMeasurement;
import com.iopipe.plugin.trace.TracePlugin;
import com.iopipe.plugin.trace.TraceUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
	/** The expected order of names and types, sorted by start time. */
	private static final String[] _ORDER =
		new String[]
		{
			"start:byplugin", "mark",
			"start:byutils", "mark",
			"end:byplugin", "mark",
			"measure:byplugin", "measure",
			"end:byutils", "mark",
			"measure:byutils", "measure",
		};
	
	/** Is the plugin enabled? */
	protected final boolean enabled;
	
	/** Is the execution passed as a parameter or derived from the current. */
	protected final boolean derived;
		
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
	
	/** Order depth. */
	protected final IntegerValue orderdepth =
		new IntegerValue("orderdepth");
	
	/** Is there an auto label? */
	protected final BooleanValue hasautolabel =
		new BooleanValue("hasautolabel");
	
	/**
	 * Constructs the test.
	 *
	 * @param __e The owning engine.
	 * @param __enabled Is the plugin enabled?
	 * @param __derived Is the IOpipeExecution passed via parameter or is
	 * it derived from the current thread.
	 * @since 2018/01/24
	 */
	__DoTracePlugin__(Engine __e, boolean __enabled, boolean __derived)
	{
		super(__e, "traceplugin-" + (__enabled ? "enabled" : "disabled") +
			"-" + (__derived ? "parameter" : "derived"));
		
		this.enabled = __enabled;
		this.derived = __derived;
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
		super.assertEquals((enabled ? ((1 << (_ORDER.length / 2)) - 1) : 0), this.orderdepth);
		super.assertEquals(enabled, this.hasautolabel);
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
		StandardPushEvent event = (StandardPushEvent)__r.event;
		
		// It is invalid if there is an error
		if (!event.hasError())
			this.noerror.set(true);
		
		// See if the trace plugin was specified
		StandardPushEvent.Plugin plugin = event.plugins.get("trace");
		if (plugin != null)
			this.tracepluginspecified.set(true);
		
		// Check all entries that they are in the right order
		IntegerValue orderdepth = this.orderdepth;
		List<PerformanceEntry> es = new ArrayList<>(
			event.performanceentries.values());
		for (int i = 0, n = es.size(); i < n; i++)
		{
			PerformanceEntry e = es.get(i);
			
			// What is wanted?
			int dx = i * 2, mdx = _ORDER.length;
			String wv = (dx < mdx ? _ORDER[dx] : null),
				wt = (dx + 1 < mdx ? _ORDER[dx + 1] : null);
			
			if (Objects.equals(e.name(), wv) &&
				Objects.equals(e.type(), wt))
				orderdepth.addAndGet(1 << i);
		}
		
		if (event.labels.contains("@iopipe/plugin-trace"))
			this.hasautolabel.set(true);
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
				try (TraceMeasurement c = __p.measure("byplugin"))
				{
					// Small delay to skew time
					try
					{
						Thread.sleep(10);
					}
					catch (InterruptedException e)
					{
					}
				}
			});
		
		// Make a measurement via TraceUtils
		try (TraceMeasurement c = (this.derived ?
			TraceUtils.measure("byutils") :
			TraceUtils.measure(__e, "byutils")))
		{
			// Small delay to skew time
			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException e)
			{
			}
		}
	}
}

