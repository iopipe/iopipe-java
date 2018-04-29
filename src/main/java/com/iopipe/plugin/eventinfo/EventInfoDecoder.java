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
	 * Returns the rules for the event.
	 *
	 * @return The event rules.
	 * @since 2018/04/29
	 */
	public abstract Rule[] rules();
}

