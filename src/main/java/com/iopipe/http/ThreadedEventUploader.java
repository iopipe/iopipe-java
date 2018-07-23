package com.iopipe.http;

import com.iopipe.IOpipeEventUploader;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This is an event uploader which
 *
 * @since 2018/07/23
 */
public final class ThreadedEventUploader
	implements IOpipeEventUploader
{
	/** The connection that is used. */
	protected final RemoteConnection connection;
	
	/** The number of bad requests. */
	private final AtomicInteger _badrequestcount =
		new AtomicInteger();
	
	/**
	 * Initializes the threaded event uploader.
	 *
	 * @param __con The connection to use.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/07/23
	 */
	public ThreadedEventUploader(RemoteConnection __con)
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
	@Override
	public final int badRequestCount()
	{
		return this._badrequestcount.get();
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
