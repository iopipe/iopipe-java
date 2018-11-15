package com.iopipe.http;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

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
	 * @param __auth The authentication code, is optional.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/14
	 */
	public ServiceConnection(String __url, String __auth)
		throws NullPointerException
	{
		if (__url == null)
			throw new NullPointerException();
		
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
		
		try
		{
			HttpURLConnection con = null;
			
			// Open connection
			try
			{
				con = (HttpURLConnection)this.url.openConnection(
					Proxy.NO_PROXY);
				
				// Set parameters
				con.setDoInput(true);
				con.setDoOutput(true);
				con.setUseCaches(false);
				con.setRequestMethod(__t.name());
				con.setConnectTimeout(3000);
				con.setReadTimeout(1500);
				con.setRequestProperty("Connection", "close");
				
				String auth = this.auth;
				if (auth != null)
					con.setRequestProperty("Authorization", auth);
				
				String mime = __r.mimeType();
				if (mime != null)
					con.setRequestProperty("Content-Type", mime);
				
				// Write the request body
				byte[] data = __r.body();
				con.setRequestProperty("Content-Length", Integer.toString(data.length));
				try (OutputStream os = con.getOutputStream())
				{
					os.write(data);
				}
				
				// Read the response the server gave us, it is likely to be very
				// short
				byte[] read;
				try (InputStream is = con.getInputStream())
				{
					// Get available bytes
					int avail = Math.max(256, is.available());
					
					// Copy
					try (ByteArrayOutputStream baos =
						new ByteArrayOutputStream(avail))
					{
						byte[] buf = new byte[avail];
						for (;;)
						{
							int rc = is.read(buf);
							
							if (rc < 0)
								break;
							
							baos.write(buf, 0, rc);
						}
						
						// Use this response
						read = baos.toByteArray();
					}
				}
				
				// If read threw an exception, it is possible that the
				// server returned some bad response so return that instead
				// of throwing some exception
				catch (IOException e)
				{
					int rcode = con.getResponseCode();
					if (rcode > 0)
						return new RemoteResult(
							rcode,
							"application/octet-stream",
							new byte[0]);
					
					// Otherwise propogate it up!
					else
						throw e;
				}
				
				// Build response
				return new RemoteResult(
					con.getResponseCode(),
					"application/octet-stream",
					read);
			}
			finally
			{
				if (con != null)
					con.disconnect();
			}
		}
		catch (IOException e)
		{
			throw new RemoteException("Could not send request.", e);
		}
	}
}

