package com.iopipe.http;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

/**
 * This is a factory which can create connections to the remote IOpipe service
 * to send reports.
 *
 * @since 2017/12/17
 */
public final class ServiceConnectionFactory
	implements RemoteConnectionFactory
{
	/** The OkHttp client manager. */
	protected final OkHttpClient client =
		new OkHttpClient.Builder().build();
	
	/**
	 * {@inheritDoc}
	 * @since 2018/02/24
	 */
	@Override
	public RemoteConnection connect(String __url, String __auth)
		throws NullPointerException, RemoteException
	{
		if (__url == null)
			throw new NullPointerException();
		
		HttpUrl url = HttpUrl.parse(__url);
		if (url == null)
			throw new RemoteException("Invalid URL: " + __url);
		return new ServiceConnection(this.client, url, __auth);
	}
}

