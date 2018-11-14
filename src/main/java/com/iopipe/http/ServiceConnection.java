package com.iopipe.http;

import java.io.IOException;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * This class sends requests to the remote server.
 *
 * @since 2018/11/14
 */
public final class ServiceConnection
	implements RemoteConnection
{
	/** The URL to send to. */
	protected final URL url;
	
	/** The authentication token. */
	protected final String auth;
	
	/**
	 * Initializes the service connection.
	 *
	 * @param __url The URL to connect to.
	 * @param __auth The authentication code.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/14
	 */
	public ServiceConnection(String __url, String __auth)
		throws NullPointerException
	{
		if (__url == null || __auth == null)
			throw new NullPointerException("NARG");
		
		try
		{
			this.url = URI.create(__url).toURL();
			this.auth = __auth;
		}
		catch (NullPointerException|IllegalArgumentException|IOException e)
		{
			throw new RemoteException("Could not parse URL or authentication code.", e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/11/14
	 */
	@Override
	public final RemoteResult send(RequestType __t, RemoteRequest __r)
		throws NullPointerException, RemoteException
	{
		if (__t == null || __r == null)
			throw new NullPointerException();
		
		throw new Error("TODO");
	}
}

