package com.iopipe;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
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
		"0.4";
	
	/** This is used to determine the load time of the service. */
	public static final long LOAD_TIME =
		System.currentTimeMillis();
	
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
}

