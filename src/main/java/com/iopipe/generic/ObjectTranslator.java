package com.iopipe.generic;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.lang.reflect.Type;

/**
 * This class manages converters to the target type.
 *
 * @since 2018/08/20
 */
public abstract class ObjectTranslator
{
	/** The type to convert to. */
	protected final Type to;
	
	/**
	 * Initializes a translator to the given type.
	 *
	 * @param __t The target type
	 * @throws NullPointerException On null arguments.
	 * @since 2018/08/20
	 */
	private ObjectTranslator(Type __t)
		throws NullPointerException
	{
		if (__t == null)
			throw new NullPointerException();
		
		this.to = __t;
	}
	
	/**
	 * Translates from the given object to the destination object.
	 *
	 * @param __f The object to translate, if this is null then no object
	 * is translated and null is returned.
	 * @return The result of the translation, if {@code __f} is null then this
	 * will return null.
	 * @since 2018/08/20
	 */
	public abstract Object translate(Object __f);
	
	/**
	 * {@inheritDoc}
	 * @since 2018/08/20
	 */
	@Override
	public final boolean equals(Object __o)
	{
		if (this == __o)
			return true;
		
		if (!(__o instanceof ObjectTranslator))
			return false;
		
		ObjectTranslator o = (ObjectTranslator)__o;
		return this.to.equals(o.to);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/08/20
	 */
	@Override
	public final int hashCode()
	{
		return this.to.hashCode();
	}
	
	/**
	 * Creates a translator for converting objects to the given type.
	 *
	 * @param __t The to class.
	 * @return A translator to translate from one class to the other.
	 * @since 2018/08/20
	 */
	public static final ObjectTranslator translator(Type __t)
		throws NullPointerException
	{
		if (__t == null)
			throw new NullPointerException();
		
		return new __JacksonConvert__(__t);
	}
	
	/**
	 * Converter which uses jackson.
	 *
	 * @since 2018/08/21
	 */
	private static final class __JacksonConvert__
		extends ObjectTranslator
	{
		/** The object mapper used. */
		protected final ObjectMapper mapper;
		
		/** The type that is used for the conversion process. */
		protected final JavaType type;
		
		/**
		 * Initializes the translator.
		 *
		 * @param __t The to type.
		 * @since 2018/08/21
		 */
		private __JacksonConvert__(Type __t)
		{
			super(__t);
			
			// Setup mapper
			ObjectMapper mapper = new ObjectMapper();
			this.mapper = mapper;
			
			// The case mappings for JSON are treated as case insensitive
			mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES,
				true);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);
			
			// Dates must be constructed
			mapper.registerModule(new JodaModule());
			
			// Setup type that can be used to handle the given type
			TypeFactory factory = mapper.getTypeFactory();
			JavaType type;
			this.type = (type = factory.constructType(__t));
		}
	
		/**
		 * {@inheritDoc}
		 * @since 2018/08/21
		 */
		@Override
		public final Object translate(Object __f)
		{
			return this.mapper.convertValue(__f, this.type);
		}
	}
}

