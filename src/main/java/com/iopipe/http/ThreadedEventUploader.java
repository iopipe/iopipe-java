package com.iopipe.http;

import com.iopipe.IOpipeEventUploader;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.Deque;
import java.util.LinkedList;

/**
 * This is an event uploader which
 *
 * @since 2018/07/23
 */
public final class ThreadedEventUploader
	implements IOpipeEventUploader
{
	/** The runner thread. */
	protected final Thread _thread;
	
	/** The runner itself. */
	protected final __Runner__ _runner;
	
	/**
	 * Initializes the threaded event uploader.
	 *
	 * @param __con The connection to use.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/07/23
	 */
	public ThreadedEventUploader(RemoteConnection __con)
		throws NullPointerException
	{
		if (__con == null)
			throw new NullPointerException();
		
		// Setup runner
		__Runner__ runner = new __Runner__(__con);
		
		// Then setup the thread running that
		Thread thread = new Thread(runner, "IOpipe-EventUploader");
		thread.setDaemon(true);
		thread.start();
		
		this._runner = runner;
		this._thread = thread;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/07/23
	 */
	@Override
	public final void await()
	{
		this._runner.__await();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/07/23
	 */
	@Override
	public final int badRequestCount()
	{
		return this._runner._badrequestcount.get();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/07/23
	 */
	@Override
	public final void upload(RemoteRequest __r)
		throws NullPointerException
	{
		this._runner.__upload(__r);
	}
	
	/**
	 * This class is the runner for background event uploads, it waits for
	 * things in the queue and tries to upload them.
	 *
	 * @since 2018/07/23
	 */
	private static final class __Runner__
		implements Runnable
	{
		/** The number of uploads to batch together. */
		private static final int _BATCH_COUNT =
			64;
		
		/** The limit to the batch count. */
		private static final int _BATCH_LIMIT =
			_BATCH_COUNT - 1;
		
		/** The connection that is used. */
		protected final RemoteConnection connection;
		
		/** The queue of requests, linked list has cheap queue-ness. */
		final Deque<RemoteRequest> _queue =
			new LinkedList();
		
		/** The number of bad requests. */
		final AtomicInteger _badrequestcount =
			new AtomicInteger();
		
		/** The number of running lambdas. */
		final AtomicInteger _activecount =
			new AtomicInteger();
		
		/** Monitor for when the last event is drained. */
		final Object _lastmonitor =
			new Object();
		
		/** Monitor on the final thread indicator, used for blocking. */
		final Object _finalthreadmonitor =
			new Object();
		
		/** The thread which is considered the last thread of execution. */
		Thread _finalthread;
		
		/**
		 * Initializes the runner.
		 *
		 * @param __con The connection used.
		 * @throws NullPointerException On null arguments.
		 * @since 2018/07/23
		 */
		private __Runner__(RemoteConnection __con)
		{
			if (__con == null)
				throw new NullPointerException();
			
			this.connection = __con;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2018/07/23
		 */
		public final void run()
		{
			AtomicInteger badrequestcount = this._badrequestcount;
			AtomicInteger activecount = this._activecount;
			Deque<RemoteRequest> queue = this._queue;
			Object lastmonitor = this._lastmonitor;
			
			// Batch multiple requests from the queue to reduce locking that
			// is done
			RemoteRequest[] batch = new RemoteRequest[_BATCH_COUNT];
			
			// Constantly read input events
			for (;;)
			{
				// The number of items which were batched, this is to prevent
				// locking multiple times just to send one thing
				int count = 0;
				
				// See if there is an event waiting in the queue
				RemoteRequest request = null;
__outer:
				for (;;)
					synchronized (queue)
					{
						// Try to grab as many requests from the queue as
						// possible
						while (count < _BATCH_LIMIT)
						{
							request = queue.pollFirst();
							
							// No more requests in the queue
							if (request == null)
							{
								// There was at least one request so do not
								// block waiting for more ever!
								if (count > 0)
									break __outer;
								
								// Wait 
								try
								{
									queue.wait(100L);
								}
								catch (InterruptedException e)
								{
									// Just try again
								}
							}
							
							// Fill the request
							else
								batch[count++] = request;
						}
					}
				
				// Send report
				for (int i = 0; i < count; i++)
					try
					{
						// Send it
						RemoteResult result = this.connection.send(
							RequestType.POST, batch[i]);
						
						// Only the 200 range is valid for okay responses
						int code = result.code();
						if (!(code >= 200 && code < 300))
							badrequestcount.getAndIncrement();
					}
					
					// Failed to write to the server
					catch (RemoteException e)
					{
						badrequestcount.getAndIncrement();
					}
				
				// If this was the last invocation then notify the running
				// thread that this happened
				if (activecount.decrementAndGet() == 0)
					synchronized (lastmonitor)
					{
						lastmonitor.notifyAll();
					}
			}
		}
		
		/**
		 * Increases the number of invocations that are currently happening
		 * so that way events in the background can be sent without waiting
		 * for the request to generate a result.
		 *
		 * @since 2018/07/23
		 */
		public final void __await()
		{
			this._activecount.getAndIncrement();
		}
		
		/**
		 * Uploads the specified event into the queue and either returns
		 * immedietly if there are multiple invocations running or blocks until
		 * there is only one left running.
		 *
		 * @param __r The request to upload.
		 * @throws NullPointerException On null arguments.
		 * @since 2018/07/23
		 */
		public final void __upload(RemoteRequest __r)
			throws NullPointerException
		{
			if (__r == null)
				throw new NullPointerException();
			
			// Determine if this is the final thread of execution
			boolean isfinalthread = false;
			Thread mythread = Thread.currentThread();
			Object finalthreadmonitor = this._finalthreadmonitor;
			synchronized (finalthreadmonitor)
			{
				// We are the final thread?
				Thread finalthread = this._finalthread;
				if (finalthread == mythread)
					isfinalthread = true;
				
				// No other threads, become the final thread
				else if (finalthread == null)
				{
					this._finalthread = mythread;
					isfinalthread = true;
				}
			}
			
			// Lock on the queue
			Deque<RemoteRequest> queue = this._queue;
			synchronized (queue)
			{
				// Add the event to the queue
				queue.addLast(__r);
				
				// Tell the uploader that an event is waiting if it was locked
				queue.notify();
			}
			
			// If this is the final thread then we have to wait until all the
			// events have been drained before we continue
			if (isfinalthread)
			{
				Object lastmonitor = this._lastmonitor;
				synchronized (lastmonitor)
				{
					AtomicInteger activecount = this._activecount;
					for (;;)
					{
						// See if the number of active events are zero, if they
						// are then nothing is happening and everything was
						// sent
						if (activecount.get() <= 0)
						{
							// Clear the final thread so it is gone now
							synchronized (finalthreadmonitor)
							{
								this._finalthread = null;
							}
							
							// There are no invocations and it is safe to
							// return, if any other events happen to occur
							// before this happens then another thread will
							// just become the final thread.
							return;
						}
						
						// Wait for the last monitor to be notified which
						// indicates that activecount was made to be zero
						try
						{
							lastmonitor.wait(5L);
						}
						catch (InterruptedException e)
						{
							// Try again
						}
					}
				}
			}
		}
	}
}
