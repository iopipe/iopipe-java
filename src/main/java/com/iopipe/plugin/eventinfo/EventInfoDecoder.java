package com.iopipe.plugin.eventinfo;

/**
 * This interface represents a decoder for event types being input into the
 * executed method and contains the information that is needed to parse
 * fields accordingly.
 *
 * @since 2018/04/22
 */
public interface EventInfoDecoder
{
	/**
	 * Accepts the given value to decode events from.
	 *
	 * @param __a Where to store values.
	 * @param __v The value to decode.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/05/02
	 */
	public abstract void accept(ValueAcceptor __a, Object __v)
		throws NullPointerException;
	
	/**
	 * Returns the class this implements a decoder for.
	 *
	 * @return The class this provides a decoder for.
	 * @since 2018/04/22
	 */
	public abstract Class<?> decodes();
	
	/**
	 * Returns the event type string this will be specified under.
	 *
	 * @return The string which specified the event type.
	 * @since 2018/04/23
	 */
	public abstract String eventType();
	
	/**
	 * Returns the slugified event type.
	 *
	 * @return The slugified event type.
	 * @since 2018/07/16
	 */
	public default String slugifiedEventType()
	{
		throw new Error("TODO");
	}
}

