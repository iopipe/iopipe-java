package com.iopipe;

import com.iopipe.http.RemoteConnection;
import com.iopipe.http.RemoteException;
import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import java.util.function.Consumer;
import javax.json.JsonObject;
import javax.json.JsonString;

/**
 * This implements a basic testing connection which verifies the input request
 * and the JSON then always returns success if it is valid.
 *
 * @since 2017/12/13
 */
public final class MockConnection
	implements RemoteConnection
{
	/** This is the token which is considered to be valid for the service. */
	public static final String VALID_TOKEN =
		"ThisIsNotARealIOpipeTokenAndIsUsedForTesting";
	
	/** This is an invalid project token. */
	public static final String INVALID_TOKEN =
		"ThisIsAnInvalidTokenAndIsNotCorrect";
	
	/** When a request is made this function will be called. */
	protected final Consumer<RemoteRequest> function;
	
	/**
	 * Initializes the connection, where requests may be passed to the
	 * specified function.
	 *
	 * @param __func The function which receives requests.
	 * @since 2017/12/16
	 */
	public MockConnection(Consumer<RemoteRequest> __func)
	{
		this.function = __func;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/13
	 */
	@Override
	public RemoteResult send(RemoteRequest __r)
		throws NullPointerException, RemoteException
	{
		if (__r == null)
			throw new NullPointerException();
		
		// Send the request to the consumer so that it may test the remote
		// end accordingly
		Consumer<RemoteRequest> function = this.function;
		if (function != null)
			function.accept(__r);
		
		// Check the authorization token
		if (MockConnection.VALID_TOKEN.equals(((JsonString)
			((JsonObject)__r.bodyValue()).get("client_id")).getString()))
			return new RemoteResult(202, "Accepted");
		return new RemoteResult(401,
			"{\"message\":\"Invalid client id sent.\"}");
	}
}

