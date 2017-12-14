package com.iopipe;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import javax.json.JsonValue;

/**
 * This is used to store the result of a request made to the service.
 *
 * This class is immutable.
 *
 * @since 2017/12/13
 */
public final class IOPipeHTTPResult
{
	/** The response code of the result. */
	protected final int code;
	
	/** The body of the result. */
	protected final JsonValue body;
	
	/** String representation. */
	private volatile Reference<String> _string;
	
	/**
	 * Initializes the result.
	 *
	 * @param __code The result code of the request.
	 * @param __body The body of the result.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/13
	 */
	public IOPipeHTTPResult(int __code, JsonValue __body)
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
	public JsonValue body()
	{
		return this.body;
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
		
		if (!(__o instanceof IOPipeHTTPResult))
			return false;
		
		IOPipeHTTPResult o = (IOPipeHTTPResult)__o;
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

