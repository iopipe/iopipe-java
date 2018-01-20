package com.iopipe.plugin;

/**
 * This interface is used for plugins which require that when the method
 * being traced has finished executing, that it should perform some final
 * operations as needed.
 *
 * @since 2018/01/20
 */
public interface IOpipePluginPostExecutable
	extends IOpipePlugin
{
	/**
	 * Performs a post-execution of the given plugin using the specified state.
	 *
	 * @param __e The plugin execution state.
	 * @since 2018/01/20
	 */
	public abstract void postExecute(IOpipePluginExecution __e);
}

