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
	 * Returns the number of requests that have failed.
	 *
	 * @return The number of requests that have failed.
	 * @since 2018/07/23
	 */
	public abstract int badRequestCount();
	
	/**
	 * Uploads the specified request to the remote server.
	 *
	 * @param __r The request to upload.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/07/23
	 */
	public abstract void upload(RemoteRequest __r)
		throws NullPointerException;
}

