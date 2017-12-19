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
	public RemoteConnection connect()
		throws RemoteException
	{
		return new NullConnection();
	}
}

