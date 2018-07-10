package com.iopipe;

import javax.json.JsonObject;

/**
 * This class contains a representation of the event that was sent to the
 * IOpipe service and decodes it into an object which simplifies testing.
 *
 * @since 2018/07/10
 */
public final class DecodedEvent
{
	/**
	 * Decodes the specified event.
	 *
	 * @param __data The event to decode.
	 * @return The decoded event.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/07/10
	 */
	public static DecodedEvent decode(String __data)
		throws NullPointerException
	{
		throw new Error("TODO");
	}
	
	/**
	 * Decodes the specified event.
	 *
	 * @param __data The event to decode.
	 * @return The decoded event.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/07/10
	 */
	public static DecodedEvent decode(JsonObject __data)
		throws NullPointerException
	{
		throw new Error("TODO");
	}
}

