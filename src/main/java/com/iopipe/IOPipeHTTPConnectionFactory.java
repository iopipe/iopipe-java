package com.iopipe;

import java.io.IOException;

/**
 * This interface is used to create connections to the IOPipe server for the
 * purpose of sending and receiving requests.
 *
 * @since 2017/12/13
 */
public interface IOPipeHTTPConnectionFactory
{
	/**
	 * Initializes the connection to the IOPipe service.
	 *
	 * @return A HTTP connection to the IOPipe service.
	 * @throws IOException If the connection could not be made.
	 * @since 2017/12/13
	 */
	public abstract IOPipeHTTPConnection connect()
		throws IOException;
}

