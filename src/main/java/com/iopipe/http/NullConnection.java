package com.iopipe.http;

/**
 * This is a connection which has no effect and always returns the 503
 * Service Unavailable code.
 *
 * @since 2017/12/15
 */
public final class NullConnection
	implements RemoteConnection
{
	/**
	 * {@inheritDoc}
	 * @since 2017/12/15
	 */
	@Override
	public final RemoteResult send(RequestType __t, RemoteRequest __r)
		throws NullPointerException, RemoteException
	{
		if (__t == null || __r == null)
			throw new NullPointerException();
		
		// Report service not available
		return new RemoteResult(503, RemoteBody.MIMETYPE_JSON, new byte[0]);
	}
}

