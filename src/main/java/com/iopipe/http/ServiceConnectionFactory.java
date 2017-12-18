package com.iopipe.http;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

/**
 * This is a factory which can create connections to the remote IOPipe service
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
	
	/** The URL to connect to. */
	protected final HttpUrl url;
	
	/**
	 * Initializes the connection factory.
	 *
	 * @param __url The URL to connect to.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/17
	 */
	public ServiceConnectionFactory(HttpUrl __url)
		throws NullPointerException
	{
		if (__url == null)
			throw new NullPointerException();
		
		this.url = __url;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/17
	 */
	@Override
	public RemoteConnection connect()
		throws RemoteException
	{
		throw new Error("TODO");
	}
}

