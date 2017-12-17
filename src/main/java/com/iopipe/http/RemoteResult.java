package com.iopipe.http;

import java.io.IOException;
import java.io.StringReader;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonStructure;

/**
 * This is used to store the result of a request made to the service.
 *
 * This class is immutable.
 *
 * @since 2017/12/13
 */
public final class RemoteResult
{
	/** The response code of the result. */
	protected final int code;
	
	/** The body of the result. */
	protected final String body;
	
	/** String representation. */
	private volatile Reference<String> _string;
	
	/** Json representation of the body. */
	private volatile Reference<JsonStructure> _jsonvalue;
	
	/**
	 * Initializes the result.
	 *
	 * @param __code The result code of the request.
	 * @param __body The body of the result.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/13
	 */
	public RemoteResult(int __code, String __body)
		throws NullPointerException
	{
		if (__body == null)
			throw new NullPointerException();
		
		this.code = __code;
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
	 * @throws RemoteException If the body could not be parsed.
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
	 * Returns the HTTP result code.
	 *
	 * @return The HTTP result code.
	 * @since 2017/12/13
	 */
	public int code()
	{
		return this.code;
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
		
		if (!(__o instanceof RemoteResult))
			return false;
		
		RemoteResult o = (RemoteResult)__o;
		return this.code == o.code &&
			this.body.equals(o.body);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/13
	 */
	@Override
	public int hashCode()
	{
		return this.code ^ this.body.hashCode();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/13
	 */
	@Override
	public String toString()
	{
		Reference<String> ref = this._string;
		String rv;
		
		if (ref == null || null == (rv = ref.get()))
			this._string = new WeakReference<>((rv = String.format(
				"{result=%d, body=%s}", this.code, this.body)));
		
		return rv;
	}
}

