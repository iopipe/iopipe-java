package com.iopipe;

import com.iopipe.http.RemoteRequest;
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
	 * Runs the test.
	 *
	 * @param __e The execution context.
	 * @throws Throwable On any exception, which is fatal.
	 * @since 2018/01/23
	 */
	public abstract void run(IOpipeExecution __e)
		throws Throwable;
	
	/**
	 * This is called when a mocked request was made.
	 *
	 * @param __r The request which was made.
	 * @since 2018/01/23
	 */
	public abstract void mockedRequest(RemoteRequest __r);
	
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
}

