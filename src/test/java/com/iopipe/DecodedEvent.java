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
	 * Returns the custom metric identified by the given key.
	 *
	 * @param __k The key to obtain.
	 * @return The custom metric by the given key or {@code null} if there is
	 * no metric.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/07/10
	 */
	public final CustomMetric customMetric(String __k)
		throws NullPointerException
	{
		if (__k == null)
			throw new NullPointerException();
		
		throw new Error("TODO");
	}
	
	/**
	 * Returns all of the custom metrics that are available.
	 *
	 * @return All of the available and used custom metrics.
	 * @since 2018/07/12
	 */
	public final CustomMetric[] customMetrics()
	{
		throw new Error("TODO");
	}
	
	/**
	 * Returns the disk space that is being used.
	 *
	 * @return The disk space information.
	 * @since 2018/07/12
	 */
	public final DiskUsage diskUsage()
	{
		throw new Error("TODO");
	}
	
	/**
	 * Does this event have an error?
	 *
	 * @return If there is an error.
	 * @since 2018/07/10
	 */
	public final boolean hasError()
	{
		throw new Error("TODO");
	}
	
	/**
	 * Does the specified label exist?
	 *
	 * @param __k The label to check.
	 * @return If the given label exists.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/07/10
	 */
	public final boolean hasLabel(String __k)
		throws NullPointerException
	{
		if (__k == null)
			throw new NullPointerException();
		
		throw new Error("TODO");
	}
	
	/**
	 * Is this a coldstart?
	 *
	 * @return If this is a cold start.
	 * @since 2018/07/10
	 */
	public final boolean isColdStart()
	{
		throw new Error("TODO");
	}
	
	/**
	 * Is the token valid?
	 *
	 * @return If the token is valid.
	 * @since 2018/07/10
	 */
	public final boolean isTokenValid()
	{
		throw new Error("TODO");
	}
	
	/**
	 * Returns all of the labels which exist in the event.
	 *
	 * @return All of the event labels.
	 * @since 2018/07/12 
	 */
	public final String[] labels()
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
	public static DecodedEvent decode(String __data)
		throws NullPointerException
	{
		if (__data == null)
			throw new NullPointerException();
		
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
		if (__data == null)
			throw new NullPointerException();
		
		throw new Error("TODO");
	}
	
	/**
	 * Represents and stores disk usage information.
	 *
	 * @since 2018/07/12
	 */
	public static final class DiskUsage
	{
		/** Total space. */
		public final double total;
		
		/** Used disk space. */
		public final double used;
		
		/** Percentage of disk space. */
		public final double percent;
		
		/**
		 * Initializes disk usage information.
		 *
		 * @param __t The total disk space available.
		 * @param __u The used disk space.
		 * @param __p The percent of disk space used.
		 * @since 2018/07/12
		 */
		public DiskUsage(double __t, double __u, double __p)
		{
			this.total = __t;
			this.used = __u;
			this.percent = __p;
		}
	}
	
	/**
	 * This represents information about a plugin.
	 *
	 * @since 2018/07/10
	 */
	public static final class Plugin
	{
		/**
		 * Is this plugin enabled?
		 *
		 * @return If the plugin is enabled.
		 * @since 2018/07/10
		 */
		public final boolean isEnabled()
		{
			throw new Error("TODO");
		}
	}
}

