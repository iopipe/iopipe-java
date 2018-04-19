package com.iopipe;

import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import javax.json.JsonObject;
import org.junit.jupiter.api.Assertions;

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
		
		// Make sure all names are valid ASCII
		__n = __n.replaceAll("[^a-zA-Z0-9-_]", "X");
		
		// Do not let test names be really long
		int cplen = __n.codePointCount(0, __n.length());
		if (cplen > 24)
			this.basename = __n.substring(0, __n.offsetByCodePoints(0, 24));
		else
			this.basename = __n;
	}
	
	/**
	 * Code to run at the end of each test.
	 *
	 * @since 2018/01/23
	 */
	public abstract void end();
	
	/**
	 * This is called when a request was made.
	 *
	 * @param __r The request which was made.
	 * @since 2018/01/23
	 */
	public abstract void remoteRequest(WrappedRequest __r);
	
	/**
	 * This is called when the remote end returns a result.
	 *
	 * @param __r The result returned from the remote end.
	 * @since 2018/01/23
	 */
	public abstract void remoteResult(WrappedResult __r);
	
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
	 * Asserts that the given condition is false.
	 *
	 * @param __v The value.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/23
	 */
	public final void assertFalse(BooleanValue __v)
		throws NullPointerException
	{
		if (__v == null)
			throw new NullPointerException();
		
		Assertions.assertFalse(__v.get(), __testName(__v.name()));
	}
	
	/**
	 * Asserts that the given condition is true.
	 *
	 * @param __v The value.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/23
	 */
	public final void assertTrue(BooleanValue __v)
		throws NullPointerException
	{
		if (__v == null)
			throw new NullPointerException();
		
		Assertions.assertTrue(__v.get(), __testName(__v.name()));
	}
	
	/**
	 * Asserts that the given condition is equal.
	 *
	 * @param __exp The expected value.
	 * @param __v The actual value.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/24
	 */
	public final void assertEquals(boolean __exp, BooleanValue __v)
		throws NullPointerException
	{
		if (__v == null)
			throw new NullPointerException();
		
		Assertions.assertEquals(__exp, __v.get(), __testName(__v.name()));
	}
	
	/**
	 * Asserts that the given condition is equal.
	 *
	 * @param __exp The expected value.
	 * @param __v The actual value.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/26
	 */
	public final void assertEquals(int __exp, IntegerValue __v)
		throws NullPointerException
	{
		if (__v == null)
			throw new NullPointerException();
		
		Assertions.assertEquals(__exp, __v.get(), __testName(__v.name()));
	}
	
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
	 * Modifies the config for the test to handle test specific settings.
	 *
	 * @param __cb The configuration to modify.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/24
	 */
	public void modifyConfig(IOpipeConfigurationBuilder __cb)
		throws NullPointerException
	{
		if (__cb == null)
			throw new NullPointerException();
	}
	
	/**
	 * Returns the constructed test name so it is easier to find tests.
	 *
	 * @param __name The input name.
	 * @return The full name of the actual test.
	 * @since 2018/01/23
	 */
	private final String __testName(String __name)
	{
		return this.fullName() + "-" + __name;
	}
}

