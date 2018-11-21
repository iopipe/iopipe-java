package com.iopipe;

import com.iopipe.http.NullConnection;
import com.iopipe.http.RemoteBody;
import com.iopipe.http.RemoteConnection;
import com.iopipe.http.RemoteConnectionFactory;
import com.iopipe.http.RemoteException;
import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import com.iopipe.http.RequestType;
import java.util.concurrent.atomic.AtomicInteger;
import org.pmw.tinylog.Logger;

/**
 * This class manages sending requests to the remote service.
 *
 * @since 2018/11/19
 */
final class __RequestSender__
{
	/** The connection to the server. */
	protected final RemoteConnection connection;
	
	/**
	 * Initializes the request sender.
	 *
	 * @param __con The remote service to connect to.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/19
	 */
	__RequestSender__(RemoteConnection __con)
		throws NullPointerException
	{
		if (__con == null)
			throw new NullPointerException();
		
		this.connection = __con;
	}
	
	/**
	 * Sends the specified request to the server, using the request ID.
	 *
	 * @param __r The request to send to the server.
	 * @return The result of the report.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/15
	 */
	final RemoteResult __send(RemoteRequest __r)
		throws NullPointerException
	{
		if (__r == null)
			throw new NullPointerException();
		
		// Send request
		try
		{
			RemoteResult result = this.connection.send(RequestType.POST, __r);
			
			// Only the 200 range is valid for okay responses
			int code = result.code();
			if (!(code >= 200 && code < 300))
			{
				// Only emit errors for failed requests
				Logger.error("Request {} failed with result {}.",
					__r, result);
			}
			
			return result;
		}
		
		// Failed to write to the server
		catch (RemoteException e)
		{
			Logger.error(e, "Request {} failed due to exception.", __r);
			
			return new RemoteResult(503, RemoteBody.MIMETYPE_JSON, "");
		}
	}
}

