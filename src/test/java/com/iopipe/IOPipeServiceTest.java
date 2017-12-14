package com.iopipe;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This tests the {@link IOPipeServiceTest} class.
 *
 * @since 2017/12/13
 */
public class IOPipeServiceTest
	extends TestCase
{
	/**
	 * Initializes the test.
	 *
	 * @param __n The name of the test to run.
	 * @since 2017/12/13
	 */
	public IOPipeServiceTest(String __n)
	{
		super(__n);
	}
	
	/**
	 * Tests to make sure that the class can be constructed properly using
	 * the test configuration.
	 *
	 * @since 2017/12/13
	 */
	public void testConstruction()
	{
		new IOPipeService(new __MockContext__("testConstruction"),
			__MockConfiguration__.testConfig());
	}
	
	/**
	 * Tests the empty function which does absolutely nothing to make sure that
	 * it operates correctly.
	 *
	 * @since 2017/12/13
	 */
	public void testEmptyFunction()
	{
		new IOPipeService(new __MockContext__("testEmptyFunction"),
			__MockConfiguration__.testConfig()).run(
			() -> {});
	}
	
	/**
	 * This tests the empty method when the service is disabled, it should
	 * not call the IOPipe service at all.
	 *
	 * @since 2017/12/13
	 */
	public void testEmptyFunctionWhenDisabled()
	{
		new IOPipeService(new __MockContext__("testEmptyFunctionWhenDisabled"),
			__MockConfiguration__.testConfig(false)).run(
			() -> {});
	}
	
	/**
	 * Used to locate this test.
	 *
	 * @return The test used.
	 * @since 2017/12/13
	 */
    public static Test suite()
    {
        return new TestSuite(IOPipeServiceTest.class);
    }
}

