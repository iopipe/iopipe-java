package com.iopipe.plugin.profiler;

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
	/**
	 * Initializes the exporter.
	 *
	 * @param __t The tracker data.
	 * @since 2018/02/12
	 */
	__CPUExport__(__Tracker__ __t)
	{
		super(__t);
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
	}
}

