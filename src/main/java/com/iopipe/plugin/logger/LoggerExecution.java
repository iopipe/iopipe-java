package com.iopipe.plugin.logger;

import com.iopipe.http.RemoteException;
import com.iopipe.IOpipeExecution;
import com.iopipe.plugin.IOpipePlugin;
import com.iopipe.plugin.IOpipePluginExecution;
import com.iopipe.plugin.IOpipePluginPostExecutable;
import com.iopipe.IOpipeSigner;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
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
	/** Lock on logging to prevent spliced logs. */
	protected final Object lock =
		new Object();;
	
	/** The temporary file. */
	protected final Path tempfile;
	
	/** The channel for the data. */
	protected final FileChannel channel;
	
	/** The stream to write to for JSON data. */
	protected final Writer writer;
	
	/** The signer. */
	private final IOpipeSigner _signer;
	
	/**
	 * Initializes the logger plugin collector.
	 *
	 * @param __exec The execution.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/09/24
	 */
	public LoggerExecution(IOpipeExecution __exec)
		throws NullPointerException
	{
		if (__exec == null)
			throw new NullPointerException();
		
		IOpipeSigner signer = __exec.signer(".log");
		
		// The signer might not be available or the logging plugging parts
		// might not initialize
		Path tempfile = null;
		FileChannel channel = null;
		Writer writer = null;
		
		// If the signer is available, setup the log to print to
		if (signer != null)
			try
			{
				// Store log data in a temporary file
				tempfile = Files.createTempFile("iopipe-logger", ".log");
				
				// Open temporary file for read/write
				channel = FileChannel.open(tempfile,
					StandardOpenOption.DELETE_ON_CLOSE,
					StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING,
					StandardOpenOption.READ,
					StandardOpenOption.WRITE);
				
				writer = new OutputStreamWriter(
					Channels.newOutputStream(channel), "utf-8");
			}
			catch (IOException e)
			{
				// Close the writer
				if (writer != null)
					try
					{
						writer.close();
					}
					catch (IOException f)
					{
					}
				
				// Close the channel
				if (channel != null)
					try
					{
						channel.close();
					}
					catch (IOException f)
					{
					}
				
				// Delete temporary
				if (tempfile != null)
					try
					{
						Files.delete(tempfile);
					}
					catch (IOException f)
					{
					}
				
				// Clear these so they are not set
				signer = null;
				tempfile = null;
				channel = null;
				writer = null;
			}
		
		// Use these
		this._signer = signer;
		this.tempfile = tempfile;
		this.channel = channel;
		this.writer = writer;
	}
	
	/**
	 * Logs the given message.
	 *
	 * @param __v The logging level.
	 * @param __n The name of the log source.
	 * @param __c The message used.
	 * @since 2018/09/26
	 */
	public final void log(Enum<?> __v, String __n, char[] __c)
	{
		this.log(System.currentTimeMillis(), (__v != null ? __v.name() : null),
			__n, (__c != null ? CharBuffer.wrap(__c) : null));
	}
	
	/**
	 * Logs the given message.
	 *
	 * @param __utcms The current time in UTC milliseconds.
	 * @param __v The logging level.
	 * @param __n The name of the log source.
	 * @param __c The message used.
	 * @since 2018/09/26
	 */
	public final void log(long __utcms, Enum<?> __v, String __n, char[] __c)
	{
		this.log(__utcms, (__v != null ? __v.name() : null),
			__n, (__c != null ? CharBuffer.wrap(__c) : null));
	}
	
	/**
	 * Logs the given message.
	 *
	 * @param __v The logging level.
	 * @param __n The name of the log source.
	 * @param __c The message used.
	 * @param __o The offset.
	 * @param __l The length.
	 * @throws IndexOutOfBoundsException If the offset and/or length are
	 * negative or exceed the array bounds.
	 * @since 2018/09/26
	 */
	public final void log(Enum<?> __v, String __n, char[] __c, int __o, int __l)
		throws IndexOutOfBoundsException
	{
		this.log(System.currentTimeMillis(), (__v != null ? __v.name() : null),
			__n, (__c != null ? CharBuffer.wrap(__c, __o, __l) : null));
	}
	
	/**
	 * Logs the given message.
	 *
	 * @param __utcms The current time in UTC milliseconds.
	 * @param __v The logging level.
	 * @param __n The name of the log source.
	 * @param __c The message used.
	 * @param __o The offset.
	 * @param __l The length.
	 * @throws IndexOutOfBoundsException If the offset and/or length are
	 * negative or exceed the array bounds.
	 * @since 2018/09/26
	 */
	public final void log(long __utcms, Enum<?> __v, String __n, char[] __c, int __o,
		int __l)
		throws IndexOutOfBoundsException
	{
		this.log(__utcms, (__v != null ? __v.name() : null),
			__n, (__c != null ? CharBuffer.wrap(__c, __o, __l) : null));
	}
	
	/**
	 * Logs the given message.
	 *
	 * @param __v The logging level.
	 * @param __n The name of the log source.
	 * @param __msg The message used.
	 * @since 2018/09/26
	 */
	public final void log(Enum<?> __v, String __n, CharSequence __msg)
	{
		this.log(System.currentTimeMillis(), (__v != null ? __v.name() : null), __n, __msg);
	}
	
	/**
	 * Logs the given message.
	 *
	 * @param __utcms The current time in UTC milliseconds.
	 * @param __v The logging level.
	 * @param __n The name of the log source.
	 * @param __msg The message used.
	 * @since 2018/09/26
	 */
	public final void log(long __utcms, Enum<?> __v, String __n, CharSequence __msg)
	{
		this.log(__utcms, (__v != null ? __v.name() : null), __n, __msg);
	}
	
	/**
	 * Logs the given message.
	 *
	 * @param __v The logging level.
	 * @param __n The name of the log source.
	 * @param __c The message used.
	 * @since 2018/09/26
	 */
	public final void log(String __v, String __n, char[] __c)
	{
		this.log(System.currentTimeMillis(), __v,
			__n, (__c != null ? CharBuffer.wrap(__c) : null));
	}
	
	/**
	 * Logs the given message.
	 *
	 * @param __utcms The current time in UTC milliseconds.
	 * @param __v The logging level.
	 * @param __n The name of the log source.
	 * @param __c The message used.
	 * @since 2018/09/26
	 */
	public final void log(long __utcms, String __v, String __n, char[] __c)
	{
		this.log(__utcms, __v,
			__n, (__c != null ? CharBuffer.wrap(__c) : null));
	}
	
	/**
	 * Logs the given message.
	 *
	 * @param __v The logging level.
	 * @param __n The name of the log source.
	 * @param __c The message used.
	 * @param __o The offset.
	 * @param __l The length.
	 * @throws IndexOutOfBoundsException If the offset and/or length are
	 * negative or exceed the array bounds.
	 * @since 2018/09/26
	 */
	public final void log(String __v, String __n, char[] __c, int __o, int __l)
		throws IndexOutOfBoundsException
	{
		this.log(System.currentTimeMillis(), __v,
			__n, (__c != null ? CharBuffer.wrap(__c, __o, __l) : null));
	}
	
	/**
	 * Logs the given message.
	 *
	 * @param __utcms The current time in UTC milliseconds.
	 * @param __v The logging level.
	 * @param __n The name of the log source.
	 * @param __c The message used.
	 * @param __o The offset.
	 * @param __l The length.
	 * @throws IndexOutOfBoundsException If the offset and/or length are
	 * negative or exceed the array bounds.
	 * @since 2018/09/26
	 */
	public final void log(long __utcms, String __v, String __n, char[] __c, int __o,
		int __l)
		throws IndexOutOfBoundsException
	{
		this.log(__utcms, __v,
			__n, (__c != null ? CharBuffer.wrap(__c, __o, __l) : null));
	}
	
	/**
	 * Logs the given message.
	 *
	 * @param __v The logging level.
	 * @param __n The name of the log source.
	 * @param __msg The message used.
	 * @since 2018/09/26
	 */
	public final void log(String __v, String __n, CharSequence __msg)
	{
		this.log(System.currentTimeMillis(), __v, __n, __msg);
	}
	
	/**
	 * Logs the given message.
	 *
	 * @param __utcms The current time in UTC milliseconds.
	 * @param __v The logging level.
	 * @param __n The name of the log source.
	 * @param __msg The message used.
	 * @since 2018/09/26
	 */
	public final void log(long __utcms, String __v, String __n, CharSequence __msg)
	{
		try
		{
			Writer writer = this.writer;
			if (writer == null)
				return;	
			
			// Lock to prevent multiple threads from interleaving log structure
			// data
			Object lock = this.lock;
			synchronized (lock)
			{
				writer.write('{');
				
				// The message
				boolean did = false;
				if (__msg != null)
				{
					did = true;
					writer.write("\"message\": \"");
					LoggerExecution.__writeChars(writer, __msg);
					writer.write('"');
				}
				
				// The name or source
				if (__n != null)
				{
					if (did)
						writer.write(", ");
					
					did = true;
					writer.write("\"name\": \"");
					LoggerExecution.__writeChars(writer, __n);
					writer.write('"');
				}
				
				// The level
				if (__v != null)
				{
					if (did)
						writer.write(", ");
					
					did = true;
					writer.write("\"severity\": \"");
					LoggerExecution.__writeChars(writer, __v);
					writer.write('"');
				}
				
				// Time in ISO-8601 format
				if (did)
					writer.write(", ");
				writer.write("\"timestamp\": \"");
				writer.write(Instant.ofEpochMilli(__utcms).toString());
				writer.write('"');
				
				writer.write("}\n");
				
				// Flush it so it exists on the disk somewhere
				writer.flush();
			}
		}
		
		// Ignore, do not log it either because this could be picked by a
		// logging framework which would result in a logging message being
		// generated.
		catch (IOException e)
		{
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
			IOpipeSigner signer = this._signer;
			FileChannel channel = this.channel;
			
			// If these failed to open previously, just ignore
			if (signer == null || channel == null)
				return;
			
			// Lock on the signer so logs are not placed while we are reading
			// the file data
			Object lock = this.lock;
			synchronized (lock)
			{
				// Send the entire file to the remote server
				try
				{
					int size = (int)Math.min(Integer.MAX_VALUE, channel.size());
					
					// Read data
					byte[] buf = new byte[size];
					channel.read(ByteBuffer.wrap(buf), 0L);
					
					// Send it in
					signer.put(buf);
				}
				catch (IOException|OutOfMemoryError|NegativeArraySizeException|
					RemoteException e)
				{
				}
			}
		}
		
		// No matter what happens during the post operation, delete the
		// temporary file so it does not consume any space!
		finally
		{
			try
			{
				this.writer.close();
				this.channel.close();
			}
			catch (IOException e)
			{
			}
			
			try
			{
				Files.delete(this.tempfile);
			}
			catch (IOException e)
			{
			}
		}
	}
	
	/**
	 * Writes log strings which are formatted for JSON strings.
	 *
	 * @param __w The writer to write to.
	 * @param __cs The input sequence.
	 * @throws IOException On write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/09/26
	 */
	private static final void __writeChars(Writer __w, CharSequence __cs)
		throws IOException, NullPointerException
	{
		if (__w == null || __cs == null)
			throw new NullPointerException();
		
		for (int i = 0, n = __cs.length(); i < n; i++)
		{
			char c = __cs.charAt(i);
			
			// Do we need to escape this character?
			boolean escape = false;
			switch (c)
			{
				case '"':
				case '\\':
				case '/':
					escape = true;
					break;
				
				case '\b':
					escape = true;
					c = 'b';
					break;
					
				case '\n':
					escape = true;
					c = 'n';
					break;
					
				case '\r':
					escape = true;
					c = 'r';
					break;
					
				case '\t':
					escape = true;
					c = 't';
					break;
				
				default:
					break;
			}
			
			if (escape)
				__w.write('\\');
			__w.write(c);
		}
	}
}

