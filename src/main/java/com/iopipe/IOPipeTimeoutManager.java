package com.iopipe;

/**
 * This class is used to ensure that timeouts during execution are properly
 * handled. This class keeps track of its own tghread
 *
 * @since 2017/12/14
 */
public final class IOPipeTimeoutManager
	implements AutoCloseable
{
	/**
	 * {@inheritDoc}
	 * @since 2017/12/14
	 */
	@Override
	public void close()
	{
		throw new Error("TODO");
	}
	
	/**
	 * This will register a new context which if it is left executing for an
	 * extended period of time, it will register a timeout before the AWS
	 * service terminates the context.
	 *
	 * @param __c The context to register.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/15
	 */
	public void register(IOPipeContext __c)
		throws NullPointerException
	{
		if (__c == null)
			throw new NullPointerException("NARG");
		
		throw new Error("TODO");
	}
}

