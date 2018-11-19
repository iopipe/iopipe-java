package com.iopipe;

/**
 * This class keeps track of a single running invocation at a time and is used
 * to determine and report when it is about to timeout.
 *
 * @since 2018/11/19
 */
final class __TimeOutTracker__
{
	/** The sender where requests go. */
	final __RequestSender__ _rsender;
	
	/**
	 * Initializes the tracker.
	 *
	 * @since 2018/11/19
	 */
	__TimeOutTracker__()
	{
		throw new Error("TODO");
	}
	
	/**
	 * This class is the thread which tracks and takes care of any invocations
	 * which have timed out. It is lazy in its future checking accordingly.
	 *
	 * @since 2018/11/19
	 */
	static final class __Squirrel__
		implements Runnable
	{
		/**
		 * {@inheritDoc}
		 * @since 2018/11/19
		 */
		@Override
		public final void run()
		{
			throw new Error("TODO");
		}
	}
}

