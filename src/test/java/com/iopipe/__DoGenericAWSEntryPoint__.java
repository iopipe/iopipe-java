package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import com.iopipe.elsewhere.AWSEntries;
import com.iopipe.generic.EntryPoint;
import com.iopipe.generic.GenericAWSRequestHandler;
import com.iopipe.generic.GenericAWSRequestStreamHandler;
import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.util.Arrays;

/**
 * Tests all of the AWS entry point types.
 *
 * @since 2018/08/24
 */
class __DoGenericAWSEntryPoint__
	extends Single
{
	/** Sent with no exception? */
	protected final BooleanValue noerror =
		new BooleanValue("noerror");
		
	/** Got a result from the server okay? */
	protected final BooleanValue remoterecvokay =
		new BooleanValue("remoterecvokay");
	
	/** Methods executed. */
	protected final IntegerValue count =
		new IntegerValue("count");
	
	/** Labels counted. */
	protected final IntegerValue labelcount =
		new IntegerValue("labelcount");
	/**
	 * Constructs the test.
	 *
	 * @param __e The owning engine.
	 * @since 2018/08/24
	 */
	__DoGenericAWSEntryPoint__(Engine __e)
	{
		super(__e, "awsentrypoint");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/08/24
	 */
	@Override
	public void end()
	{
		super.assertTrue(this.remoterecvokay);
		super.assertTrue(this.noerror);
		
		super.assertEquals(14, this.count);
		super.assertEquals(14, this.labelcount);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/08/24
	 */
	@Override
	public void remoteRequest(WrappedRequest __r)
	{
		StandardPushEvent event = (StandardPushEvent)__r.event;
		
		// It is invalid if there is an error
		if (!event.hasError())
			this.noerror.set(true);
		
		for (String l : event.labels)
			if (l.startsWith("static") || l.startsWith("instance"))
				this.labelcount.increment();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/08/24
	 */
	@Override
	public void remoteResult(WrappedResult __r)
	{
		if (__Utils__.isResultOkay(__r.result))
			this.remoterecvokay.set(true);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/08/24
	 */
	@Override
	public void run(IOpipeExecution __e)
		throws Throwable
	{
		IntegerValue count = this.count;
		Context context = __e.context();
		
		byte[] bytes = "squirrels".getBytes();
		ByteArrayInputStream in;
		ByteArrayOutputStream out;
		
		try
		{
			// Static checks
			if ("squirrels".equals(new GenericAWSRequestHandler(
				AWSEntries.class, "static0").
				handleRequest("squirrels", context)))
				count.increment();
				
			if ("squirrels".equals(new GenericAWSRequestHandler(
				AWSEntries.class, "static1").
				handleRequest("squirrels", context)))
				count.increment();
			
			if ("squirrels".equals(new GenericAWSRequestHandler(
				AWSEntries.class, "static2").
				handleRequest("squirrels", context)))
				count.increment();
			
			in = new ByteArrayInputStream(bytes);
			out = new ByteArrayOutputStream();
			new GenericAWSRequestStreamHandler(AWSEntries.class, "static3").
				handleRequest(in, out, context);
			if (Arrays.equals(bytes, out.toByteArray()))
				count.increment();
				
			in = new ByteArrayInputStream(bytes);
			out = new ByteArrayOutputStream();
			new GenericAWSRequestStreamHandler(AWSEntries.class, "static4").
				handleRequest(in, out, context);
			if (Arrays.equals(bytes, out.toByteArray()))
				count.increment();
				
			if ("squirrels".equals(new GenericAWSRequestHandler(
				AWSEntries.class, "static5").
				handleRequest("squirrels", context)))
				count.increment();
				
			in = new ByteArrayInputStream(bytes);
			out = new ByteArrayOutputStream();
			new GenericAWSRequestStreamHandler(AWSEntries.class, "static6").
				handleRequest(in, out, context);
			if (Arrays.equals(bytes, out.toByteArray()))
				count.increment();
			
			// Instance checks
			if ("squirrels".equals(new GenericAWSRequestHandler(
				AWSEntries.class, "instance0").
				handleRequest("squirrels", context)))
				count.increment();
				
			if ("squirrels".equals(new GenericAWSRequestHandler(
				AWSEntries.class, "instance1").
				handleRequest("squirrels", context)))
				count.increment();
			
			if ("squirrels".equals(new GenericAWSRequestHandler(
				AWSEntries.class, "instance2").
				handleRequest("squirrels", context)))
				count.increment();
			
			in = new ByteArrayInputStream(bytes);
			out = new ByteArrayOutputStream();
			new GenericAWSRequestStreamHandler(AWSEntries.class, "instance3").
				handleRequest(in, out, context);
			if (Arrays.equals(bytes, out.toByteArray()))
				count.increment();
				
			in = new ByteArrayInputStream(bytes);
			out = new ByteArrayOutputStream();
			new GenericAWSRequestStreamHandler(AWSEntries.class, "instance4").
				handleRequest(in, out, context);
			if (Arrays.equals(bytes, out.toByteArray()))
				count.increment();
				
			if ("squirrels".equals(new GenericAWSRequestHandler(
				AWSEntries.class, "instance5").
				handleRequest("squirrels", context)))
				count.increment();
				
			in = new ByteArrayInputStream(bytes);
			out = new ByteArrayOutputStream();
			new GenericAWSRequestStreamHandler(AWSEntries.class, "instance6").
				handleRequest(in, out, context);
			if (Arrays.equals(bytes, out.toByteArray()))
				count.increment();
		}
		catch (IOException e)
		{
			throw new Throwable("Read/write error during test.", e);
		}
	}
}

