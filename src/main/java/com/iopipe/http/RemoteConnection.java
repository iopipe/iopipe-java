package com.iopipe.http;

/**
 * This interface is used to represent a single connection to the IOpipe
 * service. The server is sent {@link IOPipeHTTPRequest}s and the result of
 * those requests are returned within a {@link IOPipeHTTPResult}.
 *
 * It is unspecified if this reuses a single connection to a server which is
 * streamlined with other requests or if it opens a new connection each time
 * a request is sent.
 *
 * @since 2017/12/13
 */
public interface RemoteConnection
{
	/**
	 * Sends the specified request to the remote server.
	 *
	 * @param __r The request to send to the remote server.
	 * @return The result of the request.
	 * @throws NullPointerException On null arguments.
	 * @throws RemoteException If the request could not be sent.
	 * @since 2017/12/13
	 */
	public abstract RemoteResult send(RemoteRequest __r)
		throws NullPointerException, RemoteException;
}

