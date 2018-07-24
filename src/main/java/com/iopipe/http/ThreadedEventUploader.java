package com.iopipe.http;

import com.iopipe.IOpipeEventUploader;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

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
	
	/** The number of bad requests. */
	final AtomicInteger _badrequestcount =
		new AtomicInteger();
	
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
		__Runner__ runner = new __Runner__(__con, this._badrequestcount);
		
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
		return this._badrequestcount.get();
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
		/** The number of invocations before the queue is utilized. */
		private static final int _ACTIVE_THRESHOLD =
			8;
		
		/** The number of uploads to batch together. */
		private static final int _BATCH_COUNT =
			64;
		
		/** The limit to the batch count. */
		private static final int _BATCH_LIMIT =
			_BATCH_COUNT - 1;
		
		/** The connection that is used. */
		protected final RemoteConnection connection;
		
		/** Use concurrent queue since it is quite fast. */
		final Queue<RemoteRequest> _queue =
			new ConcurrentLinkedQueue<>();
		
		/** The number of running lambdas. */
		final AtomicInteger _activecount =
			new AtomicInteger();
		
		/** The number of bad requests. */
		final AtomicInteger _badrequestcount;
		
		/** The number of entries in the queue. */
		final AtomicInteger _inqueue =
			new AtomicInteger();
		
		/** Lock for the event queue. */
		final Lock _queuelock =
			new ReentrantLock();
		
		/** Condition which triggers when something is added. */
		final Condition _queuetrigger =
			this._queuelock.newCondition();
		
		/** Lock on the thread which waits for events to upload. */
		final Lock _victim =
			new ReentrantLock();
		
		/**
		 * Initializes the runner.
		 *
		 * @param __con The connection used.
		 * @param __bcr The counter for bad requests.
		 * @throws NullPointerException On null arguments.
		 * @since 2018/07/23
		 */
		private __Runner__(RemoteConnection __con, AtomicInteger __brc)
		{
			if (__con == null || __brc == null)
				throw new NullPointerException();
			
			this.connection = __con;
			this._badrequestcount = __brc;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2018/07/23
		 */
		@Override
		public final void run()
		{
			AtomicInteger badrequestcount = this._badrequestcount;
			AtomicInteger activecount = this._activecount;
			AtomicInteger inqueue = this._inqueue;
			Queue<RemoteRequest> queue = this._queue;
			Lock queuelock = this._queuelock;
			Condition queuetrigger = this._queuetrigger;
			
			// Batch multiple requests from the queue to reduce locking that
			// is done
			RemoteRequest[] batch = new RemoteRequest[_BATCH_COUNT];
			
			// Constantly read input events
			for (;;)
			{
				// Determine how many items are waiting in the queue and
				// can safely be read
				int awaiting = inqueue.get();
				
				// The queue is empty so just wait until it fills again
				if (awaiting == 0)
				{
					queuelock.lock();
					try
					{
						// Just check back every second
						queuetrigger.awaitNanos(1_000_000_000L);
					}
					
					// Ignore this and just try the loop again
					catch (InterruptedException e)
					{
					}
					
					// Always clear the lock
					finally
					{
						queuelock.unlock();
					}
					
					// Try again
					continue;
				}
				
				// Pull items from the queue
				int count = 0;
				for (int i = 0; i < awaiting; i++)
				{
					RemoteRequest request = queue.poll();
					
					if (i < _BATCH_COUNT)
						batch[count++] = request;
				}
				
				// Items read from the queue, reduce the count after they have
				// been read
				inqueue.getAndAdd(-awaiting);
				
				// Send reports in batches
				int badcount = 0;
				for (int i = 0; i < count; i++)
					try
					{
						// Send it
						RemoteResult result = this.connection.send(
							RequestType.POST, batch[i]);
						
						// Clear so the reference gets garbage collected
						batch[i] = null;
						
						// Only the 200 range is valid for okay responses
						int code = result.code();
						if (!(code >= 200 && code < 300))
							badcount++;
					}
					
					// Failed to write to the server
					catch (RemoteException e)
					{
						badcount++;
					}
				
				// Add to the request count all at once since it is faster
				// than doing multiple many invocations
				badrequestcount.getAndAdd(badcount);
				
				// Reduce the active count by the number of events which
				// were sent, this is used by the victim thread to stop
				// blocking
				activecount.getAndAdd(-awaiting);
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
			
			// Get the number of threads running at once for even generation
			AtomicInteger activecount = this._activecount;
			int nowactive = activecount.get();
			
			// If nothing is going on then we can just send our events
			// serially because sending it to a queue will just add latency
			// since this thread would be waiting around anyway for the queue
			// to be emptied
			if (nowactive < _ACTIVE_THRESHOLD)
			{
				AtomicInteger badrequestcount = this._badrequestcount;
				
				// Just send the event serially
				try
				{
					// Send it
					RemoteResult result = this.connection.send(
						RequestType.POST, __r);
					
					// Only the 200 range is valid for okay responses
					int code = result.code();
					if (!(code >= 200 && code < 300))
						badrequestcount.incrementAndGet();
				}
				
				// Failed to write to the server
				catch (RemoteException e)
				{
					badrequestcount.incrementAndGet();
				}
				
				// This thread is no longer active for an event and nothing
				// more needs to be done
				activecount.decrementAndGet();
				return;
			}
			
			// There are too many threads that are running, so spill over these
			// events to the queue to be sent via another thread so we can
			// return to the user faster
			Queue<RemoteRequest> queue = this._queue;
			queue.add(__r);
			
			// Signal thread that an event was pushed, but only if there was
			// nothing (the other thread would have been asleep)
			AtomicInteger inqueue = this._inqueue;
			int was = inqueue.getAndIncrement();
			if (was == 0)
			{
				Lock queuelock = this._queuelock;
				queuelock.lock();
				try
				{
					this._queuetrigger.signal();
				}
				finally
				{
					queuelock.unlock();
				}
			}
			
			// Get the number of events that are waiting to be sent
			nowactive = activecount.get();
			
			// Try and see if our thread will become the victim thread if
			// events are currently being processed.
			// The victim thread is the one which will end up waiting for the
			// queue to empty to ensure that all events are sent. Any other
			// threads which are not the victim thread will just return if
			// they fail to get this lock.
			Lock victim = this._victim;
			if (nowactive > 0 && victim.tryLock())
				try
				{
					// Wait until the queue is drained
					while (inqueue.get() > 0)
						continue;
				}
				
				// Unlock the victim thread, another thread can claim it
				finally
				{
					victim.unlock();
				}
		}
	}
}

