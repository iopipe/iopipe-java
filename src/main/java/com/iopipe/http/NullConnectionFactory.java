package com.iopipe.http;

import javax.json.JsonValue;

/**
 * This is a factory which creates null connections.
 *
 * @since 2017/12/17
 */
public final class NullConnectionFactory
	implements RemoteConnectionFactory
{
	/**
	 * {@inheritDoc}
	 * @since 2017/12/19
	 */
	@Override
	public final RemoteConnection connect(String __url, String __auth)
		throws NullPointerException, RemoteException
	{
		if (__url == null)
			throw new NullPointerException();
		
		return new NullConnection();
	}
}

