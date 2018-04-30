package com.iopipe.plugin.eventinfo;

import com.iopipe.CustomMetric;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonStructure;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonWriter;
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
		
		// Obtain the decoder that should be used
		EventInfoDecoder decoder;
		Map<Class<?>, EventInfoDecoder> decoders = this._decoders;
		synchronized (decoders)
		{
			decoder = decoders.get(match);
		}
		
		// Custom metrics to return
		List<CustomMetric> rv = new ArrayList<>();
		
		// Used to name values
		String eventtype = decoder.eventType();
		
		// Handle each rule
		for (Rule rule : decoder.rules())
		{
			// If the value is wrapped in an optional then get the value it
			// contains
			Object val = rule.getter().apply(__o);
			if (val instanceof Optional)
				val = ((Optional<?>)val).orElse(null);
			
			// Perform function on the value
			if (val != null)
			{
				// Determine key name to use
				String name = "@iopipe/event-info." + eventtype + "." +
					rule.key();
				
				// Add value
				CustomMetric cm;
				if (val instanceof Number)
					cm = new CustomMetric(name, ((Number)val).longValue());
				else if (val instanceof Map || val instanceof List ||
					val instanceof Set)
					try (StringWriter w = new StringWriter())
					{
						// Convert value
						try (JsonWriter j = Json.createWriter(w))
						{
							j.write((JsonStructure)__convert(val));
						}
						
						// Build
						cm = new CustomMetric(name, w.toString());
					}
					catch (IOException e)
					{
						continue;
					}
				else
					cm = new CustomMetric(name, val.toString());
				rv.add(cm);
			}
		}
		
		// Add type and records
		rv.add(new CustomMetric("@iopipe/event-info.eventType", eventtype));
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
	
	/**
	 * Converts an object to a JSON type recursively so that.
	 *
	 * @param __v The value to convert.
	 * @return The converted value.
	 * @since 2018/04/29
	 */
	private static JsonValue __convert(Object __v)
	{
		// Map null
		if (__v == null)
			return JsonValue.NULL;
		
		// Already translated
		else if (__v instanceof JsonValue)
			return (JsonValue)__v;
		
		// Is true
		else if (Boolean.TRUE.equals(__v))
			return JsonValue.TRUE;
		
		// Is false
		else if (Boolean.FALSE.equals(__v))
			return JsonValue.FALSE;
		
		// Is a number
		else if (__v instanceof Number)
			return Json.createArrayBuilder().add(((Number)__v).doubleValue()).
				build().get(0);
		
		// Lists and sets
		else if (__v instanceof List || __v instanceof Set)
		{
			JsonArrayBuilder jab = Json.createArrayBuilder();
			
			for (Object s : (Iterable)__v)
				jab.add(__convert(s));
			
			return jab.build();
		}
		
		// Map
		else if (__v instanceof Map)
		{
			JsonObjectBuilder job = Json.createObjectBuilder();
			
			for (Map.Entry<Object, Object> e :
				((Map<Object, Object>)__v).entrySet())
				job.add(Objects.toString(e.getKey(), ""),
					__convert(e.getValue()));
			
			return job.build();
		}
		
		// Unknown, just treat it as a string
		else
			return Json.createArrayBuilder().add(__v.toString()).build().
				get(0);
	}
}

