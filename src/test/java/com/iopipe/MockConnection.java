package com.iopipe;

import com.iopipe.http.RemoteConnection;
import com.iopipe.http.RemoteException;
import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import java.util.function.Consumer;
import javax.json.JsonObject;
import javax.json.JsonString;

/**
 * This implements a basic connection which only checks if the token is valid
 * and if it is it will return success or failure.
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
		
		// Check the authorization token
		if (MockConnection.VALID_TOKEN.equals(((JsonString)
			((JsonObject)__r.bodyValue()).get("client_id")).getString()))
			return new RemoteResult(202, "Accepted");
		return new RemoteResult(401,
			"{\"message\":\"Invalid client id sent.\"}");
	}
}

