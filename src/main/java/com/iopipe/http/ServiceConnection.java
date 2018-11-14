package com.iopipe.http;

import java.io.IOException;
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
	/** UTF-8 Encoded URL. */
	private final byte[] _url;
	
	/** UTF-8 Encoded Authorization code. */
	private final byte[] _auth;
	
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
			// Check token for invalid characters
			for (int i = 0, n = __auth.length(); i < n; i++)
				switch (__auth.charAt(i))
				{
					case '\r':
					case '\n':
						throw new RemoteException(
							"Authentication token contains invalid character: " + __auth);
				}
			
			// Decode as bytes for easier writing
			this._url = URI.create(__url).toString().getBytes("utf-8");
			this._auth = __auth.toString().getBytes("utf-8");
		}
		catch (IOException e)
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

