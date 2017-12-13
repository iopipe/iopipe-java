package com.iopipe;

/**
 * This class is used to setup configurations which are solely used for testing
 * purposes only.
 *
 * @since 2017/12/13
 */
public final class TestingConfiguration
{
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
		throw new Error("TODO");
	}
}
