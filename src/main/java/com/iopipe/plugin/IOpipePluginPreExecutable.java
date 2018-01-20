package com.iopipe.plugin;

/**
 * This interface is used for plugins which must be initialized and setup
 * before the logged method begins execution.
 *
 * @since 2018/01/20
 */
public interface IOpipePluginPreExecutable
	extends IOpipePlugin
{
	/**
	 * Performs a pre-execution of the given plugin using the specified state.
	 *
	 * @param __e The plugin execution state.
	 * @since 2018/01/20
	 */
	public abstract void preExecute(IOpipePluginExecution __e);
}

