package com.iopipe;

import com.iopipe.CustomMetric;
import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import java.util.Map;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

/**
 * Tests that the test plugin runs its pre and post methods.
 *
 * @since 2018/01/26
 */
class __DoPluginTest__
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
	
	/** Was the plugin specified? */
	protected final BooleanValue pluginspecified =
		new BooleanValue("pluginspecified");
	
	/** Pre-execution made? */
	protected final BooleanValue madepre =
		new BooleanValue("madepre");
	
	/** Post-execution made? */
	protected final BooleanValue madepost =
		new BooleanValue("madepost");
	
	/**
	 * Constructs the test.
	 *
	 * @param __e The owning engine.
	 * @param __enabled Is the plugin enabled?
	 * @since 2018/01/26
	 */
	__DoPluginTest__(Engine __e, boolean __enabled)
	{
		super(__e, "testplugin-" + (__enabled ? "enabled" : "disabled"));
		
		this.enabled = __enabled;
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
		super.assertTrue(this.pluginspecified);
		
		// Depends on the enabled state of the plugin
		boolean enabled = this.enabled;
		
		super.assertEquals(enabled, this.madepre);
		super.assertEquals(enabled, this.madepost);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/26
	 */
	@Override
	public void modifyConfig(IOpipeConfigurationBuilder __cb)
		throws NullPointerException
	{
		if (__cb == null)
			throw new NullPointerException();
		
		__cb.setPluginEnabled("test", this.enabled);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/26
	 */
	@Override
	public void remoteRequest(WrappedRequest __r)
	{
		// It is invalid if there is an error
		if (!__r.event.hasError())
			this.noerror.set(true);
		
		// See if the test plugin was specified
		DecodedEvent.Plugin plugin = __r.event.plugin("test");
		if (plugin != null)
			this.pluginspecified.set(true);
		
		// Check if pre and post calls were made
		if (__r.event.customMetric("pre") != null)
			this.madepre.set(true);
		
		if (__r.event.customMetric("post") != null)
			this.madepost.set(true);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/26
	 */
	@Override
	public void remoteResult(WrappedResult __r)
	{
		if (__Utils__.isResultOkay(__r.result))
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
		// Do nothing, this just tests that the pre and post calls
		// were made.
	}
}

