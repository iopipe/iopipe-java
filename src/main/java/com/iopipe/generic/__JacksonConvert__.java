package com.iopipe.generic;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.lang.reflect.Type;

/**
 * Converter which uses jackson.
 *
 * @since 2018/08/21
 */
final class __JacksonConvert__
	extends ObjectTranslator
{
	/** The object mapper used. */
	protected final ObjectMapper mapper;
	
	/** The type that is used for the conversion process. */
	protected final JavaType type;
	
	/**
	 * Initializes the translator.
	 *
	 * @param __f The from type.
	 * @param __t The to type.
	 * @since 2018/08/21
	 */
	__JacksonConvert__(Type __f, Type __t)
	{
		super(__f, __t);
		
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
		if (__f == null)
			return null;
		
		return this.mapper.convertValue(__f, this.type);
	}
}

