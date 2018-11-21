package com.iopipe.plugin.eventinfo;

import com.iopipe.IOpipeConstants;
import com.iopipe.IOpipeExecution;
import com.iopipe.plugin.IOpipePlugin;
import com.iopipe.plugin.IOpipePluginExecution;
import com.iopipe.plugin.IOpipePluginPostExecutable;
import com.iopipe.plugin.IOpipePluginPreExecutable;

/**
 * This is the trace plugin which is used to track specific marks and measure
 * performance.
 *
 * @since 2018/04/22
 */
public class EventInfoPlugin
	implements IOpipePlugin, IOpipePluginPostExecutable
{
	/** Default set of decoders, lazily initialized. */
	private volatile EventInfoDecoders _decoders;
	
	/**
	 * {@inheritDoc}
	 * @since 2018/04/22
	 */
	@Override
	public boolean enabledByDefault()
	{
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/04/22
	 */
	@Override
	public IOpipePluginExecution execute(IOpipeExecution __e)
	{
		if (__e == null)
			throw new NullPointerException();
		
		// Lazily initialize decoders since the plugin might not actually
		// be enabled
		EventInfoDecoders decoders = this._decoders;
		if (decoders == null)
			this._decoders = (decoders = new EventInfoDecoders());
		
		return new EventInfoExecution(__e, decoders);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/04/22
	 */
	@Override
	public Class<? extends IOpipePluginExecution> executionClass()
	{
		return EventInfoExecution.class;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/04/22
	 */
	@Override
	public String homepage()
	{
		return "https://github.com/iopipe/iopipe-java";
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/04/22
	 */
	@Override
	public String name()
	{
		return "event-info";
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/04/23
	 */
	@Override
	public void postExecute(IOpipePluginExecution __e)
		throws NullPointerException
	{
		if (__e == null)
			throw new NullPointerException();
		
		((EventInfoExecution)__e).__post();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/04/22
	 */
	@Override
	public String version()
	{
		return "1.2.0";
	}
}

