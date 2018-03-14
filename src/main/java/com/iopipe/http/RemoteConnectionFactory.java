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
	 * @param __url The URL to connect to for requests.
	 * @param __auth The token to use for authorization, this is included
	 * in the headers, this is optional and may be {@code null}.
	 * @return A HTTP connection to the IOpipe service.
	 * @throws NullPointerException If no URL was specified.
	 * @throws RemoteException If the connection could not be made.
	 * @since 2017/12/13
	 */
	public abstract RemoteConnection connect(String __url, String __auth)
		throws NullPointerException, RemoteException;
}

