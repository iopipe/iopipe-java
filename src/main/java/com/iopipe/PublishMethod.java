package com.iopipe;

/**
 * This represents the method that should be used to publish events to the
 * IOpipe service.
 *
 * @since 2018/07/23
 */
public enum PublishMethod
{
	/**
	 * Publishes events serially, blocking until the server replies with a
	 * result from the request.
	 */
	SERIAL,
	
	/** End. */
	;
	
	/** The default publish method. */
	static final PublishMethod _DEFAULT =
		PublishMethod.SERIAL;
}

