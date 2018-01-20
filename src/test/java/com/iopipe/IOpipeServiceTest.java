package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import com.iopipe.mock.MockContext;
import com.iopipe.mock.MockException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Objects;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import org.junit.Test;

import static com.iopipe.mock.MockConfiguration.testConfig;
import static org.junit.Assert.*;

/**
 * This tests the {@link IOpipeServiceTest} class.
 *
 * @since 2017/12/13
 */
public class IOpipeServiceTest
	extends GenericTester
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
		IOpipeService sv = new IOpipeService(testConfig(true, null));
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
		super.runTest("testEmptyFunction", false,
			() -> testConfig(true, null),
			super::baseEmptyFunction);
	}
	
	/**
	 * This tests the empty method when the service is disabled, it should
	 * not call the IOpipe service at all.
	 *
	 * @since 2017/12/13
	 */
	@Test
	public void testEmptyFunctionWhenDisabled()
	{
		AtomicBoolean requestmade = new AtomicBoolean();
		
		super.runTest("testEmptyFunctionWhenDisabled", true,
			() -> testConfig(false, (__r) ->
			{
				requestmade.set(true);
			}),
			super::baseEmptyFunctionWhenDisabled);
		
		assertFalse("requestmade", requestmade.get());
	}
	
	/**
	 * This ensures that invalid tokens fail on the remote end. 
	 *
	 * @since 2017/12/17
	 */
	@Test
	public void testInvalidToken()
	{
		super.runTest("testInvalidToken", true,
			() -> super.invalidateToken(testConfig(true, null)),
			super::baseEmptyFunction);
	}
	
	/**
	 * Tests marking.
	 *
	 * @since 2018/01/19
	 */
	@Test
	public void testMark()
	{
		AtomicBoolean requestmade = new AtomicBoolean(),
			hasperformanceentries = new AtomicBoolean(),
			hasfirstmark = new AtomicBoolean();
		
		super.runTest("testMark", false, () -> testConfig(true, (__r) ->
			{
				requestmade.set(true);
				
				JsonObject o = (JsonObject)__r.bodyValue();
				JsonValue pev;
				if (null != (pev = o.get("performanceEntries")))
				{
					hasperformanceentries.set(true);
					
					JsonArray pea = (JsonArray)pev;
					if (pea.size() >= 1)
					{
						JsonObject q = (JsonObject)pea.get(0);
						
						if ("mark".equals(((JsonString)(q.get("entryType"))).
							getString()))
							hasfirstmark.set(true);
					}
				}
			}),
			super::baseMark);
		
		assertTrue("requestmade", requestmade.get());
		assertTrue("hasperformanceentries", hasperformanceentries.get());
		assertTrue("hasfirstmark", hasfirstmark.get());
	}
	
	/**
	 * Tests measurements.
	 *
	 * @since 2018/01/19
	 */
	@Test
	public void testMeasurement()
	{
		AtomicBoolean requestmade = new AtomicBoolean(),
			hasperformanceentries = new AtomicBoolean(),
			hasfirstmeasurement = new AtomicBoolean();
		
		super.runTest("testMeasurement", false, () -> testConfig(true, (__r) ->
			{
				requestmade.set(true);
				
				JsonObject o = (JsonObject)__r.bodyValue();
				JsonValue pev;
				if (null != (pev = o.get("performanceEntries")))
				{
					hasperformanceentries.set(true);
					
					JsonArray pea = (JsonArray)pev;
					if (pea.size() >= 1)
					{
						JsonObject q = (JsonObject)pea.get(0);
						
						if ("measurement".equals(
							((JsonString)(q.get("entryType"))).getString()))
							hasfirstmeasurement.set(true);
					}
				}
			}),
			super::baseMeasurement);
		
		assertTrue("requestmade", requestmade.get());
		assertTrue("hasperformanceentries", hasperformanceentries.get());
		assertTrue("hasfirstmeasurement", hasfirstmeasurement.get());
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
			haserror = new AtomicBoolean();
		
		super.runTest("testThrow",  false, () -> testConfig(true, (__r) ->
			{
				requestmade.set(true);
				if (((JsonObject)__r.bodyValue()).containsKey("errors"))
					haserror.set(true);
			}),
			super::baseThrow);
		
		assertTrue("requestmade", requestmade.get());
		assertTrue("haserror", haserror.get());
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
			haserror = new AtomicBoolean();
		
		super.runTest("testThrowWithCause", false,
			() -> testConfig(true, (__r) ->
			{
				requestmade.set(true);
				if (((JsonObject)__r.bodyValue()).containsKey("errors"))
					haserror.set(true);
			}),
			super::baseThrowWithCause);
		
		assertTrue("requestmade", requestmade.get());
		assertTrue("haserror", haserror.get());
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
			haserror = new AtomicBoolean();
		AtomicInteger errorcount = new AtomicInteger(),
			nonerrorcount = new AtomicInteger();
		
		super.runTest("testTimeOut", false, () -> testConfig(true, (__r) ->
			{
				requestmade.set(true);
				if (((JsonObject)__r.bodyValue()).containsKey("errors"))
					errorcount.incrementAndGet();
				else
					nonerrorcount.incrementAndGet();
			}),
			super::baseTimeOut);
		
		assertTrue("requestmade", requestmade.get());
		assertEquals("errorcount", errorcount.get(), 1);
		assertEquals("nonerrorcount", nonerrorcount.get(), 0);
	}
}

