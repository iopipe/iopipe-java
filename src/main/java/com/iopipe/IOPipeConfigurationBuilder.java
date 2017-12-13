package com.iopipe;

/**
 * This class is used to initialize instances of {@link IOPipeConfiguration}
 *
 * @since 2017/12/13
 */
public class IOPipeConfigurationBuilder
{
	/**
	 * This lock is used to prevent situations such as mismatched values when
	 * multiple threads are setting parameters or building configurations.
	 */
	protected final Object lock =
		new Object();
	
	/**
	 * This constructs an instance of the configuration settings from the
	 * requested configuration values.
	 *
	 * @return An immutable instance of this configuration.
	 * @throws IllegalArgumentException If a specified configuration setting
	 * is not valid.
	 * @since 2017/12/13
	 */
	public final IOPipeConfiguration build()
		throws IllegalArgumentException
	{
		synchronized (this.lock)
		{
			throw new Error("TODO");
		}
	}
	
	/**
	 * This loads the configuration settings from the host environment
	 * variables if they have not already been set.
	 *
	 * @since 2017/12/13
	 */
	public final void loadFromEnvironment()
	{
		synchronized (this.lock)
		{
			throw new Error("TODO");
		}
	}
	
	/**
	 * Sets whether the IOPipe service to to be enabled.
	 *
	 * @param __enabled If {@code true} then the IOPipe service is to be used,
	 * otherwise any requests will NOT use the service.
	 * @since 2017/12/13
	 */
	public final void setEnabled(boolean __enabled)
	{
		synchronized (this.lock)
		{
			throw new Error("TODO");
		}
	}
	
	/**
	 * Sets the project token.
	 *
	 * @param __token The token which specifies the project to measure
	 * the statistics for.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/13
	 */
	public final void setProjectToken(String __token)
		throws NullPointerException
	{
		if (__token == null)
			throw new NullPointerException();
		
		synchronized (this.lock)
		{
			throw new Error("TODO");
		}
	}
}

