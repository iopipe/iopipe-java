package com.iopipe.plugin.profiler;

import com.iopipe.IOpipeExecution;
import com.iopipe.IOpipeMeasurement;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.List;

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
	 * @param __sr The sampling rate.
	 * @since 2018/02/12
	 */
	__CPUExport__(Tracker __t, IOpipeExecution __e, int __sr)
	{
		super(__t, __e, __sr);
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
		long starttime = execution.startTimestamp();
		__dos.writeLong(starttime);
		__dos.writeLong((starttime) +
			(measurement.getDuration() / 1_000_000L));
		
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
	 * Writes the compact data information.
	 *
	 * @param __t The thread with the nodes to write.
	 * @throws IOException On write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/02/20
	 */
	private final byte[] __writeCompact(ThreadStat __t)
		throws IOException, NullPointerException
	{
		if (__t == null)
			throw new NullPointerException();
		
		// Determine the position of each node in the thread
		__Compact__ compact = new __Compact__();
		for (ThreadStat.Node sub : __t.subNodes())
			compact.recurse(sub);
			
		// The list makes it easier to write nodes since it can be done
		// linearly
		boolean iswide = compact.isWide();
		List<ThreadStat.Node> byindex = compact._byindex;
		Map<ThreadStat.Node, __Pointer__> offsets = compact._offsets;
		
		// Write compacted node data
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream(
			compact.approxMaxSize());
			DataOutputStream dos = new DataOutputStream(baos))
		{
			// Write every node
			for (ThreadStat.Node node : byindex)
			{
				MethodTracker.TrackedMethod method = node.method();
				
				dos.writeShort(method.index());
				dos.writeInt(Math.max(1, node.numCalls()));
				
				// Record time spent in method
				TimeKeeper graph = node.timeGraph();
				__writeFive(dos, graph.absolute());
				__writeFive(dos, graph.self());
				
				// Use same times for thread time
				TimeKeeper cpu = node.timeCPU();
				__writeFive(dos, cpu.absolute());
				__writeFive(dos, cpu.self());
				
				// Write sub-node offsets
				ThreadStat.Node[] subs = node.subNodes();
				int n = subs.length;
				dos.writeShort(n);
				for (int i = 0; i < n; i++)
				{
					ThreadStat.Node sub = subs[i];
					int p = offsets.get(sub).pointer(iswide);
					
					if (iswide)
						dos.writeInt(p);
					else
						__writeThree(dos, p);
				}
			}
			
			// Finish off
			dos.flush();
			return baos.toByteArray();
		}
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
		
		// Write compact node data
		byte[] compact = this.__writeCompact(__t);
		__dos.writeInt(compact.length);
		__dos.write(compact);
		
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
		__dos.writeLong(Math.max(1, __t.invocationCount()));
		
		// Always display whole thread CPU time
		__dos.writeBoolean(true);
	}
	
	/**
	 * Writes three bytes to the output stream.
	 *
	 * @param __dos The stream to write to.
	 * @param __val The value to write.
	 * @throws IOException On write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/02/20
	 */
	private static final void __writeThree(DataOutputStream __dos, int __val)
		throws IOException, NullPointerException
	{
		if (__dos == null)
			throw new NullPointerException();
		
		__dos.writeByte((byte)(__val >>> 16));
		__dos.writeByte((byte)(__val >>> 8));
		__dos.writeByte((byte)(__val));
	}
	
	/**
	 * Writes five bytes to the output stream.
	 *
	 * @param __dos The stream to write to.
	 * @param __val The value to write.
	 * @throws IOException On write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/02/20
	 */
	private static final void __writeFive(DataOutputStream __dos, long __val)
		throws IOException, NullPointerException
	{
		if (__dos == null)
			throw new NullPointerException();
		
		__dos.writeByte((byte)(__val >>> 32));
		__dos.writeByte((byte)(__val >>> 24));
		__dos.writeByte((byte)(__val >>> 16));
		__dos.writeByte((byte)(__val >>> 8));
		__dos.writeByte((byte)(__val));
	}
	
	/**
	 * Stored node information.
	 *
	 * @since 2018/02/20
	 */
	private static final class __Compact__
	{
		/** Nodes in index order. */
		final List<ThreadStat.Node> _byindex =
			new ArrayList<>();
		
		/** Offsets for every node. */
		final Map<ThreadStat.Node, __Pointer__> _offsets =
			new HashMap<>();
		
		/** Current write pointer (for narrow compact data). */
		private volatile int _narrowp;
		
		/** Current write pointer (for wide compact data). */
		private volatile int _widep;
		
		/**
		 * Returns the approximated maximum compact data size.
		 *
		 * @return The approximated maximum compact data size.
		 * @since 2018/02/20
		 */
		public final int approxMaxSize()
		{
			return (this.isWide() ? this._widep : this._narrowp);
		}
		
		/**
		 * Should the compact data be written wide?
		 *
		 * @return Is the data to be wide?
		 * @since 2018/02/20
		 */
		public final boolean isWide()
		{
			// After this many bytes, this becomes wide
			return this._narrowp > 16777215;
		}
		
		/**
		 * Recursively handles each node.
		 *
		 * @param __n The initial node.
		 * @throws NullPointerException On null arguments.
		 * @since 2018/02/20
		 */
		public final void recurse(ThreadStat.Node __n)
			throws NullPointerException
		{
			if (__n == null)
				throw new NullPointerException();
			
			// Internal recursive handling
			this.__recurse(__n);
		}
		
		/**
		 * Recursively handles each node, using internal logic.
		 *
		 * @param __n The initial node.
		 * @throws NullPointerException On null arguments.
		 * @since 2018/02/20
		 */
		private final void __recurse(ThreadStat.Node __n)
			throws NullPointerException
		{
			if (__n == null)
				throw new NullPointerException();
			
			// Register this node in the global list
			this._byindex.add(__n);
			
			// Set base offsets for this node
			int narrowp = this._narrowp,
				widep = this._widep;
			this._offsets.put(__n, new __Pointer__(narrowp, widep));
			
			// Determine the next positions for the following pointers
			ThreadStat.Node[] subs = __n.subNodes();
			int n = subs.length;
			narrowp += 28 + (n * 3);
			widep += 28 + (n * 4);
			
			// Set next pointer position
			this._narrowp = narrowp;
			this._widep = widep;
			
			// Recurse through subnodes to calculation their positions
			for (ThreadStat.Node sub : subs)
				this.recurse(sub);
		}
	}
	
	/**
	 * This is used to store the narrow and wide positions since recorded
	 * snapshots node data may come in two variable sizes.
	 *
	 * @since 2018/02/20
	 */
	private static final class __Pointer__
	{
		/** Pointer if the node list is narrow. */
		public final int narrow;
		
		/** Pointer if the node list is wide. */
		public final int wide;
		
		/**
		 * Initializes the pointer.
		 *
		 * @param __n The narrow pointer.
		 * @param __w The wide pointer.
		 * @since 2018/02/20
		 */
		public __Pointer__(int __n, int __w)
		{
			this.narrow = __n;
			this.wide = __w;
		}
		
		/**
		 * Returns the appropriate pointer.
		 *
		 * @param __w Is this wide?
		 * @return The used pointer.
		 * @since 2018/02/20
		 */
		public final int pointer(boolean __w)
		{
			return (__w ? this.wide : this.narrow);
		}
	}
}

