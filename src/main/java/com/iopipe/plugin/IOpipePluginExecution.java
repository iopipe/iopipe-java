package com.iopipe.plugin;

import javax.json.JsonObject;

/**
 * For plugins which provide access to information based on execution, this
 * interface is used as a base for those plugins to provide functionality that
 * is only needed per instance of a plugin.
 *
 * @since 2018/01/20
 */
public interface IOpipePluginExecution
{
	/**
	 * Plugins may add additional details to be reported, this allows the
	 * plugin to specify fields which will be added to the plugin information.
	 *
	 * @return An object containing the key and value pairs to be added to
	 * the plugin report, if the return value is {@code null} then nothing is
	 * used.
	 * @since 2018/03/15
	 */
	public default JsonObject extraReport()
	{
		return null;
	}
}

