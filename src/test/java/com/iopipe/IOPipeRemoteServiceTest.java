package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import com.iopipe.mock.MockContext;
import com.iopipe.mock.MockException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Objects;
import javax.json.JsonObject;
import org.junit.Test;

import static org.junit.Assume.*;
import static org.junit.Assert.*;

/**
 * This enables tests to be made to the remote service.
 *
 * These tests can only be enabled by setting the
 * {@code IOPIPE_ENABLE_REMOTE_TESTS} environment variable to true.
 *
 * @since 2017/12/17
 */
public class IOPipeRemoteServiceTest
	extends GenericTester
{
	/** If this is set then the tests here are ran. */
	public static boolean ENABLE_TESTS =
		Boolean.valueOf(Objects.toString(
			System.getenv("IOPIPE_ENABLE_REMOTE_TESTS"), "false"));
	
	/**
	 * Tests to ensure that construction is possible.
	 *
	 * @since 2017/12/13
	 */
	@Test
	public void testConstruction()
	{
		assumeTrue(ENABLE_TESTS);
		
		try (IOPipeService sv = new IOPipeService())
		{
		}
	}
	
	/**
	 * Tests the empty function which does absolutely nothing to make sure that
	 * it operates correctly.
	 *
	 * @since 2017/12/17
	 */
	@Test
	public void testEmptyFunction()
	{
		assumeTrue(ENABLE_TESTS);
		
		super.runTest("testEmptyFunction", false,
			null,
			super::baseEmptyFunction);
	}
	
	/**
	 * This tests the empty method when the service is disabled, it should
	 * not call the IOPipe service at all.
	 *
	 * @since 2017/12/17
	 */
	@Test
	public void testEmptyFunctionWhenDisabled()
	{
		assumeTrue(ENABLE_TESTS);
		
		// Requests cannot be tested locally
		super.runTest("testEmptyFunctionWhenDisabled", true, () ->
			{
				IOPipeConfigurationBuilder rv = new IOPipeConfigurationBuilder(
					IOPipeConfiguration.byDefault());
				rv.setEnabled(false);
				return rv.build();
			},
			super::baseEmptyFunctionWhenDisabled);
	}
	
	/**
	 * This ensures that invalid tokens fail on the remote end. 
	 *
	 * @since 2017/12/17
	 */
	@Test
	public void testInvalidToken()
	{
		assumeTrue(ENABLE_TESTS);
		
		super.runTest("testInvalidToken", true,
			() -> super.invalidateToken(IOPipeConfiguration.byDefault()),
			super::baseEmptyFunction);
	}
	
	/**
	 * Tests throwing of an exception.
	 *
	 * @since 2017/12/17
	 */
	@Test
	public void testThrow()
	{
		assumeTrue(ENABLE_TESTS);
		
		// Requests cannot be tested locally
		super.runTest("testThrow", false, null,
			super::baseThrow);
	}
	
	/**
	 * Tests throwing of an exception with a cause.
	 *
	 * @since 2017/12/17
	 */
	@Test
	public void testThrowWithCause()
	{
		assumeTrue(ENABLE_TESTS);
		
		// Requests cannot be tested locally
		super.runTest("testThrowWithCause", false, null,
			super::baseThrowWithCause);
	}
	
	/**
	 * Tests timing out of the service.
	 *
	 * @since 2017/12/17
	 */
	@Test
	public void testTimeOut()
	{
		assumeTrue(ENABLE_TESTS);
		
		// Requests cannot be tested locally
		super.runTest("testTimeOut", false, null,
			super::baseTimeOut);
	}
}
