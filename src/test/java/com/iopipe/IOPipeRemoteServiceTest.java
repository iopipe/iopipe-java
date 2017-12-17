package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import java.util.Objects;
import javax.json.JsonObject;
import org.junit.Test;

import static com.iopipe.__MockConfiguration__.testConfig;
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
}
