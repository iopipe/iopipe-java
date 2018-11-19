package com.iopipe.http;

import java.util.concurrent.atomic.AtomicReference;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
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
		final AtomicReference<SSLEngine> _engine =
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
				c.init(null, null, null);
				
				// Setup engine
				SSLEngine e = c.createSSLEngine();
				e.setUseClientMode(true);
				
				// Store
				this._engine.set(e);
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
			AtomicReference<SSLEngine> atomic = this._engine;
			
			// Burn until it becomes available
			SSLEngine rv = atomic.get();
			while (rv == null)
			{
				if (this._failed)
					return null;
				rv = atomic.get();
			}
			
			return rv;
		}
	}
}

