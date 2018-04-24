package com.iopipe.plugin.eventinfo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.iopipe.CustomMetric;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This class manages and initializes the decoders for event information
 * extraction.
 *
 * @since 2018/04/22
 */
public final class EventInfoDecoders
{
	/** Logging. */
	private static final Logger _LOGGER =
		LogManager.getLogger(EventInfoDecoders.class);
	
	/** Decoders which have been registered. */
	private final Map<Class<?>, EventInfoDecoder> _decoders =
		new LinkedHashMap<>();
	
	/** Cache of classes that have been registered for type lookup. */
	private volatile Class<?>[] _classes =
		new Class<?>[0];
	
	/**
	 * Initializes the event decoders with the default decoders.
	 *
	 * @since 2018/04/23
	 */
	public EventInfoDecoders()
	{
		this.register(new APIGatewayDecoder());
		this.register(new CloudFrontDecoder());
		this.register(new KinesisDecoder());
		this.register(new FirehoseDecoder());
		this.register(new S3Decoder());
		this.register(new ScheduledDecoder());
		this.register(new SNSDecoder());
	}
	
	/**
	 * Decodes the specified object and returns an array containing custom
	 * metrics to be added to the report.
	 *
	 * @param __o The object to report against.
	 * @return The custom metrics which detail the object.
	 * @since 2018/04/23
	 */
	public final CustomMetric[] decode(Object __o)
	{
		// If this is the null object then it is rather pointless to try and
		// decode it
		if (__o == null)
			return new CustomMetric[0];
		
		Class<?> oftype = __o.getClass();
		
		// Go through
		Class<?> match = null;
		for (Class<?> maybe : this._classes)
		{
			if (oftype.isAssignableFrom(maybe))
			{
				match = maybe;
				break;
			}
		}
		
		// Unmatched, do nothing
		if (match == null)
			return new CustomMetric[0];
		
		// Custom metrics to return
		List<CustomMetric> rv = new ArrayList<>();
		
		if (true)
			throw new Error("TODO");
		
		return rv.<CustomMetric>toArray(new CustomMetric[rv.size()]);
	}
	
	/**
	 * Registers a decoder which decodes for a given class type.
	 *
	 * @param __d The decoder to register.
	 * @throws IllegalStateException If the decoder decodes no class.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/04/23
	 */
	public final void register(EventInfoDecoder __d)
		throws IllegalStateException, NullPointerException
	{
		if (__d == null)
			throw new NullPointerException();
		
		// There must be a class to decode
		Class<?> decodes = __d.decodes();
		if (decodes == null)
			throw new IllegalStateException("Decoder decodes no class.");
		
		// Register it
		Map<Class<?>, EventInfoDecoder> decoders = this._decoders;
		synchronized (decoders)
		{
			decoders.put(decodes, __d);
			
			// Update class cache
			this._classes = decoders.keySet().<Class<?>>toArray(
				new Class<?>[decoders.size()]);
		}
	}
}

