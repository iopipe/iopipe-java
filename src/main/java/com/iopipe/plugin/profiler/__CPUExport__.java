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
		
		// Always measure thread time
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
		
		// Dump thread information
		ThreadStat[] threads = tracker.threads();
		n = threads.length;
		__dos.writeInt(n);
		for (int i = 0; i < n; i++)
			this.__writeThread(__dos, threads[i]);
	}
	
	/**
	 * Writes the thread information.
	 *
	 * @param __dos The stream to write to.
	 * @param __t The thread information.
	 * @throws IOException On write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/02/19
	 */
	private final void __writeThread(DataOutputStream __dos, ThreadStat __t)
		throws IOException, NullPointerException
	{
		if (__dos == null || __t == null)
			throw new NullPointerException();
		
		__dos.writeInt(__t.logicalIndex());
		__dos.writeUTF(__t.name());
		
		// Always measure thread time
		__dos.writeBoolean(true);
		
		// TODO: write compact data
		__dos.writeInt(0);
		
		// Base sub-node size is always 28
		__dos.writeInt(28);
		
		// Gross time executing nodes in the thread
		__dos.writeLong(__t.grossWholeGraphTimeAbsolute());
		__dos.writeLong(__t.grossWholeGraphTime());
		
		// Time spent in inject methods, this always seems to be zero
		__dos.writeDouble(0);
		__dos.writeDouble(0);
		
		// Pure time??? Always seems to be this value
		__dos.writeLong(Integer.MAX_VALUE);
		__dos.writeLong(Integer.MAX_VALUE);
		
		// Time spent in thread and time spent not sleeping
		__dos.writeLong(__t.wholeGraphAbsoluteTime());
		__dos.writeLong(__t.wholeGraphTime());
		
		// Invocation count
		__dos.writeLong(__t.invocationCount());
		
		// Always display whole thread CPU time
		__dos.writeBoolean(true);
	}
}

