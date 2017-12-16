package com.iopipe;

import java.io.IOException;
import java.util.function.Consumer;
import javax.json.JsonValue;

/**
 * This implements a basic testing connection which verifies the input request
 * and the JSON then always returns success if it is valid.
 *
 * @since 2017/12/13
 */
final class __MockHTTPConnection__
	implements IOPipeHTTPConnection
{
	/** When a request is made this function will be called. */
	protected final Consumer<IOPipeHTTPRequest> function;
	
	/**
	 * Initializes the connection, where requests may be passed to the
	 * specified function.
	 *
	 * @param __func The function which receives requests.
	 * @since 2017/12/16
	 */
	__MockHTTPConnection__(Consumer<IOPipeHTTPRequest> __func)
	{
		this.function = __func;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/13
	 */
	@Override
	public void close()
		throws IOException
	{
		throw new Error("TODO");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/13
	 */
	@Override
	public IOPipeHTTPResult sendRequest(IOPipeHTTPRequest __r)
		throws IOException, NullPointerException
	{
		if (__r == null)
			throw new NullPointerException();
		
		// Send the request to the consumer so that it may test the remote
		// end accordingly
		Consumer<IOPipeHTTPRequest> function = this.function;
		if (function != null)
			function.accept(__r);
		
		// Everything is okay so treat it as such
		return new IOPipeHTTPResult(202, JsonValue.NULL);
	}
}

