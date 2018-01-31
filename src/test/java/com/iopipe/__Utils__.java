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
	 * Expands a JsonObject so that each value within the object is represented
	 * in a linear fashion with an associated value. This means it easier to
	 * use by tests rather than iterating down into objects as such.
	 *
	 * Arrays are turned into indexes and values.
	 *
	 * @param __s The object to expand.
	 * @return The map of expanded values.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/23
	 */
	public static Map<String, JsonValue> expandObject(JsonStructure __s)
		throws NullPointerException
	{
		if (__s == null)
			throw new NullPointerException();
		
		Map<String, JsonValue> rv = new TreeMap<>();
		
		// Add self structure to the blank end
		rv.put("", __s);
		
		// Map array values
		if (__s instanceof JsonArray)
		{
			JsonArray a = (JsonArray)__s;
			
			for (int i = 0, n = a.size(); i < n; i++)
			{
				String k = "[" + i + "]";
				JsonValue v = a.get(i);
				
				// Add index value
				rv.put(k, v);
				
				// Recurive into structure?
				if (v instanceof JsonStructure)
					for (Map.Entry<String, JsonValue> f :
						__Utils__.expandObject((JsonStructure)v).
						entrySet())
						rv.put(k + f.getKey(), f.getValue());
			}
		}
		
		// Map object values
		else
		{
			JsonObject o = (JsonObject)__s;
			
			// Expand objects
			for (Map.Entry<String, JsonValue> e : o.entrySet())
			{
				String k = "." + e.getKey();
				JsonValue v = e.getValue();
				
				// Add value to key
				rv.put(k, v);
				
				// Recursive into structure?
				if (v instanceof JsonStructure)
					for (Map.Entry<String, JsonValue> f :
						__Utils__.expandObject((JsonStructure)v).
						entrySet())
						rv.put(k + f.getKey(), f.getValue());
			}
		}
		
		return rv;
	}
	
	/**
	 * Same as {@link __Utils__#expandObject(JsonStructure)}.
	 *
	 * @param __o The object to expand.
	 * @return The map of expanded values.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/23
	 */
	public static Map<String, JsonValue> expandObject(RemoteRequest __o)
		throws NullPointerException
	{
		if (__o == null)
			throw new NullPointerException();
		
		return __Utils__.expandObject(__o.bodyValue());
	}
	
	/**
	 * Checks if the returned object has an error in it, if it does then it
	 * will be returned.
	 * 
	 * @param __r Check if the structure has an error.
	 * @return The object represented the error or {@code null} if there
	 * is none.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/23
	 */
	public static JsonObject hasError(RemoteRequest __r)
		throws NullPointerException
	{
		if (__r == null)
			throw new NullPointerException();
		
		return __Utils__.hasError(__Utils__.
			expandObject(__r.bodyValue()));
	}
	
	/**
	 * Checks if the returned object has an error in it, if it does then it
	 * will be returned.
	 * 
	 * @param __e The expanded JSON data
	 * @return The object represented the error or {@code null} if there
	 * is none.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/23
	 */
	public static JsonObject hasError(Map<String, JsonValue> __e)
		throws NullPointerException
	{
		if (__e == null)
			throw new NullPointerException();
		
		return (JsonObject)__e.get(".errors");
	}
	
	/**
	 * Checks if the given value is equal to the given string.
	 *
	 * @param __v The value to check.
	 * @param __s The string to compare against.
	 * @return {@code true} if they are equal.
	 * @since 2018/01/24
	 */
	public static boolean isEqual(JsonValue __v, String __s)
	{
		// Compare null
		if (__s == null || __v == null)
			return (__v == null) == (__s == null);
		
		// Not a string?
		else if (!(__v instanceof JsonString))
			return false;
		
		// Compare string
		return __s.equals(((JsonString)__v).getString());
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

