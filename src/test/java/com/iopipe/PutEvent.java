package com.iopipe;

/**
 * Represents any data that was PUT to the server.
 *
 * @since 2108/07/16
 */
public final class PutEvent
	implements Event
{
	/** Event data. */
	private final byte[] _data;
	
	/**
	 * Initializes the put event.
	 *
	 * @param __d The data which was put to the remote end.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/07/16
	 */
	public PutEvent(byte[] __d)
		throws NullPointerException
	{
		if (__d == null)
			throw new NullPointerException();
		
		this._data = __d.clone();
	}
	
	/**
	 * Returns the data that was put.
	 *
	 * @return The data which was put.
	 * @since 2018/09/26
	 */
	public final byte[] data()
	{
		return this._data.clone();
	}
	
	/**
	 * The length of the put data.
	 *
	 * @return The length of the data.
	 * @since 2018/07/16
	 */
	public final int length()
	{
		return this._data.length;
	}
}

