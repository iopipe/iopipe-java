package com.iopipe;

import com.iopipe.http.RemoteRequest;

/**
 * This is an event uploader which does nothing and has no function, it is used
 * when the service is disabled.
 *
 * @since 2018/07/23
 */
final class __NullUploader__
	implements IOpipeEventUploader
{
	/**
	 * {@inheritDoc}
	 * @since 2018/07/23
	 */
	@Override
	public int badRequestCount()
	{
		return 0;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/07/23
	 */
	@Override
	public void upload(RemoteRequest __r)
		throws NullPointerException
	{
		if (__r == null)
			throw new NullPointerException();
	}
}

