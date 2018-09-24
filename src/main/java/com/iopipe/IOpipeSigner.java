package com.iopipe;

import com.iopipe.http.RemoteException;
import com.iopipe.http.RemoteResult;

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
	 * Returns the access token which is used to access the uploaded data,
	 * this will block until one is available or if none is available.
	 *
	 * @return The access token or {@code null} if it is not valid or not
	 * available.
	 * @since 2018/09/24
	 */
	public final String accessToken()
	{
		throw new Error("TODO");
	}
	
	/**
	 * Puts the given bytes to the signer.
	 *
	 * @param __b The bytes to post.
	 * @return The result of the upload.
	 * @throws NullPointerException On null arguments.
	 * @throws RemoteException If posting failed.
	 * @since 2018/09/24
	 */
	public final RemoteResult put(byte[] __b)
		throws NullPointerException, RemoteException
	{
		if (__b == null)
			throw new NullPointerException();
		
		return this.put(__b, 0, __b.length);
	}
	
	/**
	 * Puts the given bytes to the signer.
	 *
	 * @param __b The bytes to post.
	 * @param __o The offset into the array.
	 * @param __l The number of bytes to post.
	 * @return The result of the upload.
	 * @throws IndexOutOfBoundsException If the offset and/or length are
	 * negative or exceed the array bounds.
	 * @throws NullPointerException On null arguments.
	 * @throws RemoteException If posting failed.
	 * @since 2018/09/24
	 */
	public final RemoteResult put(byte[] __b, int __o, int __l)
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

