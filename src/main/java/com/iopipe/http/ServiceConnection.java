package com.iopipe.http;

import java.io.IOException;
import java.util.Objects;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.http.HttpResponse;

/**
 * This class sends requests to the remote server.
 *
 * @since 2017/12/17
 */
public final class ServiceConnection
	implements RemoteConnection
{
	/** The URL to connect to. */
	protected final String url;
	
	/**
	 * Initializes the class for sending requests.
	 *
	 * @param __url The remote URL to send requests to.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/17
	 */
	public ServiceConnection(String __url)
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
	public RemoteResult send(RemoteRequest __r)
		throws NullPointerException, RemoteException
	{
		if (__r == null)
			throw new NullPointerException();
		
		// Send request to server
		try
		{
			Response hr = Request.Post(this.url).bodyString(__r.body(),
				ContentType.APPLICATION_JSON).execute();
			
			HttpResponse rr = hr.returnResponse();
			return new RemoteResult(rr.getStatusLine().
				getStatusCode(), "");
		}
		
		catch (IOException e)
		{
			throw new RemoteException("Could not send request.", e);
		}
	}
}
