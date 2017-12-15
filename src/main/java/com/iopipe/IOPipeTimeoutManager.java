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
	 * Initializes the thread which checks for timeout.
	 *
	 * @since 2017/12/15
	 */
	IOPipeTimeoutManager()
	{
	}
	
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
	 * This is used to indicate that the specified execution of the given
	 * context has finished execution.
	 *
	 * @param __c The context which has finished.
	 * @param __exec The execution number of the context.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/15
	 */
	public void finished(IOPipeContext __c, int __exec)
		throws NullPointerException
	{
		if (__c == null)
			throw new NullPointerException("NARG");
		
		throw new Error("TODO");
	}
	
	/**
	 * This will register a new context which if it is left executing for an
	 * extended period of time, it will register a timeout before the AWS
	 * service terminates the context.
	 *
	 * @param __c The context to register.
	 * @param __exec The current execution count for the context.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/12/15
	 */
	public void register(IOPipeContext __c, int __exec)
		throws NullPointerException
	{
		if (__c == null)
			throw new NullPointerException("NARG");
		
		throw new Error("TODO");
	}
}

