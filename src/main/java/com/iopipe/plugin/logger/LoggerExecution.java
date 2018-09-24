package com.iopipe.plugin.logger;

import com.iopipe.IOpipeExecution;
import com.iopipe.plugin.IOpipePlugin;
import com.iopipe.plugin.IOpipePluginExecution;
import com.iopipe.plugin.IOpipePluginPostExecutable;
import javax.json.JsonObject;

/**
 * Contains the execution state for the logger plugin.
 *
 * @since 2018/09/24
 */
public final class LoggerExecution
	implements IOpipePluginExecution
{
	/**
	 * {@inheritDoc}
	 * @since 2018/09/24
	 */
	@Override
	public final JsonObject extraReport()
	{
		return null;
	}
}

