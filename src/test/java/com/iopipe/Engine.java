package com.iopipe;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.CloudFrontEvent;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.amazonaws.services.lambda.runtime.events.KinesisFirehoseEvent;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.simpleworkflow.flow.JsonDataConverter;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.iopipe.CustomMetric;
import com.iopipe.elsewhere.SimplePOJO;
import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import com.iopipe.IOpipeMeasurement;
import com.iopipe.plugin.eventinfo.APIGatewayDecoder;
import com.iopipe.plugin.eventinfo.CloudFrontDecoder;
import com.iopipe.plugin.eventinfo.EventInfoDecoder;
import com.iopipe.plugin.eventinfo.FirehoseDecoder;
import com.iopipe.plugin.eventinfo.KinesisDecoder;
import com.iopipe.plugin.eventinfo.S3Decoder;
import com.iopipe.plugin.eventinfo.ScheduledDecoder;
import com.iopipe.plugin.eventinfo.SNSDecoder;
import com.iopipe.plugin.eventinfo.SQSDecoder;
import com.iopipe.plugin.IOpipePlugin;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.ServiceLoader;
import java.util.Set;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import org.junit.jupiter.api.DynamicTest;
import org.pmw.tinylog.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test engine which performs the bulk of the logic for executing
 * tests.
 *
 * @since 2018/01/23
 */
public abstract class Engine
{
	/** The tests which should be ran through each engine. */
	private static final SingleTestConstructor[] _RUN_TESTS =
		new SingleTestConstructor[]
		{
			__DoEmptyMethod__::new,
			__DoThrowException__::new,
			(__e) -> new __DoTracePlugin__(__e, true, false),
			(__e) -> new __DoTracePlugin__(__e, false, false),
			(__e) -> new __DoTracePlugin__(__e, true, true),
			(__e) -> new __DoTracePlugin__(__e, false, true),
			__DoTimeOut__::new,
			__DoInvalidToken__::new,
			__DoCustomMetric__::new,
			(__e) -> new __DoPluginTest__(__e, true),
			(__e) -> new __DoPluginTest__(__e, false),
			__DoProfilerPlugin__::new,
			(__e) -> new __DoLabel__(__e, true, "squirrels"),
			(__e) -> new __DoLabel__(__e, true, "\uD83C\uDF3A\uD83C\uDF3A" +
				"\uD83C\uDF3A\uD83C\uDF3A\uD83C\uDF3A\uD83C\uDF3A" +
				"\uD83C\uDF3A\uD83C\uDF3A\uD83C\uDF3A\uD83C\uDF3A" +
				"\uD83C\uDF3A\uD83C\uDF3A\uD83C\uDF3A\uD83C\uDF3A" +
				"\uD83C\uDF3A\uD83C\uDF3A\uD83C\uDF3A\uD83C\uDF3A"),
			(__e) -> new __DoLabel__(__e, false, String.join("",
				Collections.nCopies(IOpipeConstants.NAME_CODEPOINT_LIMIT + 32,
				"a"))),
			__DoLongValueCustomMetric__::new,
			__DoLongNameCustomMetric__::new,
			__DoDiskReportCheck__::new,
			__DoColdStartAutoLabel__::new,
			__DoRecursive__::new,
			__DoGenericEntryPoint__::new,
			__DoGenericStreamHandler__::new,
			__DoGenericHandler__::new,
			
			// Event Info
			(__e) -> new __DoEventInfoPlugin__(__e,
				__DoEventInfoPlugin__::makeAPIGatewayProxyRequestEvent,
				new APIGatewayDecoder()),
			(__e) -> new __DoEventInfoPlugin__(__e,
				__DoEventInfoPlugin__::makeCloudFrontEvent,
				new CloudFrontDecoder()),
			(__e) -> new __DoEventInfoPlugin__(__e,
				__DoEventInfoPlugin__::makeKinesisEvent,
				new KinesisDecoder()),
			(__e) -> new __DoEventInfoPlugin__(__e,
				__DoEventInfoPlugin__::makeKinesisFirehoseEvent,
				new FirehoseDecoder()),
			(__e) -> new __DoEventInfoPlugin__(__e,
				__DoEventInfoPlugin__::makeS3Event,
				new S3Decoder()),
			(__e) -> new __DoEventInfoPlugin__(__e,
				__DoEventInfoPlugin__::makeScheduledEvent,
				new ScheduledDecoder()),
			(__e) -> new __DoEventInfoPlugin__(__e,
				__DoEventInfoPlugin__::makeSNSEvent,
				new SNSDecoder()),
			(__e) -> new __DoEventInfoPlugin__(__e,
				__DoEventInfoPlugin__::makeSQSEvent,
				new SQSDecoder()),
			
			// Object translator
			(__e) -> new __DoGenericObjectTranslate__(__e,
				"eventinfo_apigateway.json",
				APIGatewayProxyRequestEvent.class),
			(__e) -> new __DoGenericObjectTranslate__(__e,
				"eventinfo_cloudfront.json",
				CloudFrontEvent.class),
			(__e) -> new __DoGenericObjectTranslate__(__e,
				"eventinfo_kinesis.json",
				KinesisEvent.class),
			(__e) -> new __DoGenericObjectTranslate__(__e,
				"eventinfo_firehose.json",
				KinesisFirehoseEvent.class),
			(__e) -> new __DoGenericObjectTranslate__(__e,
				"eventinfo_s3.json",
				S3EventNotification.class),
			(__e) -> new __DoGenericObjectTranslate__(__e,
				"eventinfo_scheduled.json",
				ScheduledEvent.class),
			(__e) -> new __DoGenericObjectTranslate__(__e,
				"eventinfo_sns.json",
				SNSEvent.class),
			(__e) -> new __DoGenericObjectTranslate__(__e,
				"eventinfo_sqs.json",
				SQSEvent.class),
			(__e) -> new __DoGenericObjectTranslate__(__e,
				"simplepojo.json",
				SimplePOJO.class),
			(__e) -> new __DoGenericObjectTranslate__(__e,
				"string.json",
				String.class),
			(__e) -> new __DoGenericObjectTranslate__(__e,
				"integer.json",
				Integer.class),
			(__e) -> new __DoGenericObjectTranslate__(__e,
				"boolean.json",
				Boolean.class),
		};
	
