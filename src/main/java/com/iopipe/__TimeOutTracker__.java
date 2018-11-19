package com.iopipe;

import com.amazonaws.services.lambda.runtime.Context;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class keeps track of a single running invocation at a time and is used
 * to determine and report when it is about to timeout.
 *
 * @since 2018/11/19
 */
final class __TimeOutTracker__
{
	/** The timeout window time. */
	protected final int window;
	
	/** The thread our squirrel runs in. */
	private final Thread _thread;
	
	/** The tracker to send to. */
	private final __Squirrel__ _squirrel;
	
	/**
	 * Initializes the tracker.
	 *
	 * @param __rs The sender for requests.
	 * @param __tw The window for timeouts.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/19
	 */
	__TimeOutTracker__(__RequestSender__ __rs, int __tw)
		throws NullPointerException
	{
		if (__rs == null)
			throw new NullPointerException();
		
		// Spawn the squirrel to take care of everything
		__Squirrel__ sq = new __Squirrel__(__rs);
		Thread t = new Thread(sq, "IOpipeSquirrel");
		t.setDaemon(true);
		t.start();
		
		// The tracker to report to
		this.window = (__tw > 0 ? __tw : 0);
		this._thread = t;
		this._squirrel = sq;
	}
	
	/**
	 * Tracks the given execution and context.
	 *
	 * @param __c The context to track.
	 * @param __exec This execution.
	 * @param __sent Will be used to determine if timeout has happened.
	 * @param __t The thread of execution to keep track of.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/19
	 */
	final void __track(Context __c, IOpipeExecution __exec,
		AtomicBoolean __sent, Thread __t)
		throws NullPointerException
	{
		if (__c == null)
			throw new NullPointerException();
		
		// If the timeout window is disabled, then do not track timeouts
		int window = this.window;
		if (window == 0)
			return;
		
		// Do not keep track if there is no timeout or it is very far into
		// the future (likely set by the mock context)
		int rem = __c.getRemainingTimeInMillis();
		if (rem <= 0 || rem == Integer.MAX_VALUE)
			return;
		
		// If the remaining time is too close within the threshold then it
		// will likely have trouble firing when the time comes
		rem -= window;
		if (rem <= 0)
			return;
		
		// Setup tracker to watch on
		__Track__ t = new __Track__(__c, __exec, __sent, rem, __t);
		
		// Tell our squirrel to keep track of this execution
		__Squirrel__ squirrel = this._squirrel;
		synchronized (squirrel)
		{
			squirrel._track = t;
			
			// We do not need to notify, we can just interrupt because the
			// squirrel is waiting for something to track.
			// wait() is something that is handled by interrupts
			this._thread.interrupt();
		}
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
		/** The sender where requests go. */
		final __RequestSender__ _rsender;
		
		/** The execution our squirrel is tracking. */
		__Track__ _track;
		
		/**
		 * Initializes the squirrel.
		 *
		 * @param __rs Where requests go for sending.
		 * @throws NullPointerException On null arguments.
		 * @since 2018/11/19
		 */
		__Squirrel__(__RequestSender__ __rs)
			throws NullPointerException
		{
			if (__rs == null)
				throw new NullPointerException();
			
			this._rsender = __rs;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2018/11/19
		 */
		@Override
		public final void run()
		{
			Thread selfthread = Thread.currentThread();
			__RequestSender__ rsender = this._rsender;
			
			// Infinite loop
			for (;;)
			{
				// Wait until we get something to keep track of
				__Track__ track;
				synchronized (this)
				{
					// Clear our own interrupted flag since it cannot be
					// raised while we own the lock
					Thread.interrupted();
					
					// Are we tracking this?
					track = this._track;
					
					// Wait until we get something to track
					if (track == null)
					{
						// Wait until we get interrupted (or notified)
						try
						{
							this.wait();
						}
						catch (InterruptedException e)
						{
						}
						
						// We got our lock back so try again
						track = this._track;
						
						// Still nothing, so just try again
						if (track == null)
							continue;
					}
					
					// Remove this object from tracking
					this._track = null;
				}
				
				// Sleep for the initial sleeping period, when it ends this
				// should be when timeout is happening
				try
				{
					Thread.sleep(track._initsleep);
				}
				catch (InterruptedException e)
				{
					// Ignore, but this likely means that some other
					// invocation happened and we were still asleep
				}
				
				// If the atomic was never sent to true, then this means the
				// main service runner never sent any invocation
				AtomicBoolean sent = track._sent;
				if (sent.compareAndSet(false, true))
				{
					// The execution at this point will always be active
					__ActiveExecution__ exec =
						(__ActiveExecution__)track._exec;
					
					// Labels to indicate things
					exec.label("@iopipe/error");
					exec.label("@iopipe/timeout");
					
					// Generate a timeout exception, but for the ease of use in
					// debugging use the stack trace of the thread which timed
					// out
					IOpipeTimeOutException reported =
						new IOpipeTimeOutException("Execution timed out.");
					reported.setStackTrace(track._source.getStackTrace());
					exec.__setThrown(reported);
					
					// Send request
					rsender.__send(exec.__buildRequest());
				}
			}
		}
	}
	
	/**
	 * Stores tracking information.
	 *
	 * @since 2018/11/19
	 */
	static final class __Track__
	{
		/** The AWS context. */
		final Context _context;
		
		/** The execution. */
		final IOpipeExecution _exec;
		
		/** Was a request sent? */
		final AtomicBoolean _sent;
		
		/** Initial sleep duration. */
		final int _initsleep;
		
		/** The source thread. */
		final Thread _source;
		
		/**
		 * Tracks the given execution and context.
		 *
		 * @param __c The context to track.
		 * @param __exec This execution.
		 * @param __sent Will be used to determine if timeout has happened.
		 * @param __is Initial sleep duration.
		 * @param __t The source thread.
		 * @throws NullPointerException On null arguments.
		 * @since 2018/11/19
		 */
		__Track__(Context __c, IOpipeExecution __exec,
			AtomicBoolean __sent, int __is, Thread __t)
			throws NullPointerException
		{
			if (__c == null || __exec == null || __sent == null || __t == null)
				throw new NullPointerException();
			
			this._context = __c;
			this._exec = __exec;
			this._sent = __sent;
			this._initsleep = __is;
			this._source = __t;
		}
	}
}

