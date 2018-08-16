package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.iopipe.generic.EntryPoint;
import com.iopipe.generic.GenericAWSRequestStreamHandler;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This tests the generic AWS request stream handler.
 *
 * @since 2018/08/14
 */
public class GenericEntryPointAWSRequestStreamHandlerTest
	implements RequestStreamHandler
{
	/**
	 * Tests the generic entry point.
	 *
	 * @since 2018/08/14
	 */
	@Test
	public void test()
		throws Throwable
	{
		ByteArrayInputStream bin = new ByteArrayInputStream(
			"SQUIRRELS".getBytes());
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		
		// Run output
		new GenericAWSRequestStreamHandler(
			new EntryPoint(
			GenericEntryPointAWSRequestStreamHandlerTest.class, "handleRequest")
			).
			handleRequest(bin, bout, new MockContext("genericstreamentry"));
		
		// Check output
		bout.flush();
		assertEquals("squirrels", new String(bout.toByteArray()), "lowercasecall");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/08/16
	 */
	@Override
	public final void handleRequest(InputStream __in,
		OutputStream __out, Context __ctx)
		throws IOException
	{
		for (;;)
		{
			int c = __in.read();
			
			if (c < 0)
				break;
			
			if (c >= 'A' && c <= 'Z')
				c = (c - 'A') + 'a';
			__out.write(c);
		}
	}
}

