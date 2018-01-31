package com.iopipe;

import com.iopipe.http.RemoteConnection;
import com.iopipe.http.RemoteConnectionFactory;
import com.iopipe.http.RemoteException;
import com.iopipe.http.RemoteRequest;

/**
 * This is the connection factory which is only meant to be used for testing.
 * It only verifies that the token is okay.
 *
 * @since 2017/12/13
 */
public final class MockConnectionFactory
	implements RemoteConnectionFactory
{
	/**
	 * {@inheritDoc}
	 * @since 2017/12/13
	 */
	@Override
	public RemoteConnection connect()
		throws RemoteException
	{
		return new MockConnection();
	}
}

