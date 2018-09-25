package com.iopipe.plugin.logger;

import com.iopipe.IOpipeExecution;
import com.iopipe.plugin.IOpipePlugin;
import com.iopipe.plugin.IOpipePluginExecution;
import com.iopipe.plugin.IOpipePluginPostExecutable;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import javax.json.JsonObject;
import org.pmw.tinylog.Logger;

/**
 * Contains the execution state for the logger plugin.
 *
 * @since 2018/09/24
 */
public final class LoggerExecution
	implements IOpipePluginExecution
{
	/** The temporary file. */
	protected final Path tempfile;
	
	/** The channel for the data. */
	protected final FileChannel channel;
	
	/** The stream to write to for JSON data. */
	protected final Writer writer;
	
	/**
	 * Initializes the logger plugin collector.
	 *
	 * @since 2018/09/24
	 */
	public LoggerExecution()
	{
		try
		{
			// Store log data in a temporary file
			Path tempfile = Files.createTempFile("iopipe-logger", ".log");
			this.tempfile = tempfile;
			
			// Open temporary file for read/write
			FileChannel channel = FileChannel.open(tempfile,
				StandardOpenOption.DELETE_ON_CLOSE,
				StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING,
				StandardOpenOption.READ,
				StandardOpenOption.WRITE);
			this.channel = channel;
			
			Writer writer = new OutputStreamWriter(
				Channels.newOutputStream(channel), "utf-8");
			this.writer = writer;
			
		}
		catch (IOException e)
		{
			throw new RuntimeException("Could not initialize the logger.", e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/09/24
	 */
	@Override
	public final JsonObject extraReport()
	{
		return null;
	}
	
	/**
	 * Post execution step.
	 *
	 * @since 2018/09/25
	 */
	final void __post()
	{
		try
		{
			throw new Error("TODO");
		}
		
		// No matter what happens during the post operation, delete the
		// temporary file so it does not consume any space!
		finally
		{
			try
			{
				this.channel.close();
			}
			catch (IOException e)
			{
				Logger.debug(e, "Could not close temporary channel.");
			}
			
			try
			{
				Files.delete(this.tempfile);
			}
			catch (IOException e)
			{
				Logger.debug(e, "Could not delete temporary logger file.");
			}
		}
	}
}

