package com.iopipe.http;

/**
 * This interface is used to create connections to the IOPipe server for the
 * purpose of sending and receiving requests.
 *
 * @since 2017/12/13
 */
public interface RemoteConnectionFactory
{
	/**
	 * Initializes the connection to the IOPipe service.
	 *
	 * @return A HTTP connection to the IOPipe service.
	 * @throws RemoteException If the connection could not be made.
	 * @since 2017/12/13
	 */
	public abstract RemoteConnection connect()
		throws RemoteException;
}

