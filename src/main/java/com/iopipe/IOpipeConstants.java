package com.iopipe;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * This class contains constants which are used by the service.
 *
 * @since 2017/12/17
 */
public interface IOpipeConstants
{
	/** The version for this agent. */
	public static final String AGENT_VERSION =
		"1.9.0";
	
	/** This is used to determine the load time of the service. */
	public static final long LOAD_TIME =
		System.currentTimeMillis();
	
	/** The monotic time in milliseconds when the service was loaded. */
	public static final long LOAD_TIME_NANOS =
		System.nanoTime();
	
	/** The default region to connect to. */
	public static final String DEFAULT_REGION =
		"us-east-1";
	
	/** The regions which are supported by this agent. */
	public static final Set<String> SUPPORTED_REGIONS =
		Collections.<String>unmodifiableSet(new HashSet<>(
			Arrays.<String>asList(
				"ap-northeast-1",
				"ap-southeast-2",
				"eu-west-1",
				"us-east-2",
				"us-west-1",
				"us-west-2")));
	
	/** The default service URL. */
	public static final String DEFAULT_SERVICE_URL =
		IOpipeConstants.defaultServiceUrl();
	
	/** The default URL to request profiler snapshot uploads to. */
	@Deprecated
	public static final String DEFAULT_PROFILER_URL =
		IOpipeConstants.defaultSignerUrl();
	
	/** The default URL to use for the signer. */
	public static final String DEFAULT_SIGNER_URL =
		IOpipeConstants.defaultSignerUrl();
	
	/** The length limit for how long custom metric and label names may be. */
	public static final int NAME_CODEPOINT_LIMIT =
		128;
	
	/** The length limit for how long custom metric values may be. */
	public static final int VALUE_CODEPOINT_LIMIT =
		1024;
	
	/**
	 * Returns the default region which has been chosen to send events and
	 * profiler reports to.
	 *
	 * @return The chosen reason for reports.
	 * @since 2018/03/10
	 */
	public static String chosenRegion()
	{
		try
		{
			// If this region is not supported then always fallback to the
			// default region
			String rv = System.getenv("AWS_REGION");
			if (rv == null || !IOpipeConstants.SUPPORTED_REGIONS.contains(rv))
				return IOpipeConstants.DEFAULT_REGION;
			return rv;
		}
		
		// This should not happen but it may happen during initialization
		catch (SecurityException e)
		{
			return IOpipeConstants.DEFAULT_REGION;
		}
	}
	
	/**
	 * Returns the default profiler URL.
	 *
	 * @return The default profiler URL.
	 * @since 2018/03/10
	 */
	@Deprecated	
	public static String defaultProfilerUrl()
	{
		return IOpipeConstants.defaultSignerUrl();
	}
	
	/**
	 * Returns the default service URL.
	 *
	 * @return The default service URL.
	 * @since 2018/03/10
	 */
	public static String defaultServiceUrl()
	{
		String ar = IOpipeConstants.chosenRegion();
		if (IOpipeConstants.DEFAULT_REGION.equals(ar))
			return "https://metrics-api.iopipe.com/v0/event";
		return "https://metrics-api." + IOpipeConstants.chosenRegion() +
			".iopipe.com/v0/event";
	}
	
	/**
	 * Returns the default signer URL.
	 *
	 * @return The default signer URL.
	 * @since 2018/09/24
	 */
	public static String defaultSignerUrl()
	{
		return "https://signer." + IOpipeConstants.chosenRegion() +
			".iopipe.com/";
	}
}

