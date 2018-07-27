package com.iopipe.http;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * This is used to store the result of a request made to the service.
 *
 * This class is immutable.
 *
 * @since 2017/12/13
 */
public final class RemoteResult
	extends RemoteBody
{
	/** The response code of the result. */
	protected final int code;
	
	/** String representation. */
	private Reference<String> _string;
	
	/**
	 * Initializes the request with the given data.
	 *
	 * @param __c The status code of the result.
	 * @param __t The mime type of the body.
	 * @param __b The data making up the body.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/02/24
	 */
	public RemoteResult(int __c, String __t, byte[] __b)
		throws NullPointerException
	{
		super(__t, __b);
		
		this.code = __c;
	}
	
	/**
	 * Initializes the request with the given data.
	 *
	 * @param __c The status code of the result.
	 * @param __t The mime type of the body.
	 * @param __b The data making up the body.
	 * @param __o The offset.
	 * @param __l The length.
	 * @throws ArrayIndexOutOfBoundsException If the offset and/or length
	 * exceed the array bounds or are negative.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/02/24
	 */
	public RemoteResult(int __c, String __t, byte[] __b, int __o, int __l)
		throws ArrayIndexOutOfBoundsException, NullPointerException
	{
		super(__t, __b, __o, __l);
		
		this.code = __c;
	}
	
	/**
	 * Initializes the request with the given string.
	 *
	 * @param __c The status code of the result.
	 * @param __t The mime type of the body.
	 * @param __s The string to initialize the body with.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/02/24
	 */
	public RemoteResult(int __c, String __t, String __s)
		throws NullPointerException
	{
		super(__t, __s);
		
		this.code = __c;
	}
	
	/**
	 * Returns the status code of the result.
	 *
	 * @return The result status code.
	 * @since 2018/02/24
	 */
	public final int code()
	{
		return this.code;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/02/24
	 */
	@Override
	public final boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		if (!(__o instanceof RemoteResult))
			return false;
		
		RemoteResult o = (RemoteResult)__o;
		return super.equals(o) &&
			this.code == o.code;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/02/24
	 */
	@Override
	public final String toString()
	{
		Reference<String> ref = this._string;
		String rv;
		
		if (ref == null || null == (rv = ref.get()))
			this._string = new WeakReference<>((rv =
				String.format("{result=%d, type=%s, body=%d bytes}",
					this.code, this.mimetype, this.body().length)));
		
		return rv;
	}
}

