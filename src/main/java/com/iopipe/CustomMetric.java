package com.iopipe;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 * This represents a custom metric which may have a string and/or double value.
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
	
	/** The double value of the metric. */
	protected final double doublevalue;
	
	/** Has a double value? */
	protected final boolean hasdouble;
	
	/** String representation. */
	private volatile Reference<String> _string;
	
	/**
	 * Initializes the custom metric with a string value.
	 *
	 * @param __name The matric name.
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
		this.doublevalue = Double.NaN;
		this.hasdouble = false;
	}
	
	/**
	 * Initializes the custom metric with a double value.
	 *
	 * @param __name The matric name.
	 * @param __dv The double value.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/20
	 */
	public CustomMetric(String __name, double __dv)
		throws NullPointerException
	{
		if (__name == null)
			throw new NullPointerException();
		
		this.name = __name;
		this.stringvalue = null;
		this.doublevalue = __dv;
		this.hasdouble = true;
	}
	
	/**
	 * Initializes the custom metric with a string and double value.
	 *
	 * @param __name The matric name.
	 * @param __sv The string value.
	 * @param __dv The double value.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/20
	 */
	public CustomMetric(String __name, String __sv, double __dv)
		throws NullPointerException
	{
		if (__name == null || __sv == null)
			throw new NullPointerException();
		
		this.name = __name;
		this.stringvalue = __sv;
		this.doublevalue = __dv;
		this.hasdouble = true;
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
		
		return Double.compare(this.doublevalue, __o.doublevalue);
	}
	
	/**
	 * Returns the double value.
	 *
	 * @return The double value or {@link Double#NaN} if there is no value.
	 * @since 2018/01/20
	 */
	public double doubleValue()
	{
		if (this.hasdouble)
			return this.doublevalue;
		return Double.NaN;
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
			this.hasdouble == o.hasdouble &&
			this.doublevalue == o.doublevalue;
	}
	
	/**
	 * Does this have a double value?
	 *
	 * @return If this has a double value.
	 * @since 2018/01/20
	 */
	public boolean hasDouble()
	{
		return this.hasdouble;
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
			Double.hashCode(this.doublevalue)) ^ (this.hasdouble ? ~0 : 0);
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
			double doublevalue = this.doublevalue;
			
			if (stringvalue != null)
				if (this.hasdouble)
					rv = String.format("%s={%s, %f}", name, stringvalue,
						doublevalue);
				else
					rv = String.format("%s=%s", name, stringvalue);
			else
				rv = String.format("%s=%f", name, doublevalue);
			
			// Cache it
			this._string = new WeakReference<>(rv);
		}
		
		return rv;
	}
}

