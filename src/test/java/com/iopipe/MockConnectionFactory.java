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
	 * @since 2017/12/19
	 */
	@Override
	public final RemoteConnection connect(String __url, String __auth)
		throws NullPointerException, RemoteException
	{
		if (__url == null)
			throw new NullPointerException();
		
		return new MockConnection(__url, __auth);
	}
}

