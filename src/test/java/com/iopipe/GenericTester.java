package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import com.iopipe.mock.MockConfiguration;
import com.iopipe.mock.MockContext;
import com.iopipe.mock.MockException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.iopipe.mock.MockConfiguration.testConfig;
import static org.junit.Assert.*;

/**
 * The tests for the local mock setup and the test which communicates with the
 * actual server shares the same code, so as such this used as a base to do
 * both things.
 *
 * @since 2017/12/17
 */
public abstract class GenericTester
{
	/**
	 * Tests the empty function which does absolutely nothing to make sure that
	 * it operates correctly.
	 *
	 * @param __c The execution context.
	 * @since 2017/12/18
	 */
	public final void baseEmptyFunction(IOPipeContext __c)
	{
		AtomicBoolean ranfunc = new AtomicBoolean();
		
		__c.run(() ->
			{
				ranfunc.set(true);
				return null;
			});
		
		assertTrue("ranfunc", ranfunc.get());
	}
	
	/**
	 * This tests the empty method when the service is disabled, it should
	 * not call the IOPipe service at all.
	 *
	 * @param __c The execution context.
	 * @since 2017/12/17
	 */
	public final void baseEmptyFunctionWhenDisabled(IOPipeContext __c)
	{
		AtomicBoolean ranfunc = new AtomicBoolean();
		
		__c.run(() ->
			{
				ranfunc.set(true);
				return null;
			});
		
		assertTrue("ranfunc", ranfunc.get());
	}
	
	/**
	 * Tests throwing of an exception.
	 *
	 * @param __c The execution context.
	 * @since 2017/12/17
	 */
	public final void baseThrow(IOPipeContext __c)
	{
		AtomicBoolean ranfunc = new AtomicBoolean(),
			exceptioncaught = new AtomicBoolean();
		
		try
		{
			__c.run(() ->
				{
					ranfunc.set(true);
					throw new MockException("Something went wrong!");
				});
		}
		catch (MockException e)
		{
			exceptioncaught.set(true);
		}
		
		assertTrue("ranfunc", ranfunc.get());
		assertTrue("exceptioncaught", exceptioncaught.get());
	}
	
	/**
	 * Tests throwing of an exception with a cause.
	 *
	 * @param __c The execution context.
	 * @since 2017/12/17
	 */
	public void baseThrowWithCause(IOPipeContext __c)
	{
		AtomicBoolean ranfunc = new AtomicBoolean(),
			exceptioncaught = new AtomicBoolean();
		
		try
		{
			__c.run(() ->
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
		
		assertTrue("ranfunc", ranfunc.get());
		assertTrue("exceptioncaught", exceptioncaught.get());
	}
	
	/**
	 * Tests timing out of the service.
	 *
	 * @param __c The execution context.
	 * @since 2017/12/17
	 */
	public void baseTimeOut(IOPipeContext __c)
	{
		AtomicBoolean ranfunc = new AtomicBoolean();
		
		__c.run(() ->
			{
				ranfunc.set(true);
				
				int extratime = MockContext.CONTEXT_DURATION_MS +
					(MockContext.CONTEXT_DURATION_MS >>> 4);
				
				Context context = __c.context();
				
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
		
		assertTrue("ranfunc", ranfunc.get());
	}
	
	/**
	 * Invalidates the project token.
	 *
	 * @param __c The configuration to invalidate.
	 * @return The project with the invalidated token.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/18
	 */
	public final IOPipeConfiguration invalidateToken(IOPipeConfiguration __c)
		throws NullPointerException
	{
		if (__c == null)
			throw new NullPointerException();
		
		IOPipeConfigurationBuilder rv = new IOPipeConfigurationBuilder(__c);
		
		rv.setProjectToken(MockConfiguration.INVALID_TOKEN);
		
		return rv.build();
	}
	
	/**
	 * Runs the specified test.
	 *
	 * @param __funcname The name of the function.
	 * @param __getconf The supplier for the configuration, if this is
	 * {@code null} then the default configuration will be used.
	 * @param __run The runner to use for the test.
	 * @since 2017/12/17
	 */
	public final void runTest(String __funcname,
		Supplier<IOPipeConfiguration> __getconf,
		Consumer<IOPipeContext> __run)
	{
		// Initialize the service and run the test
		IOPipeConfiguration config = (__getconf != null ? __getconf.get() :
			null);
		try (IOPipeService sv = (config != null ? new IOPipeService(config) :
			new IOPipeService()))
		{
			// Setup new context which is mocked
			IOPipeContext context = sv.createContext(
				new MockContext(__funcname));
			__run.accept(context);
		}
	}
}

