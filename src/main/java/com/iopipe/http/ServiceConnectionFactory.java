package com.iopipe.http;

/**
 * This is a factory which can create connections to the remote IOPipe service
 * to send reports.
 *
 * @since 2017/12/17
 */
public final class ServiceConnectionFactory
	implements RemoteConnectionFactory
{
	/** The URL to connect to. */
	protected final String url;
	
	/**
	 * Initializes the connection factory.
	 *
	 * @param __url The URL to connect to.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/17
	 */
	public ServiceConnectionFactory(String __url)
		throws NullPointerException
	{
		if (__url == null)
			throw new NullPointerException();
		
		this.url = __url;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/17
	 */
	@Override
	public RemoteConnection connect()
		throws RemoteException
	{
		return new ServiceConnection(this.url);
	}
}

