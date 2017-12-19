package com.iopipe.examples;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import com.amazonaws.services.lambda.runtime.Context;
import com.iopipe.SimpleRequestStreamHandlerWrapper;

/**
 * This request handler takes the given input stream and lowercases all
 * characters which have been input. It just translates simple ASCII as an
 * example.
 *
 * @since 2017/12/18
 */
public class Lowercase
	extends SimpleRequestStreamHandlerWrapper
{
	/**
	 * {@inheritDoc}
	 * @since 2017/12/18
	 */
	@Override
	protected final void wrappedHandleRequest(InputStream __in,
		OutputStream __out, Context __c)
		throws IOException
	{
		for (;;)
		{
			int c = __in.read();
			
			if (c < 0)
				break;
			
			if (c >= 'A' && c <= 'Z')
				c = (c - 'A') + 'a';
			__out.write(c);
		}
	}
}
