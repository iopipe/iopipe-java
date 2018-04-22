package com.iopipe.plugin.eventinfo;

import com.iopipe.IOpipeConstants;
import com.iopipe.IOpipeExecution;
import com.iopipe.plugin.IOpipePlugin;
import com.iopipe.plugin.IOpipePluginExecution;

/**
 * This is the trace plugin which is used to track specific marks and measure
 * performance.
 *
 * @since 2018/04/22
 */
public class EventInfoPlugin
	implements IOpipePlugin
{
	/** Default set of decoders. */
	protected final EventInfoDecoders decoders =
		new EventInfoDecoders();
	
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
		
		return new EventInfoExecution(__e, this.decoders);
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
	 * @since 2018/04/22
	 */
	@Override
	public String version()
	{
		return IOpipeConstants.AGENT_VERSION;
	}
}

