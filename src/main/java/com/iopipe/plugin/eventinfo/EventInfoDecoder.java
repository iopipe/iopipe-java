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
	 * Returns the class names which this provides a decoder for.
	 *
	 * @return The classes this decodes for.
	 * @since 2018/11/20
	 */
	public abstract String[] decodes();
	
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
	 * For example {@code apiGateway} becomes {@code api-gateway}.
	 *
	 * @return The slugified event type.
	 * @since 2018/07/16
	 */
	public default String slugifiedEventType()
	{
		// Use event type but fallback just in case it was never set
		String et = this.eventType();
		if (et == null)
			et = "unknown";
		
		// Just go through and add dashes before capitals
		StringBuilder sb = new StringBuilder();
		for (int i = 0, n = et.length(); i < n; i++)
		{
			char c = et.charAt(i);
			
			// Prefix with dash?
			if (c >= 'A' && c <= 'Z')
			{
				// Never dash on first character
				if (i > 0)
					sb.append('-');
				
				// Lowercase it
				c = (char)((c - 'A') + 'a');
			}
			
			sb.append(c);
		}
		
		return sb.toString();
	}
}

