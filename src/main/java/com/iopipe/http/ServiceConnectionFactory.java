package com.iopipe.http;

import java.util.concurrent.atomic.AtomicReference;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

/**
 * This is a factory which can create connections to the remote IOpipe service
 * to send reports.
 *
 * @since 2018/11/14
 */
public final class ServiceConnectionFactory
	implements RemoteConnectionFactory
{
	/** SSL information. */
	final __SSL__ _ssl =
		new __SSL__();
	
	/**
	 * {@inheritDoc}
	 * @since 2018/11/14
	 */
	@Override
	public RemoteConnection connect(String __url, String __auth)
		throws NullPointerException, RemoteException
	{
		if (__url == null)
			throw new NullPointerException();
		
		return new ServiceConnection(__url, __auth, this._ssl);
	}
	
	/**
	 * Contains SSL information.
	 *
	 * @since 2018/11/19
	 */
	static final class __SSL__
		implements Runnable
	{
		/** The engine to use. */
		final AtomicReference<SSLContext> _context =
			new AtomicReference<>();
		
		/** Did execution fail? */
		volatile boolean _failed;
		
		/**
		 * Initializes the SSL engine.
		 *
		 * @since 2018/11/19
		 */
		{
			Thread t = new Thread(this, "IOpipeSSLInit");
			t.setDaemon(true);
			t.start();
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2018/11/19
		 */
		@Override
		public void run()
		{
			try
			{
				// Initialize context
				SSLContext c = SSLContext.getInstance("TLSv1.2");
				c.init(null, null, new SecureRandom());
				
				this._context.set(c);
			}
			
			// Could not initialize
			catch (NoSuchAlgorithmException|KeyManagementException e)
			{
				e.printStackTrace();
				
				this._failed = true;
			}
		}
		
		/**
		 * Returns the SSL engine.
		 *
		 * @since 2018/11/19
		 */
		final SSLEngine __getEngine()
		{
			AtomicReference<SSLContext> atomic = this._context;
			
			// Burn until it becomes available
			SSLContext c = atomic.get();
			while (c == null)
			{
				if (this._failed)
					return null;
				c = atomic.get();
			}
			
			// Setup engine
			SSLEngine rv = c.createSSLEngine();
			rv.setUseClientMode(true);
			
			return rv;
		}
	}
}

