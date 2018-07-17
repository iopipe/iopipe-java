package com.iopipe;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;

/**
 * This represents a request that was made to the signer for profiler data
 * upload, it is given a URL of data.
 *
 * @since 2018/07/16
 */
public final class SignerEvent
	implements Event
{
	/** The ARN. */
	public final String arn;
	
	/** The request ID. */
	public final String requestid;
	
	/** The timestamp. */
	public final long timestamp;
	
	/** The extension. */
	public final String extension;
	
	/**
	 * Initializes the signer event.
	 *
	 * @param __arn The passed ARN.
	 * @param __requestid The request ID.
	 * @param __timestamp The timestamp used.
	 * @param __extension The extension used.
	 * @since 2018/07/16
	 */
	public SignerEvent(String __arn, String __requestid, long __timestamp,
		String __extension)
	{
		this.arn = __arn;
		this.requestid = __requestid;
		this.timestamp = __timestamp;
		this.extension = __extension;
	}
	
	/**
	 * Decodes the specified event.
	 *
	 * @param __data The event to decode.
	 * @return The decoded event.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/07/16
	 */
	public static SignerEvent decode(String __data)
		throws NullPointerException
	{
		if (__data == null)
			throw new NullPointerException();
		
		try (StringReader r = new StringReader(__data))
		{
			return SignerEvent.decode(
				((JsonObject)(Json.createReader(r).read())));
		}
	}
	
	/**
	 * Decodes the specified event.
	 *
	 * @param __data The event to decode.
	 * @return The decoded event.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/07/16
	 */
	public static SignerEvent decode(JsonObject __data)
		throws NullPointerException
	{
		if (__data == null)
			throw new NullPointerException();
		
		String arn = null;
		String requestid = null;
		long timestamp = Long.MIN_VALUE;
		String extension = null;
		
		for (Map.Entry<String, JsonValue> e : __data.entrySet())
		{
			JsonValue v = e.getValue();
			
			String k;
			switch ((k = e.getKey()))
			{
				case "arn":
					arn = ((JsonString)v).getString();
					break;
					
				case "requestId":
					requestid = ((JsonString)v).getString();
					break;
					
				case "timestamp":
					timestamp = ((JsonNumber)v).longValue();
					break;
					
				case "extension":
					extension = ((JsonString)v).getString();
					break;
				
					// Unknown
				default:
					throw new RuntimeException(
						"Invalid key in Signer event: " + k);
			}
		}
		
		return new SignerEvent(arn, requestid, timestamp, extension);
	}
}

