package com.iopipe.elsewhere;

/**
 * This is a package private class for testing.
 *
 * @since 2018/08/13
 */
class PackagePrivateClass
{
	/**
	 * Private instance method.
	 *
	 * @param __s Input.
	 * @return Output.
	 * @since 2018/08/13
	 */
	private String instancePrivate(String __s)
	{
		return (__s != null ? __s.toLowerCase() : null);
	}
	
	/**
	 * Package private instance method.
	 *
	 * @param __s Input.
	 * @return Output.
	 * @since 2018/08/13
	 */
	String instancePackagePrivate(String __s)
	{
		return (__s != null ? __s.toLowerCase() : null);
	}
	
	/**
	 * Protected instance method.
	 *
	 * @param __s Input.
	 * @return Output.
	 * @since 2018/08/13
	 */
	protected String instanceProtected(String __s)
	{
		return (__s != null ? __s.toLowerCase() : null);
	}
	
	/**
	 * Public instance method.
	 *
	 * @param __s Input.
	 * @return Output.
	 * @since 2018/08/13
	 */
	public String instancePublic(String __s)
	{
		return (__s != null ? __s.toLowerCase() : null);
	}
	
	/**
	 * Private static method.
	 *
	 * @param __s Input.
	 * @return Output.
	 * @since 2018/08/13
	 */
	private static String staticPrivate(String __s)
	{
		return (__s != null ? __s.toLowerCase() : null);
	}
	
	/**
	 * Package private static method.
	 *
	 * @param __s Input.
	 * @return Output.
	 * @since 2018/08/13
	 */
	static String staticPackagePrivate(String __s)
	{
		return (__s != null ? __s.toLowerCase() : null);
	}
	
	/**
	 * Protected static method.
	 *
	 * @param __s Input.
	 * @return Output.
	 * @since 2018/08/13
	 */
	protected static String staticProtected(String __s)
	{
		return (__s != null ? __s.toLowerCase() : null);
	}
	
	/**
	 * Public static method.
	 *
	 * @param __s Input.
	 * @return Output.
	 * @since 2018/08/13
	 */
	public static String staticPublic(String __s)
	{
		return (__s != null ? __s.toLowerCase() : null);
	}
}

