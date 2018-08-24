package com.iopipe;

import com.iopipe.elsewhere.Classes;
import com.iopipe.generic.EntryPoint;
import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import java.lang.invoke.MethodHandle;

/**
 * Checks to make sure entry points are working.
 *
 * @since 2018/08/17
 */
class __DoGenericEntryPoint__
	extends Single
{
	/** The methods to be tested for entry points. */
	private static Object[] _METHODS =
		{
			Classes.PACKAGE_PRIVATE, "instancePrivate",
			Classes.PACKAGE_PRIVATE, "instancePackagePrivate",
			Classes.PACKAGE_PRIVATE, "instanceProtected",
			Classes.PACKAGE_PRIVATE, "instancePublic",
			Classes.PACKAGE_PRIVATE, "staticPrivate",
			Classes.PACKAGE_PRIVATE, "staticPackagePrivate",
			Classes.PACKAGE_PRIVATE, "staticProtected",
			Classes.PACKAGE_PRIVATE, "staticPublic",
			Classes.PUBLIC, "instancePrivate",
			Classes.PUBLIC, "instancePackagePrivate",
			Classes.PUBLIC, "instanceProtected",
			Classes.PUBLIC, "instancePublic",
			Classes.PUBLIC, "staticPrivate",
			Classes.PUBLIC, "staticPackagePrivate",
			Classes.PUBLIC, "staticProtected",
			Classes.PUBLIC, "staticPublic",
		};
	
	/** Sent with no exception? */
	protected final BooleanValue noerror =
		new BooleanValue("noerror");
		
	/** Got a result from the server okay? */
	protected final BooleanValue remoterecvokay =
		new BooleanValue("remoterecvokay");
	
	/** Methods executed. */
	protected final IntegerValue count =
		new IntegerValue("count");
	
	/**
	 * Constructs the test.
	 *
	 * @param __e The owning engine.
	 * @since 2018/08/17
	 */
	__DoGenericEntryPoint__(Engine __e)
	{
		super(__e, "entrypoint");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/08/17
	 */
	@Override
	public void end()
	{
		super.assertTrue(this.remoterecvokay);
		super.assertTrue(this.noerror);
		
		super.assertEquals(_METHODS.length / 2, this.count);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/08/17
	 */
	@Override
	public void remoteRequest(WrappedRequest __r)
	{
		StandardPushEvent event = (StandardPushEvent)__r.event;
		
		// It is invalid if there is an error
		if (!event.hasError())
			this.noerror.set(true);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/08/17
	 */
	@Override
	public void remoteResult(WrappedResult __r)
	{
		if (__Utils__.isResultOkay(__r.result))
			this.remoterecvokay.set(true);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/08/17
	 */
	@Override
	public void run(IOpipeExecution __e)
		throws Throwable
	{
		for (int i = 0, n = _METHODS.length; i < n; i += 2)
		{
			String form = (((Class<?>)_METHODS[i]).getName()) + _METHODS[i + 1];
			
			try
			{
				EntryPoint ep = EntryPoint.newAWSEntryPoint((Class)_METHODS[i],
					(String)_METHODS[i + 1]);
				
				if ("squirrel".equals(ep.handleWithNewInstance().invoke("SQUIRREL")))
					this.count.incrementAndGet();
			}
			catch (Throwable t)
			{
				throw new RuntimeException(t);
			}
		}
	}
}

