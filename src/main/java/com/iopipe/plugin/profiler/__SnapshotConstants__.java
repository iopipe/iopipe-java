package com.iopipe.plugin.profiler;

/**
 * Constants for the snapshot format.
 *
 * @since 2018/02/15
 */
interface __SnapshotConstants__
{
	/** Compression level for snapshot data. */
	public static final int COMPRESSION_LEVEL =
		3;
	
	/** The magic number. */
	public static final String MAGIC_NUMBER =
		"nBpRoFiLeR";
	
	/** The major version number. */
	public static final int MAJOR_VERSION =
		1;
	
	/** The minor version number. */
	public static final int MINOR_VERSION =
		2;
	
	/** The CPU snapshot type. */
	public static final int TYPE_CPU =
		1;
}

