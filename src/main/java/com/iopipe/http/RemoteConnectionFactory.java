package com.iopipe.http;

/**
 * This interface is used to create connections to the IOpipe server for the
 * purpose of sending and receiving requests.
 *
 * @since 2017/12/13
 */
public interface RemoteConnectionFactory
{
	/**
	 * Initializes the connection to the IOpipe service.
	 *
	 * @return A HTTP connection to the IOpipe service.
	 * @throws RemoteException If the connection could not be made.
	 * @since 2017/12/13
	 */
	public abstract RemoteConnection connect()
		throws RemoteException;
	
	/**
	 * Initializes a connection which uses ths same means as the factory but
	 * connects to an alternative URL instead.
	 *
	 * @param __url The URL to connect to.
	 * @return A remote connection but to the specified URL instead.
	 * @throws NullPointerException On null arguments.
	 * @throws RemoteException If the connection could not be made.
	 * @since 2018/02/22
	 */
	public default RemoteConnection connectAlternateUrl(String __url)
		throws NullPointerException, RemoteException
	{
		if (__url == null)
			throw new NullPointerException();
		
		throw new RemoteException("Connect with alternative URL not " +
			"implemented.");
	}
}

