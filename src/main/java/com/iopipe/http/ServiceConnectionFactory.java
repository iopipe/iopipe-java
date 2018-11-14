package com.iopipe.http;

/**
 * This is a factory which can create connections to the remote IOpipe service
 * to send reports.
 *
 * @since 2018/11/14
 */
public final class ServiceConnectionFactory
	implements RemoteConnectionFactory
{
	/**
	 * {@inheritDoc}
	 * @since 2018/11/14
	 */
	@Override
	public RemoteConnection connect(String __url, String __auth)
		throws NullPointerException, RemoteException
	{
		if (__url == null)
			throw new NullPointerException();
		
		return new ServiceConnection(__url, __auth);
	}
}

