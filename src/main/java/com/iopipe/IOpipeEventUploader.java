package com.iopipe;

import com.iopipe.http.RemoteRequest;

/**
 * This interface is implemented by classes which provide an implementation of
 * events that may be uploaded to the IOpipe service.
 *
 * @since 2018/07/23
 */
public interface IOpipeEventUploader
{
	/**
	 * Indicates that an event is waiting to be generated to push into the
	 * queue. This can be used to determine the number of invocations that
	 * are happening at the same time.
	 *
	 * @since 2018/07/23
	 */
	public abstract void await();
	
	/**
	 * Returns the number of requests that have failed.
	 *
	 * @return The number of requests that have failed.
	 * @since 2018/07/23
	 */
	public abstract int badRequestCount();
	
	/**
	 * Uploads the specified request to the remote server.
	 *
	 * If the uploader requires that an active invocation count be tracked then
	 * any requests which are processed will reduce that count.
	 *
	 * @param __r The request to upload.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/07/23
	 */
	public abstract void upload(RemoteRequest __r)
		throws NullPointerException;
}

