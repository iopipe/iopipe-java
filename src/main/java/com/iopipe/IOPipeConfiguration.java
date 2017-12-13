package com.iopipe;

/**
 * This class contains the configuration for IOPipe and specifies the settings
 * which are to be used when the server is contacted.
 *
 * This class is mutable.
 *
 * @since 2017/12/12
 */
public final class IOPipeConfiguration
{
	/**
	 * {@inheritDoc}
	 * @since 2017/12/12
	 */
	@Override
	public final boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		if (!(__o instanceof IOPipeConfiguration))
			return false;
		
		throw new Error("TODO");
	}
	
	/**
	 * Returns the token for the project to write statistics for.
	 *
	 * @return The project's token.
	 * @since 2017/12/13
	 */
	public final String getProjectToken()
	{
		throw new Error("TODO");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/12
	 */
	@Override
	public final int hashCode()
	{
		throw new Error("TODO");
	}
	
	/**
	 * Returns {@code true} if IOPipe logging is to be enabled, this allows
	 * the service to be disabled for testing.
	 *
	 * @return {@code true} if logging is enabled.
	 * @since 2017/12/13
	 */
	public final boolean isEnabled()
	{
		throw new Error("TODO");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/12
	 */
	@Override
	public final String toString()
	{
		throw new Error("TODO");
	}
	
	/**
	 * This returns a configuration which is initialized by values using the
	 * default means of obtaining them (via environment variables).
	 *
	 * @return The default configuration to use.
	 * @since 2017/12/13
	 */
	public static final IOPipeConfiguration byDefault()
	{
		throw new Error("TODO");
	}
}

