package com.iopipe;

import java.io.Closeable;
import java.io.IOException;

/**
 * This interface is used to represent a single connection to the IOPipe
 * service. The server is sent {@link IOPipeHTTPRequest}s and the result of
 * those requests are returned within a {@link IOPipeHTTPResult}.
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
