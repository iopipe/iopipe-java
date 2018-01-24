package com.iopipe;

import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import javax.json.JsonObject;

/**
 * This represents a single test which is to be extended and .
 *
 * @since 2018/01/23
 */
public abstract class Single
{
	/** The engine this is running under */
	protected final Engine engine;
	
	/** The base name of the test. */
	protected final String basename;
	
	/**
	 * Initializes the base test.
	 *
	 * @param __e The engine this is running under.
	 * @param __n The base name of the test.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/23
	 */
	public Single(Engine __e, String __n)
		throws NullPointerException
	{
		if (__e == null || __n == null)
			throw new NullPointerException();
		
		this.engine = __e;
		this.basename = __n;
	}
	
	/**
	 * Code to run at the end of each test.
	 *
	 * @since 2018/01/23
	 */
	public abstract void endCommon();
	
	/**
	 * Code to run at the end of a mocked test.
	 *
	 * @since 2018/01/23
	 */
	public abstract void endMocked();
	
	/**
	 * Code to run at the end of a service test.
	 *
	 * @since 2018/01/23
	 */
	public abstract void endRemote();
	
	/**
	 * This is called when a mocked request was made.
	 *
	 * @param __r The request which was made.
	 * @since 2018/01/23
	 */
	public abstract void mockedRequest(RemoteRequest __r);
	
	/**
	 * This is called when the remote end returns a result.
	 *
	 * @param __r The result returned from the remote end.
	 * @since 2018/01/23
	 */
	public abstract void remoteResult(RemoteResult __r);
	
	/**
	 * Runs the test.
	 *
	 * @param __e The execution context.
	 * @throws Throwable On any exception, which is fatal.
	 * @since 2018/01/23
	 */
	public abstract void run(IOpipeExecution __e)
		throws Throwable;
	
	/**
	 * Returns the full name of the test.
	 *
	 * @return The test full name.
	 * @since 2018/01/23
	 */
	public final String fullName()
	{
		return this.engine.baseName() + "-" + this.basename;
	}
	
	/**
	 * Returns if the mocked test requires a valid token or an invalid one.
	 *
	 * @return Is a valid token required?
	 * @since 2018/01/12
	 */
	public boolean mockedTokenValidity()
	{
		return true;
	}
}

