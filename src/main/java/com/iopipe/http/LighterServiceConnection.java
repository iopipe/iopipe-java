package com.iopipe.http;

/**
 * This class sends requests to the remote server.
 *
 * @since 2018/11/14
 */
public final class LighterServiceConnection
	implements RemoteConnection
{
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

