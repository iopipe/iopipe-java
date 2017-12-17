package com.iopipe.mock;

import com.iopipe.http.RemoteRequest;
import com.iopipe.IOPipeConfiguration;
import com.iopipe.IOPipeConfigurationBuilder;
import java.util.function.Consumer;
import junit.framework.TestCase;

/**
 * This class is used to setup configurations which are solely used for testing
 * purposes only.
 *
 * @since 2017/12/13
 */
public final class MockConfiguration
{
	/** This is the token which is considered to be valid for the service. */
	public static final String VALID_TOKEN =
		"ThisIsNotARealIOPipeTokenAndIsUsedForTesting";
	
	/**
	 * Not used.
	 *
	 * @since 2017/12/13
	 */
	private MockConfiguration()
	{
	}
	
	/**
	 * Returns a new test configuration.
	 *
	 * @param __enabled A flag which specifies if the test is enabled.
	 * @param __rrh Remote test to perform when a request is sent.
	 * @return A new test configuration.
	 * @since 2017/12/13
	 */
	public static final IOPipeConfiguration testConfig(boolean __enabled,
		Consumer<RemoteRequest> __rrh)
	{
		IOPipeConfigurationBuilder rv = new IOPipeConfigurationBuilder();
		
		rv.setEnabled(__enabled);
		rv.setDebugStream(System.err);
		rv.setProjectToken(MockConfiguration.VALID_TOKEN);
		rv.setRemoteConnectionFactory(new MockConnectionFactory(__rrh));
		rv.setTimeOutWindow(150);
		
		return rv.build();
	}
}