	/** The base name for this engine. */
	protected final String basename;
	
	/**
	 * Initializes the base engine.
	 *
	 * @param __bn The base name of the engine.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/23
	 */
	public Engine(String __bn)
		throws NullPointerException
	{
		if (__bn == null)
			throw new NullPointerException();
		
		this.basename = __bn;
	}
	
	/**
	 * Generates a new configuration to use for this test.
	 *
	 * @param __s The test being ran, may be used by the engine to change
	 * the connection factory to monitor requests as needed.
	 * @return The generated configuration.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/23
	 */
	protected abstract IOpipeConfigurationBuilder generateConfig(Single __s)
		throws NullPointerException;
	
	/**
	 * Returns the base name of this engine.
	 *
	 * @return The engine base name
	 * @since 2018/01/23
	 */
	public final String baseName()
	{
		return this.basename;
	}
	
	/**
	 * Runs the specified test.
	 *
	 * @param __s The test to run.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/23
	 */
	private final void __run(Single __s)
		throws NullPointerException
	{
		if (__s == null)
			throw new NullPointerException("NARG");
		
		Logger.debug("..");
		Logger.debug("..");
		Logger.debug("..");
		Logger.debug(">>> BEGIN TEST: {}", __s.fullName());
		
		// Run it
		IOpipeService sv = null;
		
		// Obtain configuration to use
		IOpipeConfigurationBuilder confbld = this.generateConfig(__s);
		
		// There may be plugins configured in the environment or the test
		// system, so disable all of them by default
		for (IOpipePlugin p : ServiceLoader.<IOpipePlugin>load(
			IOpipePlugin.class))
			confbld.setPluginEnabled(p.name(), false);
		
		// Modify the configuration as needed by some tests
		__s.modifyConfig(confbld);
		
		// Wrap the connection factory with one where we can tunnel returned
		// results from the remote service to our single handler
		confbld.setRemoteConnectionFactory(new __WrappedConnectionFactory__(
			__s, confbld.getRemoteConnectionFactory()));
		
		// Setup service
		sv = new IOpipeService(confbld.build());
		
		// Has the function body been entered?
		BooleanValue enteredbody = new BooleanValue("enteredbody");
		BooleanValue mockedexception = new BooleanValue("mockedexception");
		
		// Execute service
		try
		{
			sv.<Object>run(new MockContext(__s.fullName()), (__exec) ->
				{
					// Body entered, which should always happen no matter
					// what
					enteredbody.set(true);
				
					// Exceptions could be thrown
					try
					{
						// Run it
						__s.run(__exec);
					
						// No exception expected
						__s.assertFalse(mockedexception);
					}
	
					// Threw an exception, which might not be in error
					catch (Throwable t)
					{
						// Mock exception was thrown, treat that as valid
						if (t instanceof MockException)
						{
							mockedexception.set(true);
							__s.assertTrue(mockedexception);
						
							// Throw it again so an error is actually generated
							throw (MockException)t;
						}
		
						// Otherwise, this is not good
						else
							throw new RuntimeException(t);
					}
				
					return null;
				}, __s.input());
		}
		
		// Ignore the mock exception
		catch (MockException e)
		{
		}
		
		// This hopefully should not happen
		catch (RuntimeException|Error e)
		{
			Logger.error("Test threw an exception!");
			e.printStackTrace();
			
			throw e;
		}
			
		// The body must have always been entered
		__s.assertTrue(enteredbody);
		
		// Common end of service
		__s.end();
		
		Logger.debug("<<< END TEST  : {}", __s.fullName());
	}
	
	/**
	 * Generates test to be executed according to the specified engine.
	 *
	 * @param __con The constructor for the test engine which are run under
	 * tests.
	 * @return The iterable set of tests to run.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/23
	 */
	public static Iterable<DynamicTest> generateTests(Supplier<Engine> __con)
		throws NullPointerException
	{
		if (__con == null)
			throw new NullPointerException();
		
		Collection<DynamicTest> rv = new ArrayList<>();
		
		// Go through all constructors and setup all tests
		for (SingleTestConstructor constructor : Engine._RUN_TESTS)
		{
			// Setup engine instance
			Engine engine = __con.get();
			
			// Setup instance since it needs to be known the test details
			Single instance = constructor.construct(engine);
			
			// Add test
			rv.add(DynamicTest.dynamicTest(instance.fullName(),
				() -> engine.__run(instance)));
		}
		
		return rv;
	}
	
	/**
	 * Constructor for a single test.
	 *
	 * @param <C> The type of test to construct.
	 * @since 2018/01/23
	 */
	@FunctionalInterface
	public static interface SingleTestConstructor<C extends Single>
	{
		/**
		 * Constructs an instance of the given class.
		 *
		 * @param __e The owning engine.
		 * @return The instance of the test.
		 * @since 2018/01/23
		 */
		public abstract C construct(Engine __e);
	}
}

