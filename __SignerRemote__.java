package com.iopipe;

/**
 * This contains information on the remote.
 *
 * @since 2018/07/27
 */
final class __Remote__
{
	/** Is this remote valid? */
	public final boolean valid;
	
	/** The URL to upload to. */
	public final String url;
	
	/** The JWT access token. */
	public final String jwtaccesstoken;
	
	/**
	 * Initializes the remote information.
	 *
	 * @param __valid Is this valid?
	 * @param __url The URL to upload to.
	 * @param __at The access token.
	 * @since 2018/07/22
	 */
	__Remote__(boolean __valid, String __url, String __at)
	{
		this.valid = __valid;
		this.url = __url;
		this.jwtaccesstoken = __at;
	}
}

