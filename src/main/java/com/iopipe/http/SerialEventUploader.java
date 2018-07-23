package com.iopipe.http;

import com.iopipe.http.RemoteRequest;
import com.iopipe.IOpipeEventUploader;

/**
 * This is an uploader which is completely serial based and it will only
 * upload events one at a time, blocking for each one. It cannot handle
 * multiple concurrent invocations at once.
 *
 * @since 2018/07/23
 */
public final class SerialEventUploader
	implements IOpipeEventUploader
{
	/** The connection to the remote service to use. */
	protected final RemoteConnection connection;
	
	/**
	 * Initializes the serialized event uploader.
	 *
	 * @param __con The connection to use.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/07/23
	 */
	public SerialEventUploader(RemoteConnection __con)
		throws NullPointerException
	{
		if (__con == null)
			throw new NullPointerException();
		
		this.connection = __con;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/07/23
	 */
	public final int badRequestCount()
	{
		throw new Error("TODO");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/07/23
	 */
	@Override
	public final void upload(RemoteRequest __r)
		throws NullPointerException
	{
		if (__r == null)
			throw new NullPointerException();
		
		throw new Error("TODO");
	}
}

