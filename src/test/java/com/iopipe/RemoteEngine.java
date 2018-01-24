package com.iopipe;

/**
 * This runs all of the tests but interacts directly 
 *
 * @since 2018/01/23
 */
public class RemoteEngine
	extends Engine
{
	/**
	 * Initializes the engine.
	 *
	 * @since 2018/01/23
	 */
	public RemoteEngine()
	{
		super("remote");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/23
	 */
	@Override
	protected IOpipeConfigurationBuilder generateConfig(Single __s)
	{
		if (__s == null)
			throw new NullPointerException();
		
		// Use the system configuration, it will be modified accordingly
		return new IOpipeConfigurationBuilder(IOpipeConfiguration.byDefault());
	}
}

