package com.iopipe.plugin.profiler;

import com.iopipe.IOpipeExecution;
import com.iopipe.IOpipeMeasurement;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

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
	__CPUExport__(Tracker __t, IOpipeExecution __e)
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
		
		Tracker tracker = this.tracker;
		MethodTracker methods = tracker.methods();
		IOpipeExecution execution = this.execution;
		IOpipeMeasurement measurement = this.measurement;
		
		// Write header fields
		__dos.writeInt(VERSION);
		__dos.writeLong(execution.startTimestamp());
		__dos.writeLong(measurement.getDuration());
		
		// Measure each thread time
		__dos.writeBoolean(true);
		
		// Record instrumented methods
		MethodTracker.TrackedMethod[] instrumented = methods.methods();
		int n = instrumented.length;
		__dos.writeInt(n);
		for (int i = 0; i < n; i++)
		{
			MethodTracker.TrackedMethod m = instrumented[i];
			
			// These may be null in which case use an empty string instead
			__dos.writeUTF(Objects.toString(m.className(), ""));
			__dos.writeUTF(Objects.toString(m.methodName(), ""));
			
			// No descriptor is used
			__dos.writeUTF("");
		}
		
		// Count and threads
		__dos.writeInt(0);
	}
}

