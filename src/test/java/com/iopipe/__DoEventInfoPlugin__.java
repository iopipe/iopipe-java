package com.iopipe;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.CloudFrontEvent;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.amazonaws.services.lambda.runtime.events.KinesisFirehoseEvent;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.simpleworkflow.flow.JsonDataConverter;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import com.iopipe.IOpipeMeasurement;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.function.Supplier;
import java.util.Map;
import java.util.regex.Pattern;
import javax.json.JsonNumber;
import javax.json.JsonString;
import javax.json.JsonValue;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This tests that the event info plugin detects the input event sources for
 * object correctly.
 *
 * @since 2018/04/16
 */
class __DoEventInfoPlugin__
	extends Single
{
	/** Logging. */
	private static final Logger _LOGGER =
		LogManager.getLogger(__DoEventInfoPlugin__.class);
	
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
		super(__e, "eventinfo-" + __type, __input);
		
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
		
		_LOGGER.debug("end()");
		throw new Error("TODO");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/04/18
	 */
	@Override
	public void modifyConfig(IOpipeConfigurationBuilder __cb)
		throws NullPointerException
	{
		if (__cb == null)
			throw new NullPointerException();
		
		__cb.setPluginEnabled("event-info", true);
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
		
		_LOGGER.debug("remoteRequest()");
		//throw new Error("TODO");
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
		return __DoEventInfoPlugin__.<APIGatewayProxyRequestEvent>__convert(
			APIGatewayProxyRequestEvent.class, "eventinfo_apigateway.json");
	}
	
	/**
	 * Creates an instance of CloudFrontEvent.
	 *
	 * @return The created input.
	 * @since 2018/04/16
	 */
	public static Object makeCloudFrontEvent()
	{
		return __DoEventInfoPlugin__.<CloudFrontEvent>__convert(
			CloudFrontEvent.class, "eventinfo_cloudfront.json");
	}
	
	/**
	 * Creates an instance of KinesisEvent.
	 *
	 * @return The created input.
	 * @since 2018/04/16
	 */
	public static Object makeKinesisEvent()
	{
		return __DoEventInfoPlugin__.<KinesisEvent>__convert(
			KinesisEvent.class, "eventinfo_kinesis.json");
	}
	
	/**
	 * Creates an instance of KinesisFirehoseEvent.
	 *
	 * @return The created input.
	 * @since 2018/04/16
	 */
	public static Object makeKinesisFirehoseEvent()
	{
		return __DoEventInfoPlugin__.<KinesisFirehoseEvent>__convert(
			KinesisFirehoseEvent.class, "eventinfo_firehose.json");
	}
	
	/**
	 * Creates an instance of S3Event.
	 *
	 * @return The created input.
	 * @since 2018/04/16
	 */
	public static Object makeS3Event()
	{
		return __DoEventInfoPlugin__.<S3EventNotification>__convert(
			S3EventNotification.class, "eventinfo_s3.json");
	}
	
	/**
	 * Creates an instance of ScheduledEvent.
	 *
	 * @return The created input.
	 * @since 2018/04/16
	 */
	public static Object makeScheduledEvent()
	{
		return __DoEventInfoPlugin__.<ScheduledEvent>__convert(
			ScheduledEvent.class, "eventinfo_scheduled.json");
	}
	
	/**
	 * Creates an instance of SNSEvent.
	 *
	 * @return The created input.
	 * @since 2018/04/16
	 */
	public static Object makeSNSEvent()
	{
		return __DoEventInfoPlugin__.<SNSEvent>__convert(
			SNSEvent.class, "eventinfo_sns.json");
	}
	
	/**
	 * Converts a resource to an object.
	 *
	 * @param <T> The type of class to convert to.
	 * @param __cl The type of class to convert to.
	 * @param __rc The resource to convert.
	 * @return The converted data.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/04/18
	 */
	public static <T> T __convert(Class<T> __cl, String __rc)
		throws NullPointerException
	{
		if (__cl == null || __rc == null)
			throw new NullPointerException();
		
		// Setup mapper and allow it to be case insensitive, because the
		// event POJOs differ from our input JSON and it would be preferred to
		// keep them untouched
		ObjectMapper map = new ObjectMapper();
		map.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
		
		// Need to handle JODA time events correctly
		map.registerModule(new JodaModule());
		
		// Setup converter
		JsonDataConverter jdc = new JsonDataConverter(map);
		
		// Debug conversion first
		T rv = __cl.cast(jdc.<T>fromData(
			__DoEventInfoPlugin__.__linesFromResource(__rc), __cl));
		_LOGGER.debug("Testing POJO ({}) with {}.", __cl, rv);
		
		// Convert
		return rv;
	}
	
	/**
	 * Reads lines from a resource.
	 *
	 * @param __rc The resource to read.
	 * @return The lines from the resource.
	 * @throws NullPointerException On null arguments or the resource does not
	 * exist.
	 * @throws RuntimeException If it could not be read.
	 * @since 2018/04/18
	 */
	private static String __linesFromResource(String __rc)
		throws NullPointerException, RuntimeException
	{
		if (__rc == null)
			throw new NullPointerException();
		
		try (InputStream in = __DoEventInfoPlugin__.class.getResourceAsStream(
			__rc);
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(isr))
		{
			StringBuilder sb = new StringBuilder();
			
			String ln;
			while (null != (ln = br.readLine()))
			{
				sb.append(ln);
				sb.append('\n');
			}
			
			return sb.toString();
		}
		catch (IOException e)
		{
			throw new RuntimeException("Could not read resource.", e);
		}
	}
}
