package com.iopipe;

/**
 * This class contains constants which are used by the service.
 *
 * @since 2017/12/17
 */
public interface IOPipeConstants
{
	/** The version for this agent. */
	public static final String AGENT_VERSION =
		"1.0-SNAPSHOT";
	
	/** This is used to determine the load time of the service. */
	public static final long LOAD_TIME =
		System.currentTimeMillis();
}

