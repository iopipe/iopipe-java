package com.iopipe.http;

import java.io.IOException;
import java.io.StringReader;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonStructure;

/**
 * This is used to store a request which is sent to a remote server.
 *
 * This class is immutable.
 *
 * @since 2017/12/13
 */
public final class RemoteRequest
{
	/** The body of the request containing JSON data. */
	protected final String body;
	
	/** Json representation of the body. */
	private volatile Reference<JsonStructure> _jsonvalue;
	
	/**
	 * Initializes the request.
	 *
	 * @param __body The body of the request which contains the JSON data.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/13
	 */
	public RemoteRequest(String __body)
		throws NullPointerException
	{
		if (__body == null)
			throw new NullPointerException();
		
		this.body = __body;
	}
	
	/**
	 * Returns the message body.
	 *
	 * @return The message body.
	 * @since 2017/12/13
	 */
	public String body()
	{
		return this.body;
	}
	
	/**
	 * Returns the value of the body as a structure.
	 *
	 * @return The value of the body as a structure.
	 * @throws RemoteException If the JSON is not valid.
	 * @since 2017/12/17
	 */
	public JsonStructure bodyValue()
		throws RemoteException
	{
		Reference<JsonStructure> ref = this._jsonvalue;
		JsonStructure rv;
		
		if (ref == null || null == (rv = ref.get()))
			try
			{
				this._jsonvalue = new WeakReference<>((rv =
					Json.createReader(new StringReader(this.body)).read()));
			}
			catch (JsonException e)
			{
				throw new RemoteException("Failed to parse the body.", e);
			}
		
		return rv;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/13
	 */
	@Override
	public boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		if (!(__o instanceof RemoteRequest))
			return false;
		
		return this.body.equals(((RemoteRequest)__o).body);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/13
	 */
	@Override
	public int hashCode()
	{
		return this.body.hashCode();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/13
	 */
	@Override
	public String toString()
	{
		return this.body.toString();
	}
}

