package com.iopipe;

import com.iopipe.http.RemoteBody;
import com.iopipe.http.RemoteConnection;
import com.iopipe.http.RemoteException;
import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import com.iopipe.http.RequestType;
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
	
	/** The remote URL. */
	protected final String url;
	
	/** The authorization token. */
	protected final String authtoken;
	
	/**
	 * Initializes the mock connection.
	 *
	 * @param __url The remote URL.
	 * @param __auth The authorization token.
	 * @throws NullPointerException If no URL was specified.
	 * @since 2018/02/24
	 */
	public MockConnection(String __url, String __auth)
		throws NullPointerException
	{
		if (__url == null)
			throw new NullPointerException();
		
		this.url = __url;
		this.authtoken = __auth;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/02/24
	 */
	@Override
	public RemoteResult send(RequestType __t, RemoteRequest __r)
		throws NullPointerException, RemoteException
	{
		if (__t == null || __r == null)
			throw new NullPointerException();
		
		// Check the authorization token
		String url = this.url;
		if (url.equals(MockEngine.EVENT_URL))
		{
			if (MockConnection.VALID_TOKEN.equals(((JsonString)
				((JsonObject)__r.bodyAsJsonStructure()).get("client_id")).
				getString()))
				return new RemoteResult(202, "text/plain", "Accepted");
			return new RemoteResult(401, RemoteBody.MIMETYPE_JSON,
				"{\"message\":\"Invalid client id sent.\"}");
		}
		
		// Profiling URL
		else if (url.equals(MockEngine.PROFILER_URL))
		{
			// Authorization must be sent
			if (!MockConnection.VALID_TOKEN.equals(this.authtoken))
				return new RemoteResult(403, RemoteBody.MIMETYPE_JSON,
					"{\"message\":\"Not authorized.\"}");
			
			// Use a mocked upload URL
			return new RemoteResult(201, RemoteBody.MIMETYPE_JSON,
				"{\"signedRequest\":\"" + MockEngine.PROFILER_RESULT_URL +
					"\", \"jwtAccess\":\"token\", " +
					"\"url\":\"http://localhost/snapshot\"}");
		}
		
		// Profiling URL result
		else if (url.equals(MockEngine.PROFILER_RESULT_URL))
		{
			return new RemoteResult(200, "", "");
		}
		
		// Unknown
		else
			throw new RemoteException("Unknown remote URL: " + url);
	}
}

