package com.iopipe;

import com.iopipe.http.RemoteConnection;
import com.iopipe.http.RemoteConnectionFactory;
import com.iopipe.http.RemoteException;
import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import com.iopipe.http.RequestType;
import java.util.concurrent.atomic.AtomicInteger;
import org.pmw.tinylog.Logger;

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
	
	/** The number of sent requests. */
	private final AtomicInteger _requestcount =
		new AtomicInteger();
	
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
	public final RemoteConnection connect(String __url, String __auth)
		throws NullPointerException, RemoteException
	{
		return new __Connection__(this.single, __url, __auth,
			this.factory.connect(__url, __auth), this._requestcount);
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
		
		/** The remote URL. */
		protected final String url;
		
		/** The authorization token. */
		protected final String authtoken;
		
		/** The number of sent requests. */
		private final AtomicInteger _requestcount;
	
		/**
		 * Initializes the wrapped connection.
		 *
		 * @param __s The single to wrap.
		 * @param __url The remote URL.
		 * @param __auth The authorization token.
		 * @param __c The connection to wrap.
		 * @param __rc The request counter.
		 * @throws NullPointerException On null arguments except for
		 * {@code __auth}.
		 * @since 2018/01/23
		 */
		__Connection__(Single __s, String __url, String __auth,
			RemoteConnection __c, AtomicInteger __rc)
			throws NullPointerException
		{
			if (__s == null || __url == null || __c == null || __rc == null)
				throw new NullPointerException();
		
			this.single = __s;
			this.url = __url;
			this.authtoken = __auth;
			this.connection = __c;
			this._requestcount = __rc;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2018/01/23
		 */
		@Override
		public RemoteResult send(RequestType __t, RemoteRequest __r)
			throws NullPointerException, RemoteException
		{
			// Snoop the request being sent to the server to make sure it is
			// being formed correctly
			Single single = this.single;
			single.remoteRequest(new WrappedRequest(
				this.url, this.authtoken, __t, __r,
				this._requestcount.incrementAndGet()));
			
			// Send result to remote server, which generates some things
			RemoteResult rv = this.connection.send(__t, __r);
			
			// Snoop and have the test see the result before the service
			// sees it
			single.remoteResult(new WrappedResult(this.url, rv));
			
			return rv;
		}
	}
}

