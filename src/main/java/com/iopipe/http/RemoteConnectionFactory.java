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
}

