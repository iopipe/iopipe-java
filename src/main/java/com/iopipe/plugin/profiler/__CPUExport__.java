package com.iopipe.plugin.profiler;

import com.iopipe.IOpipeExecution;
import com.iopipe.IOpipeMeasurement;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This class contains all the methods which are needed to export a tracker
 * result for the CPU.
 *
 * @since 2018/02/12
 */
final class __CPUExport__
	extends __BaseExport__
	implements __SnapshotConstants__
{
	/** The version of the snapshot. */
	public static final int VERSION =
		1;
	
	/**
	 * Initializes the exporter.
	 *
	 * @param __t The tracker data.
	 * @param __e Execution context.
	 * @since 2018/02/12
	 */
	__CPUExport__(__Tracker__ __t, IOpipeExecution __e)
	{
		super(__t, __e);
	}
	
	/**
	 * {@inheritDoc{
	 * @since 2018/02/15
	 */
	@Override
	public int snapshotType()
	{
		return TYPE_CPU;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/02/15
	 */
	@Override
	public void writeSubOutput(DataOutputStream __dos)
		throws IOException, NullPointerException
	{
		if (__dos == null)
			throw new NullPointerException();
		
		__Tracker__ tracker = this.tracker;
		IOpipeExecution execution = this.execution;
		IOpipeMeasurement measurement = this.measurement;
		
		// Write header fields
		__dos.writeInt(VERSION);
		__dos.writeLong(execution.startTimestamp());
		__dos.writeLong(measurement.getDuration());
		
		// Measure each thread time
		__dos.writeBoolean(true);
		
		// Count and instrumented methods
		__dos.writeInt(0);
		
		// Count and threads
		__dos.writeInt(0);
	}
}

