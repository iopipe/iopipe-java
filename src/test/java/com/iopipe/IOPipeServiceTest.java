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
		new IOPipeService(__MockConfiguration__.testConfig());
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

