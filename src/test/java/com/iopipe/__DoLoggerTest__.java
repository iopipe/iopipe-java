package com.iopipe;

import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import com.iopipe.http.RequestType;
import com.iopipe.plugin.logger.LoggerUtil;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Map;
import javax.json.JsonString;
import javax.json.JsonNumber;
import javax.json.JsonValue;

/**
 * Tests that the base logger framework is operational.
 *
 * @since 2018/09/25
 */
class __DoLoggerTest__
	extends Single
{
	/** Is the plugin enabled? */
	protected final boolean enabled;
	
	/** Sent with no exception? */
	protected final BooleanValue noerror =
		new BooleanValue("noerror");
		
	/** Got a result from the server okay? */
	protected final BooleanValue remoterecvokay =
		new BooleanValue("remoterecvokay");
	
	/** Was the logger plugin specified? */
	protected final BooleanValue loggerpluginspecified =
		new BooleanValue("loggerpluginspecified");
		
	/** The number of lines in the file. */
	protected final IntegerValue lines =
		new IntegerValue("lines");
	
	/** Was a post made? */
	protected final BooleanValue gotpost =
		new BooleanValue("gotpost");
	
	/** Was a put made? */
	protected final BooleanValue gotput =
		new BooleanValue("gotput");
	
	/** Logger has all the fields. */
	protected final BooleanValue hassignerpostfields =
		new BooleanValue("hassignerpostfields");
	
	/** Has uploads? */
	protected final BooleanValue hasuploads =
		new BooleanValue("hasuploads");
	
	/** Has auto label? */
	protected final BooleanValue hasautolabel =
		new BooleanValue("hasautolabel");
	
	/**
	 * Constructs the test.
	 *
	 * @param __e The owning engine.
	 * @param __enabled Is the plugin enabled?
	 * @since 2018/09/25
	 */
	__DoLoggerTest__(Engine __e, boolean __enabled)
	{
		super(__e, "logger-" + __enabled);
		
		this.enabled = __enabled;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/09/25
	 */
	@Override
	public void end()
	{
		super.assertTrue(this.remoterecvokay);
		super.assertTrue(this.noerror);
		
		super.assertEquals(this.enabled, this.gotpost);
		super.assertEquals(this.enabled, this.gotput);
		super.assertEquals(this.enabled, this.hassignerpostfields);
		super.assertEquals(this.enabled, this.hasuploads);
		super.assertEquals(this.enabled, this.hasautolabel);
		
		super.assertEquals((this.enabled ? 12 : 0), this.lines);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/09/26
	 */
	@Override
	public void modifyConfig(IOpipeConfigurationBuilder __cb)
		throws NullPointerException
	{
		if (__cb == null)
			throw new NullPointerException();
		
		__cb.setPluginEnabled("logger", this.enabled);
		__cb.setTimeOutWindow(0);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/09/25
	 */
	@Override
	public void remoteRequest(WrappedRequest __r)
	{
		Event rawevent = __r.event;
		
		// Data being uploaded
		if (rawevent instanceof PutEvent)
		{
			if (__r.type == RequestType.PUT)
				this.gotput.set(true);
			
			try (BufferedReader br = new BufferedReader(new InputStreamReader(
				new ByteArrayInputStream(((PutEvent)rawevent).data()), "utf-8")))
			{
				for (;;)
				{
					String ln = br.readLine();
					
					if (ln == null)
						break;
					
					this.lines.increment();
				}
			}
			catch (IOException e)
			{
			}
		}
		
		// A request made by the signer
		else if (rawevent instanceof SignerEvent)
		{
			SignerEvent event = (SignerEvent)rawevent;
			
			// Post was made?
			if (__r.type == RequestType.POST)
				this.gotpost.set(true);
			
			// Needs to have all the fields
			if (event.arn != null &&
				event.requestid != null &&
				event.timestamp > Long.MIN_VALUE &&
				event.extension != null)
				this.hassignerpostfields.set(true);
		}
		
		// Standard push event
		else if (rawevent instanceof StandardPushEvent)
		{
			StandardPushEvent event = (StandardPushEvent)rawevent;
			
			// It is invalid if there is an error
			if (!event.hasError())
				this.noerror.set(true);
			
			// See if the logger plugin was specified
			StandardPushEvent.Plugin plugin = event.plugins.get("logger");
			if (plugin != null)
			{
				this.loggerpluginspecified.set(true);
				
				// There must also be uploads
				if (plugin.uploads != null && !plugin.uploads.isEmpty())
					this.hasuploads.set(true);
			}
			
			if (event.labels.contains("@iopipe/plugin-logger"))
				this.hasautolabel.set(true);
		}
		
		// Do not know what this is
		else
			throw new RuntimeException("Unknown event type in logger?");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/09/25
	 */
	@Override
	public void remoteResult(WrappedResult __r)
	{
		if (__Utils__.isResultOkay(__r.result))
			this.remoterecvokay.set(true);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/09/25
	 */
	@Override
	public void run(IOpipeExecution __e)
		throws Throwable
	{
		LoggerUtil.log(FakeLevel.ENUM, "test",
			"Squirrels are quick!".toCharArray());
		LoggerUtil.log(1520541000000L, FakeLevel.ENUM, "test",
			"Squirrels are cautious!".toCharArray());
		LoggerUtil.log(FakeLevel.ENUM, "test",
			"Squirrels are beautiful!".toCharArray(), 2, 17);
		LoggerUtil.log(1520541000000L, FakeLevel.ENUM, "test",
			"Squirrels are sleepy!".toCharArray(), 7, 14);
		LoggerUtil.log(FakeLevel.ENUM, "test",
			"Squirrels are cute!");
		LoggerUtil.log(1520541000000L, FakeLevel.ENUM, "test",
			"Squirrels are adorable!");
		LoggerUtil.log("STRING", "test",
			"Squirrels are curious!".toCharArray());
		LoggerUtil.log(1520541000000L, "STRING", "test",
			"Squirrels are energetic!".toCharArray());
		LoggerUtil.log("STRING", "test",
			"Squirrels are loving!".toCharArray(), 0, 13);
		LoggerUtil.log(1520541000000L, "STRING", "test",
			"Squirrels are hungry!".toCharArray(), 19, 2);
		LoggerUtil.log("STRING", "test",
			"Squirrels are sweet!");
		LoggerUtil.log(1520541000000L, "STRING", "test",
			"Squirrels are fluffy!");
	}
	
	/**
	 * Represents a fake enum based logging level.
	 *
	 * @since 2018/09/26
	 */
	public static enum FakeLevel
	{
		/** The constant. */
		ENUM,
		
		/** End. */
		;
	}
}

