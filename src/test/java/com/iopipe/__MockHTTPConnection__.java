package com.iopipe;

import java.io.IOException;
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
		
		// Print the sent JSON data
		System.err.printf("Request: %s%n", __r);
		
		// Just check the authorization token
		if (true)
			throw new Error("TODO");
		
		// Everything is okay so treat it as such
		return new IOPipeHTTPResult(202, JsonValue.NULL);
	}
}

