package com.iopipe;

/**
 * This class is used to setup configurations which are solely used for testing
 * purposes only.
 *
 * @since 2017/12/13
 */
public final class TestingConfiguration
{
	/** This is the token which is considered to be valid for the service. */
	public static final String VALID_TOKEN =
		"ThisIsNotARealIOPipeTokenAndIsUsedForTesting";
	
	/**
	 * Not used.
	 *
	 * @since 2017/12/13
	 */
	private TestingConfiguration()
	{
	}
	
	/**
	 * Returns a new test configuration.
	 *
	 * @return A new test configuration.
	 * @since 2017/12/13
	 */
	public static final IOPipeConfiguration testConfig()
	{
		IOPipeConfigurationBuilder rv = new IOPipeConfigurationBuilder();
		
		rv.setEnabled(true);
		rv.setDebugStream(System.err);
		rv.setProjectToken(TestingConfiguration.VALID_TOKEN);
		
		return rv.build();
	}
}
