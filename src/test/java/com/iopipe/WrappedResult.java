package com.iopipe;

import com.iopipe.http.RemoteResult;

/**
 * This contains the result information.
 *
 * @since 2018/02/24
 */
public final class WrappedResult
{
	/** The URL. */
	public final String url;
	
	/** The result of the call. */
	public final RemoteResult result;
	
	/**
	 * Initializes the wrapped result.
	 *
	 * @param __u The URL.
	 * @param __r The result of the result.
	 * @since 2018/02/24
	 */
	public WrappedResult(String __u, RemoteResult __r)
	{
		this.url = __u;
		this.result = __r;
	}
}

