package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import static com.iopipe.__MockConfiguration__.testConfig;

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
		try (IOPipeService sv = new IOPipeService(testConfig()))
		{
		}
	}
	
	/**
	 * Tests the construction of a context.
	 *
	 * @since 2017/12/15
	 */
	public void testConstructionContext()
	{
		try (IOPipeService sv = new IOPipeService(testConfig()))
		{
			sv.createContext(new __MockContext__("testConstructionContext"));
		}
	}
	
	/**
	 * Tests the empty function which does absolutely nothing to make sure that
	 * it operates correctly.
	 *
	 * @since 2017/12/13
	 */
	public void testEmptyFunction()
	{
		try (IOPipeService sv = new IOPipeService(testConfig()))
		{
			sv.createContext(new __MockContext__("testEmptyFunction")).run(
				() -> null);
		}
	}
	
	/**
	 * This tests the empty method when the service is disabled, it should
	 * not call the IOPipe service at all.
	 *
	 * @since 2017/12/13
	 */
	public void testEmptyFunctionWhenDisabled()
	{
		try (IOPipeService sv = new IOPipeService(testConfig()))
		{
			sv.createContext(
				new __MockContext__("testEmptyFunctionWhenDisabled")).run(
				() -> null);
		}
	}
	
	/**
	 * Tests timing out of the service.
	 *
	 * @since 2017/12/15
	 */
	public void testTimeOut()
	{
		try (IOPipeService sv = new IOPipeService(testConfig()))
		{
			Context context;
			sv.createContext(
				(context = new __MockContext__("testTimeOut"))).run(
				() ->
				{
					int extratime = __MockContext__.CONTEXT_DURATION_MS +
						(__MockContext__.CONTEXT_DURATION_MS >>> 4);
					
					// Sleep for the duration time
					System.err.println("Timeout test is waiting...");
					for (;;)
					{
						// Determine how long to sleep for
						int sleepdur = context.getRemainingTimeInMillis();
						
						// Finished sleeping
						if (sleepdur <= 0)
							break;
						
						// Sleep
						try
						{
							Thread.sleep(sleepdur + extratime);
						}
						catch (InterruptedException e)
						{
						}
					}
					
					// Sleep for an extra half-second
					System.err.println("Timeout test is waiting more...");
					try
					{
						Thread.sleep(500);
					}
					catch (InterruptedException e)
					{
					}
					
					System.err.println("Timeout test finished!");
					
					return null;
				});
		}
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

