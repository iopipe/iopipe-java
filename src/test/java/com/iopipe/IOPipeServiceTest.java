package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import com.iopipe.mock.MockContext;
import com.iopipe.mock.MockException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.json.JsonObject;
import org.junit.Test;

import static com.iopipe.mock.MockConfiguration.testConfig;
import static org.junit.Assert.*;

/**
 * This tests the {@link IOPipeServiceTest} class.
 *
 * @since 2017/12/13
 */
public class IOPipeServiceTest
{
	/**
	 * Tests to make sure that the class can be constructed properly using
	 * the test configuration.
	 *
	 * @since 2017/12/13
	 */
	@Test
	public void testConstruction()
	{
		try (IOPipeService sv = new IOPipeService(testConfig(true, null)))
		{
		}
	}
	
	/**
	 * Tests the construction of a context.
	 *
	 * @since 2017/12/15
	 */
	@Test
	public void testConstructionContext()
	{
		try (IOPipeService sv = new IOPipeService(testConfig(true, null)))
		{
			sv.createContext(new MockContext("testConstructionContext"));
		}
	}
	
	/**
	 * Tests the empty function which does absolutely nothing to make sure that
	 * it operates correctly.
	 *
	 * @since 2017/12/13
	 */
	@Test
	public void testEmptyFunction()
	{
		AtomicBoolean ranfunc = new AtomicBoolean();
		
		try (IOPipeService sv = new IOPipeService(testConfig(true, null)))
		{
			sv.createContext(new MockContext("testEmptyFunction")).run(
				() ->
				{
					ranfunc.set(true);
					return null;
				});
		};
		
		assertTrue("ranfunc", ranfunc.get());
	}
	
	/**
	 * This tests the empty method when the service is disabled, it should
	 * not call the IOPipe service at all.
	 *
	 * @since 2017/12/13
	 */
	@Test
	public void testEmptyFunctionWhenDisabled()
	{
		AtomicBoolean requestmade = new AtomicBoolean(),
			ranfunc = new AtomicBoolean();
		
		try (IOPipeService sv = new IOPipeService(testConfig(false, (__r) ->
			{
				requestmade.set(true);
			})))
		{
			sv.createContext(
				new MockContext("testEmptyFunctionWhenDisabled")).run(
				() ->
				{
					ranfunc.set(true);
					return null;
				});
		}
		
		assertFalse("requestmade", requestmade.get());
		assertTrue("ranfunc", ranfunc.get());
	}
	
	/**
	 * Tests throwing of an exception.
	 *
	 * @since 2017/12/15
	 */
	@Test
	public void testThrow()
	{
		AtomicBoolean requestmade = new AtomicBoolean(),
			haserror = new AtomicBoolean(),
			ranfunc = new AtomicBoolean(),
			exceptioncaught = new AtomicBoolean();
		
		try (IOPipeService sv = new IOPipeService(testConfig(true, (__r) ->
			{
				requestmade.set(true);
				if (((JsonObject)__r.bodyValue()).containsKey("errors"))
					haserror.set(true);
			})))
		{
			try
			{
				sv.createContext(
					new MockContext("testThrow")).run(
					() ->
					{
						ranfunc.set(true);
						throw new MockException("Something went wrong!");
					});
			}
			catch (MockException e)
			{
				exceptioncaught.set(true);
			}
		}
		
		assertTrue("requestmade", requestmade.get());
		assertTrue("ranfunc", ranfunc.get());
		assertTrue("haserror", haserror.get());
		assertTrue("exceptioncaught", exceptioncaught.get());
	}
	
	/**
	 * Tests throwing of an exception with a cause.
	 *
	 * @since 2017/12/15
	 */
	@Test
	public void testThrowWithCause()
	{
		AtomicBoolean requestmade = new AtomicBoolean(),
			haserror = new AtomicBoolean(),
			ranfunc = new AtomicBoolean(),
			exceptioncaught = new AtomicBoolean();
		
		try (IOPipeService sv = new IOPipeService(testConfig(true, (__r) ->
			{
				requestmade.set(true);
				if (((JsonObject)__r.bodyValue()).containsKey("errors"))
					haserror.set(true);
			})))
		{
			try
			{
				sv.createContext(
					new MockContext("testThrowWithCause")).run(
					() ->
					{
						ranfunc.set(true);
						throw new MockException("Not our fault!",
							new MockException("This is why!"));
					});
			}
			
			catch (MockException e)
			{
				exceptioncaught.set(true);
			}
		}
		
		assertTrue("requestmade", requestmade.get());
		assertTrue("ranfunc", ranfunc.get());
		assertTrue("haserror", haserror.get());
		assertTrue("exceptioncaught", exceptioncaught.get());
	}
	
	/**
	 * Tests timing out of the service.
	 *
	 * @since 2017/12/15
	 */
	@Test
	public void testTimeOut()
	{
		AtomicBoolean requestmade = new AtomicBoolean(),
			haserror = new AtomicBoolean(),
			ranfunc = new AtomicBoolean();
		AtomicInteger errorcount = new AtomicInteger(),
			nonerrorcount = new AtomicInteger();
		
		try (IOPipeService sv = new IOPipeService(testConfig(true, (__r) ->
			{
				requestmade.set(true);
				if (((JsonObject)__r.bodyValue()).containsKey("errors"))
					errorcount.incrementAndGet();
				else
					nonerrorcount.incrementAndGet();
			})))
		{
			Context context;
			sv.createContext(
				(context = new MockContext("testTimeOut"))).run(
				() ->
				{
					ranfunc.set(true);
					
					int extratime = MockContext.CONTEXT_DURATION_MS +
						(MockContext.CONTEXT_DURATION_MS >>> 4);
					
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
		
		assertTrue("requestmade", requestmade.get());
		assertTrue("ranfunc", ranfunc.get());
		assertEquals("errorcount", errorcount.get(), 1);
		assertEquals("nonerrorcount", nonerrorcount.get(), 0);
	}
}

