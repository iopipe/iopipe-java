package com.iopipe.http;

import java.io.IOException;
import java.util.Objects;
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
	/** The OkHttp client manager. */
	protected final OkHttpClient client;
	
	/** The URL to connect to. */
	protected final HttpUrl url;
	
	/** The optional authorization token. */
	protected final String authtoken;
	
	/**
	 * Initializes the class for sending requests.
	 *
	 * @param __cl The pool where connections are sourced from.
	 * @param __url The remote URL to send requests to.
	 * @param __auth The optional authorization token.
	 * @throws NullPointerException On null arguments except for
	 * {@code __auth}.
	 * @since 2017/12/17
	 */
	public ServiceConnection(OkHttpClient __cl, HttpUrl __url, String __auth)
		throws NullPointerException
	{
		if (__cl == null || __url == null)
			throw new NullPointerException();
		
		this.client = __cl;
		this.url = __url;
		this.authtoken = __auth;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/02/24
	 */
	@Override
	public final RemoteResult send(RequestType __t, RemoteRequest __r)
		throws NullPointerException, RemoteException
	{
		if (__t == null || __r == null)
			throw new NullPointerException();
		
		try
		{
			Request.Builder request = new Request.Builder();
			
			request.url(this.url);
			
			String authtoken = this.authtoken;
			if (authtoken != null)
				request.header("Authorization", authtoken);
			
			String mimetype = __r.mimeType();
			RequestBody body;
			if (mimetype == null || mimetype.isEmpty())
				body = RequestBody.create(null, __r.body());
			else
				body = RequestBody.create(MediaType.parse(mimetype),
					__r.body());
			
			switch (__t)
			{
				case POST:
					request.post(body);
					break;
				
				case PUT:
					request.put(body);
					break;
				
				default:
					throw new RemoteException("Unsupported type: " + __t);
			}
			
			// Send request
			Response hr = this.client.newCall(request.build()).execute();
			
			// Decode response
			try (ResponseBody rb = hr.body())
			{
				if (rb == null)
					return new RemoteResult(hr.code(), "", new byte[0]);
				else
					return new RemoteResult(hr.code(),
						Objects.toString(rb.contentType(),
						RemoteBody.MIMETYPE_JSON), rb.bytes());
			}
		}
		catch (IOException e)
		{
			throw new RemoteException("Could not send request.", e);
		}
	}
}
