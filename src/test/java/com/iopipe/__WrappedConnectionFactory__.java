package com.iopipe;

import com.iopipe.http.RemoteConnection;
import com.iopipe.http.RemoteConnectionFactory;
import com.iopipe.http.RemoteException;
import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;

/**
 * This is a connection factory which allows results to be monitored.
 *
 * @since 2018/01/23
 */
final class __WrappedConnectionFactory__
	implements RemoteConnectionFactory
{
	/** The single to call into. */
	protected final Single single;
	
	/** The connection factory to wrap. */
	protected final RemoteConnectionFactory factory;
	
	/**
	 * Initializes the wrapped connection factory.
	 *
	 * @param __s The single to wrap.
	 * @param __c The connection factory to wrap.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/23
	 */
	__WrappedConnectionFactory__(Single __s, RemoteConnectionFactory __c)
		throws NullPointerException
	{
		if (__s == null || __c == null)
			throw new NullPointerException();
		
		this.single = __s;
		this.factory = __c;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/23
	 */
	@Override
	public RemoteConnection connect()
		throws RemoteException
	{
		return new __Connection__(this.single, this.factory.connect());
	}
	
	/**
	 * Connection to the remote end for snooping.
	 *
	 * @since 2018/01/23
	 */
	private static final class __Connection__
		implements RemoteConnection
	{
		/** The single to call into. */
		protected final Single single;
	
		/** The connection to wrap. */
		protected final RemoteConnection connection;
	
		/**
		 * Initializes the wrapped connection.
		 *
		 * @param __s The single to wrap.
		 * @param __c The connection to wrap.
		 * @throws NullPointerException On null arguments.
		 * @since 2018/01/23
		 */
		__Connection__(Single __s, RemoteConnection __c)
			throws NullPointerException
		{
			if (__s == null || __c == null)
				throw new NullPointerException();
		
			this.single = __s;
			this.connection = __c;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2018/01/23
		 */
		@Override
		public RemoteResult send(RemoteRequest __r)
			throws NullPointerException, RemoteException
		{
			RemoteResult rv = this.connection.send(__r);
			
			// Snoop and have the test see the result before the service
			// sees it
			this.single.remoteResult(rv);
			
			return rv;
		}
	}
}

