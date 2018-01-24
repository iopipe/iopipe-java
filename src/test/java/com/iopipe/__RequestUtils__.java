package com.iopipe;

import com.iopipe.http.RemoteRequest;
import java.util.Map;
import java.util.TreeMap;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonValue;

/**
 * Basic class to work with requests.
 *
 * @since 2018/01/23
 */
final class __RequestUtils__
{
	/**
	 * Not used.
	 *
	 * @since 2018/01/23
	 */
	private __RequestUtils__()
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
						__RequestUtils__.expandObject((JsonStructure)v).
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
						__RequestUtils__.expandObject((JsonStructure)v).
						entrySet())
						rv.put(k + f.getKey(), f.getValue());
			}
		}
		
		return rv;
	}
	
	/**
	 * Same as {@link __RequestUtils__#expandObject(JsonStructure)}.
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
		
		return __RequestUtils__.expandObject(__o.bodyValue());
	}
	
	/**
	 * Checks if the returned object has an error in it, if it does then it
	 * will be returned.
	 * 
	 * @param __e Check if the structure has an error.
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
		
		// It is easier to handle this way
		Map<String, JsonValue> expanded = __RequestUtils__.
			expandObject(__r.bodyValue());
		
		for (Map.Entry<String, JsonValue> e : expanded.entrySet())
			System.err.printf("DEBUG -- %s=%s%n", e.getKey(), e.getValue());
		
		return (JsonObject)expanded.get(".errors");
	}
}

