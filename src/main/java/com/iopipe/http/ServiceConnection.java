package com.iopipe.http;

import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * This class sends requests to the remote server.
 *
 * @since 2017/12/17
 */
public final class ServiceConnection
	implements RemoteConnection
{
	/** JSON Media type. */
	private static final MediaType _JSON_TYPE =
		MediaType.parse("application/json; charset=utf-8");
	
	/** The OkHttp client manager. */
	protected final OkHttpClient client;
	
	/** The URL to connect to. */
	protected final HttpUrl url;
	
	/**
	 * Initializes the class for sending requests.
	 *
	 * @param __cl The pool where connections are sourced from.
	 * @param __url The remote URL to send requests to.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/17
	 */
	public ServiceConnection(OkHttpClient __cl, HttpUrl __url)
		throws NullPointerException
	{
		if (__cl == null || __url == null)
			throw new NullPointerException();
		
		this.client = __cl;
		this.url = __url;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/17
	 */
	@Override
	public void close()
		throws RemoteException
	{
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/17
	 */
	@Override
	public RemoteResult send(RemoteRequest __r)
		throws NullPointerException, RemoteException
	{
		if (__r == null)
			throw new NullPointerException();
		
		// Send request to server
		Response hr;
		try
		{
			hr = this.client.newCall(new Request.Builder().
				url(this.url).
				post(RequestBody.create(_JSON_TYPE, __r.body())).
				build()).execute();
			
			ResponseBody rb = hr.body();
			return new RemoteResult(hr.code(),
				(rb != null ? rb.string() : "{}"));
		}
		
		catch (IOException e)
		{
			throw new RemoteException("Could not send request.", e);
		}
	}
}
