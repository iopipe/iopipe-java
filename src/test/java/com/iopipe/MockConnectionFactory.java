package com.iopipe;

import com.iopipe.http.RemoteConnection;
import com.iopipe.http.RemoteConnectionFactory;
import com.iopipe.http.RemoteException;
import com.iopipe.http.RemoteRequest;
import java.util.function.Consumer;

/**
 * This is the connection factory which is only meant to be used for testing.
 *
 * @since 2017/12/13
 */
public final class MockConnectionFactory
	implements RemoteConnectionFactory
{
	/** When a request is made this function will be called. */
	protected final Consumer<RemoteRequest> function;
	
	/**
	 * Initializes the factory where requests are passed to the given consumer
	 * for testing.
	 *
	 * @param __func The function which receives requests.
	 * @since 2017/12/16
	 */
	public MockConnectionFactory(Consumer<RemoteRequest> __func)
	{
		this.function = __func;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/12/13
	 */
	@Override
	public RemoteConnection connect()
		throws RemoteException
	{
		return new MockConnection(this.function);
	}
}

