package com.iopipe;

import com.iopipe.http.RemoteRequest;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.json.JsonObject;

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
	protected Consumer<Single> endTestFunction()
	{
		return Single::endMocked;
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
				// Force the body to be generated to check if it is valid JSON
				__rr.bodyValue();
				
				// Mock it
				__s.mockedRequest(__rr);
			}));
		
		return rv;
	}
}

