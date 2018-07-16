package com.iopipe;

import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import com.iopipe.http.RequestType;
import com.iopipe.plugin.trace.TraceExecution;
import com.iopipe.plugin.trace.TraceMeasurement;
import com.iopipe.plugin.trace.TracePlugin;
import com.iopipe.plugin.trace.TraceUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.List;
import java.util.Map;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

/**
 * This tests the trace plugin to ensure that it operates and generates trace
 * results.
 *
 * @since 2018/02/07
 */
class __DoProfilerPlugin__
	extends Single
{
	/** Random element count. */
	public static final int RANDOM_COUNT =
		100_000;
	
	/** Maximum entries for selection sort (it is really slow). */
	public static final int RANDOM_COUNT_SELECT_CAP =
		20_000;
	
	/** Marking interval. */
	public static final int MARK =
		1_000;
	
	/** Sent with no exception? */
	protected final BooleanValue noerror =
		new BooleanValue("noerror");
		
	/** Got a result from the server okay? */
	protected final BooleanValue remoterecvokay =
		new BooleanValue("remoterecvokay");
	
	/** Was the profiler plugin specified? */
	protected final BooleanValue profilerpluginspecified =
		new BooleanValue("profilerpluginspecified");
	
	/** Was a post made? */
	protected final BooleanValue gotpost =
		new BooleanValue("gotpost");
	
	/** Was a put made? */
	protected final BooleanValue gotput =
		new BooleanValue("gotput");
	
	/** Profiler has all the fields. */
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
	 * @since 2018/02/07
	 */
	__DoProfilerPlugin__(Engine __e)
	{
		super(__e, "profilerplugin");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/02/07
	 */
	@Override
	public void end()
	{
		super.assertTrue(this.remoterecvokay);
		super.assertTrue(this.noerror);
		super.assertTrue(this.profilerpluginspecified);
		super.assertTrue(this.gotpost);
		super.assertTrue(this.gotput);
		super.assertTrue(this.hassignerpostfields);
		super.assertTrue(this.hasuploads);
		super.assertTrue(this.hasautolabel);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/02/07
	 */
	@Override
	public void modifyConfig(IOpipeConfigurationBuilder __cb)
		throws NullPointerException
	{
		if (__cb == null)
			throw new NullPointerException();
		
		__cb.setPluginEnabled("profiler", true);
		__cb.setTimeOutWindow(0);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/02/07
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
			
			// See if the trace plugin was specified
			StandardPushEvent.Plugin plugin = event.plugins.get("profiler");
			if (plugin != null)
			{
				this.profilerpluginspecified.set(true);
				
				// There must also be uploads
				if (plugin.uploads != null && !plugin.uploads.isEmpty())
					this.hasuploads.set(true);
			}
			
			if (event.labels.contains("@iopipe/plugin-profiler"))
				this.hasautolabel.set(true);
		}
		
		// Do not know what this is
		else
			throw new RuntimeException("Unknown event type in profiler?");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/02/07
	 */
	@Override
	public void remoteResult(WrappedResult __r)
	{
		if (__Utils__.isResultOkay(__r.result))
			this.remoterecvokay.set(true);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/02/07
	 */
	@Override
	public void run(IOpipeExecution __e)
		throws Throwable
	{
		// Generate a bunch of random numbers
		Random rand = new Random(0x537175697272656CL);
		List<Long> unsorted = new ArrayList<>(RANDOM_COUNT);
		for (int i = 0; i < RANDOM_COUNT; i++)
			unsorted.add(rand.nextLong());
		
		// Selection sort
		Thread sel = new Thread(() ->
			{
				List<Long> result = new ArrayList<>(RANDOM_COUNT);
				
				// Selection sort is really slow so limit the maximum size
				for (int i = 0; i < RANDOM_COUNT_SELECT_CAP &&
					i < RANDOM_COUNT; i++)
					result.add(unsorted.get(i));
					
				for (int i = 0, n = result.size(); i < n; i++)
				{
					if (false && ((i + 1) % MARK) == 0)
						System.err.printf("SEL %d%n", i);
					
					Long v = result.get(i);
					
					int lowdx = i;
					Long lowva = v;
					for (int j = i; j < n; j++)
					{
						Long temp = result.get(j);
						if (temp.compareTo(lowva) < 0)
						{
							lowdx = j;
							lowva = temp;
						}
					}
					
					if (i != lowdx)
					{
						result.set(i, lowva);
						result.set(lowdx, v);
					}
				}
			}, "SelectionSort");
		sel.start();
		
		// Binary-ish insertion sort
		Thread ins = new Thread(() ->
			{
				List<Long> result = new ArrayList<>(RANDOM_COUNT);
				
				int dxcount = 0;
				for (Long l : unsorted)
				{
					// Status debug
					int dxat = dxcount++;
					if (false && ((dxat + 1) % MARK) == 0)
						System.err.printf("BIN %d%n", dxat);
					
					int pos = Collections.<Long>binarySearch(result, l);
					if (pos < 0)
						pos = (-pos) - 1;
					result.add(pos, l);
				}
			}, "BinaryInsertionSort");
		ins.start();
		
		// Wait for selection sort to end
		for (;;)
			try
			{
				sel.join();
				break;
			}
			catch (InterruptedException e)
			{
			}
		
		// Wait for insertion sort to end
		for (;;)
			try
			{
				ins.join();
				break;
			}
			catch (InterruptedException e)
			{
			}
	}
}

