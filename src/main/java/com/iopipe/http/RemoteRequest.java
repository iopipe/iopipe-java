package com.iopipe.http;

/**
 * This is used to store a request which is sent to a remote server.
 *
 * This class is immutable.
 *
 * @since 2017/12/13
 */
public final class RemoteRequest
	extends RemoteBody
{
	/**
	 * Initializes the request with the given data.
	 *
	 * @param __t The mime type of the body.
	 * @param __b The data making up the body.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/02/24
	 */
	public RemoteRequest(String __t, byte[] __b)
		throws NullPointerException
	{
		super(__t, __b);
	}
	
	/**
	 * Initializes the request with the given data.
	 *
	 * @param __t The mime type of the body.
	 * @param __b The data making up the body.
	 * @param __o The offset.
	 * @param __l The length.
	 * @throws ArrayIndexOutOfBoundsException If the offset and/or length
	 * exceed the array bounds or are negative.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/02/24
	 */
	public RemoteRequest(String __t, byte[] __b, int __o, int __l)
		throws ArrayIndexOutOfBoundsException, NullPointerException
	{
		super(__t, __b, __o, __l);
	}
	
	/**
	 * Initializes the request with the given string.
	 *
	 * @param __t The mime type of the body.
	 * @param __s The string to initialize the body with.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/02/24
	 */
	public RemoteRequest(String __t, String __s)
		throws NullPointerException
	{
		super(__t, __s);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/02/24
	 */
	@Override
	public final boolean equals(Object __o)
	{
		return super.equals(__o) && (__o instanceof RemoteRequest);
	}
}

