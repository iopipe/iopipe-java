package com.iopipe;

import com.iopipe.http.RemoteException;

/**
 * This class handles signed requests which are used to upload data to IOpipe.
 *
 * When the class is created it will setup and run the background thread which
 * obtains the signer information accordingly.
 *
 * @since 2018/09/24
 */
public final class IOpipeSigner
{
	/**
	 * Initializes the signer.
	 *
	 * @since 2018/09/24
	 */
	IOpipeSigner()
	{
	}
	
	/**
	 * Posts the given bytes to the signer.
	 *
	 * @param __b The bytes to post.
	 * @throws NullPointerException On null arguments.
	 * @throws RemoteException If posting failed.
	 * @since 2018/09/24
	 */
	public final void post(byte[] __b)
		throws NullPointerException, RemoteException
	{
		if (__b == null)
			throw new NullPointerException();
		
		this.post(__b, 0, __b.length);
	}
	
	/**
	 * Posts the given bytes to the signer.
	 *
	 * @param __b The bytes to post.
	 * @param __o The offset into the array.
	 * @param __l The number of bytes to post.
	 * @throws IndexOutOfBoundsException If the offset and/or length are
	 * negative or exceed the array bounds.
	 * @throws NullPointerException On null arguments.
	 * @throws RemoteException If posting failed.
	 * @since 2018/09/24
	 */
	public final void post(byte[] __b, int __o, int __l)
		throws IndexOutOfBoundsException, NullPointerException,
			RemoteException
	{
		if (__b == null)
			throw new NullPointerException();
		if (__o < 0 || __l < 0 || (__o + __l) > __b.length)
			throw new IndexOutOfBoundsException();
		
		throw new Error("TODO");
	}
}

