package com.iopipe;

import com.iopipe.http.RemoteResult;

/**
 * Basic class to work with results.
 *
 * @since 2018/01/23
 */
final class __ResultUtils__
{
	/**
	 * Not used.
	 *
	 * @since 2018/01/23
	 */
	private __ResultUtils__()
	{
	}
	
	/**
	 * Checks if the result status code is okay.
	 *
	 * @param __r The request to check.
	 * @return If the status code is okay.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/23
	 */
	public static boolean isOkay(RemoteResult __r)
		throws NullPointerException
	{
		if (__r == null)
			throw new NullPointerException();
		
		int code = __r.code();
		return code >= 200 && code < 300;
	}
}

