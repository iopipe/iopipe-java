package com.iopipe.plugin.profiler;

import com.iopipe.IOpipeExecution;
import com.iopipe.IOpipeMeasurement;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

/**
 * Class which contains base methods used for exporting of data.
 *
 * @since 2018/02/15
 */
abstract class __BaseExport__
	implements __SnapshotConstants__
{
	/** The tracker data. */
	protected final Tracker tracker;
	
	/** The execution. */
	protected final IOpipeExecution execution;
	
	/** The measurement. */
	protected final IOpipeMeasurement measurement;
	
	/**
	 * Initializes the exporter.
	 *
	 * @param __t The tracker data.
	 * @param __e Execution context.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/02/12
	 */
	__BaseExport__(Tracker __t, IOpipeExecution __e)
		throws NullPointerException
	{
		if (__t == null || __e == null)
			throw new NullPointerException();
		
		this.tracker = __t;
		this.execution = __e;
		this.measurement = __e.measurement();
	}
	
	/**
	 * Returns the type of snapshot this is.
	 *
	 * @return The snapshot type.
	 * @since 2018/02/15
	 */
	public abstract int snapshotType();
	
	/**
	 * Writes the sub compression output.
	 *
	 * @param __dos The stream to write to.
	 * @throws IOException On write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/02/15
	 */
	public abstract void writeSubOutput(DataOutputStream __dos)
		throws IOException, NullPointerException;
	
	/**
	 * Exports the snapshot to the given output stream.
	 *
	 * @param __out The stream to write to.
	 * @throws IOException On write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/02/12
	 */
	public final void run(OutputStream __out)
		throws IOException, NullPointerException
	{
		if (__out == null)
			throw new NullPointerException();
		
		DataOutputStream dos = new DataOutputStream(__out);
		
		// Write header information
		dos.writeBytes(MAGIC_NUMBER);
		dos.write(MAJOR_VERSION);
		dos.write(MINOR_VERSION);
		dos.writeInt(snapshotType());
		
		// Need to export sub-data
		int complen, uncomplen;
		byte[] databuf;
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DeflaterOutputStream defl = new DeflaterOutputStream(baos,
				new Deflater(COMPRESSION_LEVEL));
			DataOutputStream xdflos = new DataOutputStream(defl))
		{
			// Write sub-data
			this.writeSubOutput(xdflos);
			
			// Flush streams and finish writing compressed data
			xdflos.flush();
			defl.finish();
			defl.flush();
			
			// Compressed length are bytes written to the byte output because
			// those 
			complen = baos.size();
			
			// Uncompressed length is data written to the data output
			uncomplen = xdflos.size();
			
			databuf = baos.toByteArray();
		}
		
		// Write snapshot data
		dos.writeInt(complen);
		dos.writeInt(uncomplen);
		dos.write(databuf);
		
		// There are no settings currently
		// Settings are in .properties file format
		dos.writeInt(0);
		// Bytes follow
		
		// Write comment just to say it was generated by the plugin
		dos.writeUTF("Generated by IOpipe profiler plugin. " +
			"<https://www.iopipe.com>");
		
		// Flush to make sure everything is written
		dos.flush();
	}
}
