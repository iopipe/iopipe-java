package com.iopipe;

import java.io.Closeable;
import java.io.IOException;

/**
 * This interface is used to represent a single connection to the IOPipe
 * service. The server is sent {@link IOPipeHTTPRequest}s and the result of
 * those requests are returned within a {@link IOPipeHTTPResult}.
 *
 * It is unspecified if this reuses a single connection to a server which is
 * streamlined with other requests or if it opens a new connection each time
 * a request is sent.
 *
 * @since 2017/12/13
 */
public interface IOPipeHTTPConnection
	extends Closeable
{
	/**
	 * Sends the specified request to the remote server.
	 *
	 * @param __r The request to send to the remote server.
	 * @return The result of the request.
	 * @throws IOException On read/write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/13
	 */
	public abstract IOPipeHTTPResult sendRequest(IOPipeHTTPRequest __r)
		throws IOException, NullPointerException;
}

