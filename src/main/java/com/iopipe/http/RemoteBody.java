package com.iopipe.http;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonStructure;

/**
 * This is the base for requests and results which both contain bodies.
 *
 * @since 2018/02/24
 */
public abstract class RemoteBody
{
	/** The mime type for JSON. */
	public static final String MIMETYPE_JSON =
		"application/json; charset=utf-8";
	
	/** The mimetype of the body. */
	protected final String mimetype;
	
	/** The data which makes up the body. */
	private final byte[] _body;
	
	/** String representation of the body. */
	private Reference<String> _string;
	
	/** JSON representation of the body. */
	private Reference<JsonStructure> _json;
	
	/** toString() representation. */
	private Reference<String> _tostring;
	
	/** Hash code for this body. */
	private int _hash;
	
	/**
	 * Initializes the body with the given data.
	 *
	 * @param __t The mimetype of the body.
	 * @param __b The data making up the body.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/02/24
	 */
	RemoteBody(String __t, byte[] __b)
		throws NullPointerException
	{
		this(__t, __b, 0, __b.length);
	}
	
	/**
	 * Initializes the body with the given data.
	 *
	 * @param __t The mimetype of the body.
	 * @param __b The data making up the body.
	 * @param __o The offset.
	 * @param __l The length.
	 * @throws ArrayIndexOutOfBoundsException If the offset and/or length
	 * exceed the array bounds or are negative.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/02/24
	 */
	RemoteBody(String __t, byte[] __b, int __o, int __l)
		throws ArrayIndexOutOfBoundsException, NullPointerException
	{
		if (__t == null || __b == null)
			throw new NullPointerException();
		int blen = __b.length;
		if (__o < 0 || __l < 0 || (__o + __l) > blen)
			throw new ArrayIndexOutOfBoundsException();
		
		this.mimetype = __t;
		this._body = Arrays.copyOfRange(__b, __o, __o + __l);
	}
	
	/**
	 * Initializes the body with the given string.
	 *
	 * @param __t The mimetype of the body.
	 * @param __s The string to initialize the body with.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/02/24
	 */
	RemoteBody(String __t, String __s)
		throws NullPointerException
	{
		if (__t == null || __s == null)
			throw new NullPointerException();
		
		this.mimetype = __t;
		
		// This could fail with an unsupported encoding but it should never
		// happen ever
		byte[] body = null;
		try
		{
			body = __s.getBytes("utf-8");
		}
		
		// Could fail, but never should ever but just in case
		catch (UnsupportedEncodingException e)
		{
			body = __s.getBytes();
		}
		
		this._body = body;
	}
	
	/**
	 * Returns the body value as a byte array.
	 *
	 * @return The bytes making up the body.
	 * @since 2018/02/24
	 */
	public final byte[] body()
	{
		return this._body.clone();
	}
	
	/**
	 * Returns the body as a string.
	 *
	 * @return The body as a string.
	 * @since 2018/02/24
	 */
	public final String bodyAsString()
	{
		Reference<String> ref = this._string;
		String rv;
		
		if (ref == null || null == (rv = ref.get()))
		{
			try
			{
				rv = new String(this._body, "utf-8");
			}
			catch (UnsupportedEncodingException e)
			{
				rv = new String(this._body);
			}
			
			this._string = new WeakReference<>(rv);
		}
		
		return rv;
	}
	
	/**
	 * Returns the body as a JSON structure.
	 *
	 * @return The body as a JSON structure.
	 * @throws JsonException If the body is not a valid structure.
	 * @since 2018/02/24
	 */
	public final JsonStructure bodyAsJsonStructure()
		throws JsonException
	{
		Reference<JsonStructure> ref = this._json;
		JsonStructure rv;
		
		if (ref == null || null == (rv = ref.get()))
		{
			ByteArrayInputStream bais = new ByteArrayInputStream(this._body);
			
			InputStreamReader r;
			try
			{
				r = new InputStreamReader(bais, "utf-8");
			}
			catch (UnsupportedEncodingException e)
			{
				r = new InputStreamReader(bais);
			}
		
			this._json = new WeakReference<>((rv =
				Json.createReader(r).read()));
		}
		
		return rv;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/02/24
	 */
	@Override
	public boolean equals(Object __o)
	{
		if (__o == this)
			return true;
		
		if (!(__o instanceof RemoteBody))
			return false;
		
		RemoteBody o = (RemoteBody)__o;
		int ha = this.hashCode(),
			hb = o.hashCode();
		return ha == hb &&
			Arrays.equals(this._body, o._body) &&
			this.mimetype.equals(o.mimetype);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/02/24
	 */
	@Override
	public int hashCode()
	{
		int hash = this._hash;
		if (hash == 0)
			this._hash = (hash = Arrays.hashCode(this._body) ^
				this.mimetype.hashCode());
		return hash;
	}
	
	/**
	 * Returns the MIME type of the body.
	 *
	 * @return The body mime type.
	 * @since 2018/02/24
	 */
	public final String mimeType()
	{
		return this.mimetype;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/02/24
	 */
	@Override
	public String toString()
	{
		Reference<String> ref = this._tostring;
		String rv;
		
		if (ref == null || null == (rv = ref.get()))
			this._tostring = new WeakReference<>((rv =
				String.format("{type=%s, body=%d bytes}", this.mimetype,
					this._body.length)));
		
		return rv;
	}
}

