package com.iopipe.plugin;

import com.iopipe.IOpipeExecution;
import java.lang.ref.Reference;

/**
 * This interface represents the base of a plugin and is used to manage and
 * create executable instances of plugins as they are needed.
 *
 * If it is required that state be initialized before execution begins then
 * {@link IOpipePluginPreExecutable} may be implemented.
 *
 * If it is required that finalization be performed after execution ends then
 * {@link IOpipePluginPostExecutable} may be implemented.
 *
 * This class is used with {@link java.util.ServiceLoader} to locate services.
 *
 * @see IOpipePluginPreExecutable
 * @see IOpipePluginPostExecutable
 * @since 2018/01/20
 */
public interface IOpipePlugin
{
	/**
	 * This creates a new execution for the given plugin which allows it to
	 * store state for a single execution of a method.
	 *
	 * @param __e The reference to the owning execution for this plugin.
	 * @return The execution instance for this plugin.
	 * @since 2018/01/20
	 */
	public abstract IOpipePluginExecution execute(
		Reference<IOpipeExecution> __e);
	
	/**
	 * This returns the class type of the plugin execution object, this is
	 * used to determine which state to obtain when a plugin state is
	 * requested.
	 *
	 * @return The class type of the execution class.
	 * @since 2018/01/20
	 */
	public abstract Class<? extends IOpipePluginExecution> executionClass();
	
	/**
	 * Return the name of this plugin.
	 *
	 * @return The name of this plugin.
	 * @since 2018/01/20
	 */
	public abstract String name();
	
	/**
	 * Returns the version of this plugin.
	 *
	 * @return The plugin version.
	 * @since 2018/01/20
	 */
	public abstract String version();
	
	/**
	 * Return the homepage where this plugin is located, this is optional.
	 *
	 * @return The homepage for this plugin or {@code null} if there is none.
	 * @since 2018/01/20
	 */
	public default String homepage()
	{
		return null;
	}
}

