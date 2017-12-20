package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is used to manage
 *
 * @since 2017/12/20
 */
final class __TimeOutWatchDog__
	implements Runnable
{
	/** This is set to true when the timeout has been finished. */
	final AtomicBoolean _generated =
		new AtomicBoolean();
	
	/**
	 * Initializes the watch dog.
	 *
	 * @param __sv The service being watched.
	 * @param __context The context to generate timeouts for.
	 * @param __src The source thread of execution.
	 * @param __wt The duration of the timeout window.
	 * @param __cs Is this a cold start and thus the first execution ever
	 * to run on the JVM?
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/20
	 */
	__TimeOutWatchDog__(IOPipeService __sv, Context __context, Thread __src,
		int __wt, boolean __cs)
		throws NullPointerException
	{
		if (__sv == null || __context == null || __src == null)
			throw new NullPointerException();
		
		throw new Error("TODO");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/20
	 */
	@Override
	public void run()
	{
		throw new Error("TODO");
	}
	
	/**
	 * Indicates that the method being executed has finished and that the
	 * timeout watcher should stop running.
	 *
	 * @since 2017/12/20
	 */
	final void __finished()
	{
		throw new Error("TODO");
	}
}

