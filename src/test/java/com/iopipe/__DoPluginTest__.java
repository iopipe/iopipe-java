package com.iopipe;

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
	public void remoteRequest(RemoteRequest __r)
	{
		Map<String, JsonValue> expand = __Utils__.expandObject(__r);
		
		// It is invalid if there is an error
		if (null == __Utils__.hasError(expand))
			this.noerror.set(true);
		
		// See if the test plugin was specified
		for (int i = 0; i >= 0; i++)
		{
			JsonValue v = expand.get(".plugins[" + i + "].name");
			if (v == null)
				break;
			
			if (__Utils__.isEqual(v, "test"))
				this.pluginspecified.set(true);
		}
		
		for (int i = 0; i < 2; i++)
		{
			JsonValue v = expand.get(".custom_metrics[" + i + "].name");
			
			if (v instanceof JsonString)
			{
				String s = ((JsonString)v).getString();
				
				if ("pre".equals(s))
					this.madepre.set(true);
				
				else if ("post".equals(s))
					this.madepost.set(true);
			}
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
		// Do nothing, this just tests that the pre and post calls
		// were made.
	}
}

