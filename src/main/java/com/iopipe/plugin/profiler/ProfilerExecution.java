package com.iopipe.plugin.profiler;

import com.iopipe.IOpipeExecution;
import com.iopipe.plugin.IOpipePluginExecution;

/**
 * This contains the execution state for the profile plugin. This class is not
 * intended to be used by the user.
 *
 * @since 2018/02/07
 */
public class ProfilerExecution
	implements IOpipePluginExecution
{
	/** The execution state. */
	protected final IOpipeExecution execution;
	
	/**
	 * Initializes the profiler state.
	 *
	 * @param __e The execution state.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/02/03
	 */
	public ProfilerExecution(IOpipeExecution __e)
		throws NullPointerException
	{
		if (__e == null)
			throw new NullPointerException();
		
		this.execution = __e;
	}
	
	/**
	 * Post execution.
	 *
	 * @since 2018/02/09
	 */
	final void __post()
	{
		throw new Error("TODO");
	}
	
	/**
	 * Pre execution.
	 *
	 * @since 2018/02/09
	 */
	final void __pre()
	{
		throw new Error("TODO");
	}
}

