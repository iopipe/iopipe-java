package com.iopipe;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 * This represents a custom metric which may have a string or long value,
 * these may be used to add extra data points that are normally not present.
 *
 * @since 2018/01/20
 */
public final class CustomMetric
	implements Comparable<CustomMetric>
{
	/** The name of this metric. */
	protected final String name;
	
	/** The string value of the metric. */
	protected final String stringvalue;
	
	/** The long value of the metric. */
	protected final long longvalue;
	
	/** Has a long value? */
	protected final boolean haslong;
	
	/** String representation. */
	private volatile Reference<String> _string;
	
	/**
	 * Initializes the custom metric with a string value.
	 *
	 * @param __name The metric name.
	 * @param __sv The string value.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/20
	 */
	public CustomMetric(String __name, String __sv)
		throws NullPointerException
	{
		if (__name == null || __sv == null)
			throw new NullPointerException();
		
		this.name = __name;
		this.stringvalue = __sv;
		this.longvalue = 0L;
		this.haslong = false;
	}
	
	/**
	 * Initializes the custom metric with a long value.
	 *
	 * @param __name The metric name.
	 * @param __lv The long value.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/20
	 */
	public CustomMetric(String __name, long __lv)
		throws NullPointerException
	{
		if (__name == null)
			throw new NullPointerException();
		
		this.name = __name;
		this.stringvalue = null;
		this.longvalue = __lv;
		this.haslong = true;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/20
	 */
	@Override
	public int compareTo(CustomMetric __o)
	{
		int rv = this.name.compareTo(__o.name);
		if (rv != 0)
			return rv;
		
		String a = this.stringvalue,
			b = __o.stringvalue;
		if ((a == null) != (b == null))
			if (a == null)
				return -1;
			else
				return 1;
		else if (a == null)
			return 0;
		rv = a.compareTo(b);
		if (rv != 0)
			return rv;
		
		return Long.compare(this.longvalue, __o.longvalue);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/20
	 */
	@Override
	public boolean equals(Object __o)
	{
		if (__o == this)
			return true;
		
		if (!(__o instanceof CustomMetric))
			return false;
		
		CustomMetric o = (CustomMetric)__o;
		return this.name.equals(o.name) &&
			Objects.equals(this.stringvalue, o.stringvalue) &&
			this.haslong == o.haslong &&
			this.longvalue == o.longvalue;
	}
	
	/**
	 * Does this have a long value?
	 *
	 * @return If this has a long value.
	 * @since 2018/01/20
	 */
	public boolean hasLong()
	{
		return this.haslong;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/20
	 */
	@Override
	public int hashCode()
	{
		return (this.name.hashCode() ^
			Objects.hashCode(this.stringvalue) ^
			Long.hashCode(this.longvalue)) ^ (this.haslong ? ~0 : 0);
	}
	
	/**
	 * Does this have a string value?
	 *
	 * @return If this has a string value.
	 * @since 2018/01/20
	 */
	public boolean hasString()
	{
		return this.stringvalue != null;
	}
	
	/**
	 * Returns the long value.
	 *
	 * @return The long value or {@code 0} if there is no value.
	 * @since 2018/01/20
	 */
	public long longValue()
	{
		if (this.haslong)
			return this.longvalue;
		return 0L;
	}
	
	/**
	 * Returns the name of the custom metric.
	 *
	 * @return The custom metric name.
	 * @since 2018/01/20
	 */
	public String name()
	{
		return this.name;
	}
	
	/**
	 * Returns the string value.
	 *
	 * @return The String value or {@code null} if it is not set.
	 * @since 2018/01/20
	 */
	public String stringValue()
	{
		return this.stringvalue;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/20
	 */
	@Override
	public String toString()
	{
		Reference<String> ref = this._string;
		String rv;
		
		if (ref == null || null == (rv = ref.get()))
		{
			String name = this.name,
				stringvalue = this.stringvalue;
			
			if (stringvalue != null)
				rv = String.format("%s=%s", name, stringvalue);
			else
				rv = String.format("%s=%d", name, this.longvalue);
			
			// Cache it
			this._string = new WeakReference<>(rv);
		}
		
		return rv;
	}
}

