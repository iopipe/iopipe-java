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
	 * store state for a single execution of a method. This is only initialized
	 * when it is needed and as such if it is required that it be initialized
	 * before the execution of a method then {@link IOpipePluginPreExecutable}
	 * should be implemented.
	 *
	 * @param __e The reference to the owning execution for this plugin.
	 * @param __enabled This specifies whether the plugin is enabled. If a
	 * plugin is disabled, it should not perform any operations.
	 * @return The execution instance for this plugin.
	 * @throws NullPointerException This may be thrown if the execution is
	 * required.
	 * @since 2018/01/20
	 */
	public abstract IOpipePluginExecution execute(
		Reference<IOpipeExecution> __e, boolean __enabled)
		throws NullPointerException;
	
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

