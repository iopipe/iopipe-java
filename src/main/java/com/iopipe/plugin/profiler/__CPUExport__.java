package com.iopipe.plugin.profiler;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This class contains all the methods which are needed to export a tracker
 * result for the CPU.
 *
 * @since 2018/02/12
 */
final class __CPUExport__
{
	/** The tracker data. */
	protected final __Tracker__ tracker;
	
	/**
	 * Initializes the exporter.
	 *
	 * @param __t The tracker data.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/02/12
	 */
	__CPUExport__(__Tracker__ __t)
		throws NullPointerException
	{
		if (__t == null)
			throw new NullPointerException();
		
		this.tracker = __t;
	}
	
	/**
	 * Exports the CPU snapshot to the given output stream.
	 *
	 * @param __out The stream to write to.
	 * @throws IOException On write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/02/12
	 */
	public void run(OutputStream __out)
		throws IOException, NullPointerException
	{
		if (__out == null)
			throw new NullPointerException();
	}
}

