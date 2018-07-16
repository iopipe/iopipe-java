package com.iopipe;

import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import java.util.Map;
import java.util.TreeMap;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;

/**
 * Basic class to work with requests and results.
 *
 * @since 2018/01/23
 */
final class __Utils__
{
	/**
	 * Not used.
	 *
	 * @since 2018/01/23
	 */
	private __Utils__()
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
	public static boolean isResultOkay(RemoteResult __r)
		throws NullPointerException
	{
		if (__r == null)
			throw new NullPointerException();
		
		int code = __r.code();
		return code >= 200 && code < 300;
	}
}

