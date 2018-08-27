package com.iopipe;

import java.util.UUID;

/**
 * Internal shared variables and such
 *
 * @since 2018/08/01
 */
final class __Shared__
{
	/** The thread group to use main service threads under. */
	static final ThreadGroup _SERVICE_THREAD_GROUP;
	
	/** The process ID, used to identify this process. */
	static final UUID _PROCESS_ID =
		UUID.randomUUID();
	
	/**
	 * Initializes some shared variables.
	 *
	 * @since 2018/08/01
	 */
	static
	{
		// Setup a thread group where IOpipe's service threads are placed
		// under
		ThreadGroup stg = null;
		try
		{
			stg = new ThreadGroup("IOpipe-ServiceThreads");
		}
		catch (SecurityException e)
		{
			// Just use our thread group
			stg = Thread.currentThread().getThreadGroup();
		}
		
		_SERVICE_THREAD_GROUP = stg;
	}
	
	/**
	 * Not used.
	 *
	 * @since 2018/08/01
	 */
	private __Shared__()
	{
	}
}

