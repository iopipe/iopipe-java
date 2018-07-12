package com.iopipe;

import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import com.iopipe.IOpipeMeasurement;
import java.util.Map;
import javax.json.JsonString;
import javax.json.JsonNumber;
import javax.json.JsonValue;

/**
 * This checks that disk usage was reported.
 *
 * @since 2018/05/17
 */
class __DoDiskReportCheck__
	extends Single
{
	/** Sent with no exception? */
	protected final BooleanValue noerror =
		new BooleanValue("noerror");
		
	/** Got a result from the server okay? */
	protected final BooleanValue remoterecvokay =
		new BooleanValue("remoterecvokay");
	
	/** Is there total space? */
	protected final BooleanValue hastotal =
		new BooleanValue("hastotal");
	
	/** Is there positive non-zero total space? */
	protected final BooleanValue nonzeropositivetotal =
		new BooleanValue("nonzeropositivetotal");
	
	/** Is there used space? */
	protected final BooleanValue hasused =
		new BooleanValue("hasused");
	
	/** Is there precentage used? */
	protected final BooleanValue haspercent =
		new BooleanValue("haspercent");
	
	/**
	 * Constructs the test.
	 *
	 * @param __e The owning engine.
	 * @since 2018/05/17
	 */
	__DoDiskReportCheck__(Engine __e)
	{
		super(__e, "diskreport");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/05/17
	 */
	@Override
	public void end()
	{
		super.assertTrue(this.remoterecvokay);
		super.assertTrue(this.noerror);
		
		super.assertTrue(this.hastotal);
		super.assertTrue(this.nonzeropositivetotal);
		super.assertTrue(this.hasused);
		super.assertTrue(this.haspercent);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/05/17
	 */
	@Override
	public void remoteRequest(WrappedRequest __r)
	{
		Map<String, JsonValue> expand = __Utils__.expandObject(__r.request);
		
		// It is invalid if there is an error
		if (!__r.event.hasError())
			this.noerror.set(true);
		
		DecodedEvent.DiskUsage usage = __r.event.diskUsage();
		
		if (!Double.isNaN(usage.total))
			this.hastotal.set(true);
		
		if (!Double.isNaN(usage.used))
			this.hasused.set(true);
		
		if (!Double.isNaN(usage.percent))
			this.haspercent.set(true);
		
		if (Math.signum(usage.percent) > 0)
			this.nonzeropositivetotal.set(true);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/05/17
	 */
	@Override
	public void remoteResult(WrappedResult __r)
	{
		if (__Utils__.isResultOkay(__r.result))
			this.remoterecvokay.set(true);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/05/17
	 */
	@Override
	public void run(IOpipeExecution __e)
		throws Throwable
	{
	}
}

