package com.iopipe.plugin.eventinfo;

import com.iopipe.CustomMetric;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.pmw.tinylog.Logger;

/**
 * This class manages and initializes the decoders for event information
 * extraction.
 *
 * @since 2018/04/22
 */
public final class EventInfoDecoders
{
	/** Decoders which have been registered. */
	private final Map<String, EventInfoDecoder> _decoders =
		new LinkedHashMap<>();
	
	/** Cache of strings that have been registered for type lookup. */
	private volatile String[] _clcache =
		new String[0];
	
	/** Cache of decoders that are available based on the class. */
	private volatile EventInfoDecoder[] _decache =
		new EventInfoDecoder[0];
	
	/** The last decoder which was used. */
	private EventInfoDecoder _last;
	
	/** The last class used. */
	private String _lastname;
	
	/**
	 * Initializes the event decoders with the default decoders.
	 *
	 * @since 2018/04/23
	 */
	public EventInfoDecoders()
	{
		// Register every internal test, but allow for a means so the internal
		// tests are only included if their classes load properly. This is
		// to prevent for cases where the client has shaded their JAR
		// incorrectly due to AWS changes, to prevent the lambda not executing
		// with an exception.
		for (String i : new String[]{
			"com.iopipe.plugin.eventinfo.APIGatewayDecoder",
			"com.iopipe.plugin.eventinfo.CloudFrontDecoder",
			"com.iopipe.plugin.eventinfo.KinesisDecoder",
			"com.iopipe.plugin.eventinfo.FirehoseDecoder",
			"com.iopipe.plugin.eventinfo.S3Decoder",
			"com.iopipe.plugin.eventinfo.ScheduledDecoder",
			"com.iopipe.plugin.eventinfo.SNSDecoder",
			"com.iopipe.plugin.eventinfo.SQSDecoder"})
			try
			{
				this.register(i);
			}
			catch (ClassNotFoundException e)
			{
				Logger.error(e, "Failed to register event info decoder " + i +
					", some events might not be decodeable by the event-info" +
					"plugin.");
			}
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
		return this.decode(__o, null);
	}
	
	/**
	 * Decodes the specified object and returns an array containing custom
	 * metrics to be added to the report.
	 *
	 * @param __o The object to report against.
	 * @param __d Optional output for the decoder which was used.
	 * @return The custom metrics which detail the object.
	 * @since 2018/07/17
	 */
	public final CustomMetric[] decode(Object __o, EventInfoDecoder[] __d)
	{
		// If this is the null object then it is rather pointless to try and
		// decode it
		if (__o == null)
			return new CustomMetric[0];
		
		// Search for the decoder to use for the input object's class
		EventInfoDecoder decoder = this.getDecoder(__o.getClass());
		
		// None found, has no result
		if (decoder == null)
			return new CustomMetric[0];
		
		// Record the used decoder
		if (__d != null && __d.length > 0)
			__d[0] = decoder;
		
		// Handle all input values
		ValueAcceptor a = new ValueAcceptor(decoder.eventType());
		decoder.accept(a, __o);
		
		return a.get();
	}
	
	/**
	 * Returns the the decoder which can decode the given class.
	 *
	 * @param __cl The input class to check.
	 * @return The decoder which was found for the class or {@code null} if
	 * none is available.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/09/20
	 */
	public final EventInfoDecoder getDecoder(Class<?> __cl)
		throws NullPointerException
	{
		if (__cl == null)
			throw new NullPointerException();
		
		// Get the name of this class
		String clname = __cl.getName();
		
		// Check to see if the last decoder which was found matches this
		// type so we do not need to look at every single class again if it is
		// the same
		EventInfoDecoder last = this._last;
		if (last != null)
		{
			String decodestype = this._lastname;
			
			// Same as the last class?
			if (clname.equals(decodestype))
				return last;
		}
		
		// Go through the cached set of classes and decoders and check each
		// individual class
		String[] clcache = this._clcache;
		EventInfoDecoder[] decache = this._decache;
		for (int n = Math.min(clcache.length, decache.length),
			i = 0; i < n; i++)
		{
			String maybe = clcache[i];
			
			// Same name as class
			if (maybe != null && maybe.equals(clname))
			{
				EventInfoDecoder decoder = decache[i];
				
				// Cache this result so that if the same request is made it
				// more quickly recycles it rather than searching through the
				// arrays
				this._last = decoder;
				this._lastname = clname;
				
				return decoder;
			}
		}
		
		// Not found
		return null;
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
		String[] decodes = __d.decodes();
		if (decodes == null || decodes.length == 0)
			throw new IllegalStateException("Decoder decodes no class.");
		
		// Register it
		Map<String, EventInfoDecoder> decoders = this._decoders;
		synchronized (decoders)
		{
			for (String s : decodes)
				decoders.put(s, __d);
			
			// Update class cache
			int ds = decoders.size();
			this._clcache = decoders.keySet().<String>toArray(
				new String[ds]);
			this._decache = _decoders.values().<EventInfoDecoder>toArray(
				new EventInfoDecoder[ds]);
		}
	}
	
	/**
	 * Registers the given class as an event info decoder.
	 *
	 * @param __cl The class which implements the decoder.
	 * @throws ClassNotFoundException If the class could not be found or
	 * initialized.
	 * @throws IllegalStateException If the decoder decodes no class.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/09/18
	 */
	public final void register(Class<? extends EventInfoDecoder> __cl)
		throws ClassNotFoundException, IllegalStateException,
			NullPointerException
	{
		if (__cl == null)
			throw new NullPointerException();
		
		try
		{
			this.register(__cl.newInstance());
		}
		catch (LinkageError|IllegalAccessException|InstantiationException e)
		{
			throw new ClassNotFoundException(
				"Failed to load class " + __cl + ".", e);
		}
	}
	
	/**
	 * Registers the given class, which is dynamically looked up, as an event
	 * info decoder.
	 *
	 * @param __cl The class which implements the decoder.
	 * @throws ClassNotFoundException If the class could not be found or it
	 * could not be initialized properly.
	 * @throws IllegalStateException If the decoder decodes no class.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/09/18
	 */
	public final void register(String __cl)
		throws ClassNotFoundException, IllegalStateException,
			NullPointerException
	{
		if (__cl == null)
			throw new NullPointerException();
		
		try
		{
			this.register(Class.forName(__cl).<EventInfoDecoder>asSubclass(
				EventInfoDecoder.class));
		}
		catch (ClassCastException|ClassNotFoundException|LinkageError e)
		{
			throw new ClassNotFoundException(
				"Failed to load class " + __cl + ".", e);
		}
	}
}

