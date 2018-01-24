package com.iopipe;

import com.iopipe.http.RemoteRequest;
import javax.json.JsonObject;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This runs all of the tests using the mock service rather than the real
 * service.
 *
 * @since 2018/01/23
 */
public class MockEngine
	extends Engine
{
	/**
	 * Initializes the engine.
	 *
	 * @since 2018/01/23
	 */
	public MockEngine()
	{
		super("mock");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/23
	 */
	@Override
	protected IOpipeConfigurationBuilder generateConfig(Single __s)
		throws NullPointerException
	{
		if (__s == null)
			throw new NullPointerException();
		
		IOpipeConfigurationBuilder rv = new IOpipeConfigurationBuilder();
		
		rv.setEnabled(true);
		rv.setProjectToken(MockConnection.VALID_TOKEN);
		rv.setTimeOutWindow(150);
		
		// Use the request handler from the single test
		rv.setRemoteConnectionFactory(new MockConnectionFactory((__rr) ->
			{
				__s.mockedRequest(__rr);
			}));
		
		return rv;
	}
}

