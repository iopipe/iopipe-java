package com.iopipe;

import javax.json.JsonValue;

/**
 * This is used to store a request which is sent to a remote server.
 *
 * This class is immutable.
 *
 * @since 2017/12/13
 */
public final class IOPipeHTTPRequest
{
	/** The body of the request containing JSON data. */
	protected final JsonValue body;
	
	/**
	 * Initializes the request.
	 *
	 * @param __body The body of the request which contains the JSON data.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/13
	 */
	public IOPipeHTTPRequest(JsonValue __body)
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
	public JsonValue body()
	{
		return this.body;
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
		
		if (!(__o instanceof IOPipeHTTPRequest))
			return false;
		
		return this.body.equals(((IOPipeHTTPRequest)__o).body);
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

