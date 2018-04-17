package com.iopipe;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.CloudFrontEvent;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.amazonaws.services.lambda.runtime.events.KinesisFirehoseEvent;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import com.iopipe.IOpipeMeasurement;
import java.util.function.Supplier;
import java.util.Map;
import javax.json.JsonString;
import javax.json.JsonNumber;
import javax.json.JsonValue;

/**
 * This tests that the event info plugin detects the input event sources for
 * object correctly.
 *
 * @since 2018/04/16
 */
class __DoEventInfoPlugin__
	extends Single
{
	/** Sent with no exception? */
	protected final BooleanValue noerror =
		new BooleanValue("noerror");
		
	/** Got a result from the server okay? */
	protected final BooleanValue remoterecvokay =
		new BooleanValue("remoterecvokay");
	
	/** The expected type. */
	protected final String type;
	
	/**
	 * Constructs the test.
	 *
	 * @param __e The owning engine.
	 * @param __type The expected type.
	 * @param __input The input.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/04/16
	 */
	__DoEventInfoPlugin__(Engine __e, String __type, Supplier<Object> __input)
		throws NullPointerException
	{
		super(__e, "eventinfo-" + __type);
		
		if (__type == null)
			throw new NullPointerException();
		
		this.type = __type;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/04/16
	 */
	@Override
	public void end()
	{
		super.assertTrue(this.remoterecvokay);
		super.assertTrue(this.noerror);
		
		throw new Error("TODO");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/04/16
	 */
	@Override
	public void remoteRequest(WrappedRequest __r)
	{
		Map<String, JsonValue> expand = __Utils__.expandObject(__r.request);
		
		// It is invalid if there is an error
		if (null == __Utils__.hasError(expand))
			this.noerror.set(true);
		
		throw new Error("TODO");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/04/16
	 */
	@Override
	public void remoteResult(WrappedResult __r)
	{
		if (__Utils__.isResultOkay(__r.result))
			this.remoterecvokay.set(true);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/04/16
	 */
	@Override
	public void run(IOpipeExecution __e)
		throws Throwable
	{
		// This is handled by the plugin, so nothing needs to be done
	}
	
	/**
	 * Creates an instance of APIGatewayProxyRequestEvent.
	 *
	 * @return The created input.
	 * @since 2018/04/16
	 */
	public static Object makeAPIGatewayProxyRequestEvent()
	{
		throw new Error("TODO");
	}
	
	/**
	 * Creates an instance of CloudFrontEvent.
	 *
	 * @return The created input.
	 * @since 2018/04/16
	 */
	public static Object makeCloudFrontEvent()
	{
		throw new Error("TODO");
	}
	
	/**
	 * Creates an instance of KinesisEvent.
	 *
	 * @return The created input.
	 * @since 2018/04/16
	 */
	public static Object makeKinesisEvent()
	{
		throw new Error("TODO");
	}
	
	/**
	 * Creates an instance of KinesisFirehoseEvent.
	 *
	 * @return The created input.
	 * @since 2018/04/16
	 */
	public static Object makeKinesisFirehoseEvent()
	{
		throw new Error("TODO");
	}
	
	/**
	 * Creates an instance of S3Event.
	 *
	 * @return The created input.
	 * @since 2018/04/16
	 */
	public static Object makeS3Event()
	{
		throw new Error("TODO");
	}
	
	/**
	 * Creates an instance of ScheduledEvent.
	 *
	 * @return The created input.
	 * @since 2018/04/16
	 */
	public static Object makeScheduledEvent()
	{
		throw new Error("TODO");
	}
	
	/**
	 * Creates an instance of SNSEvent.
	 *
	 * @return The created input.
	 * @since 2018/04/16
	 */
	public static Object makeSNSEvent()
	{
		throw new Error("TODO");
	}
}

