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
	/** Mocked event URL. */
	public static final String EVENT_URL =
		"https://localhost/event";
	
	/** Mocked profiler URL. */
	public static final String PROFILER_URL =
		"https://localhost/profiler";
	
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
		rv.setServiceUrl(EVENT_URL);
		rv.setProfilerUrl(PROFILER_URL);
		
		// Use the request handler from the single test
		rv.setRemoteConnectionFactory(new MockConnectionFactory());
		
		return rv;
	}
}

