package com.iopipe.http;

import com.iopipe.IOpipeConstants;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.Objects;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.pmw.tinylog.Logger;
import org.baswell.niossl.SSLSocketChannel;

/**
 * This class sends requests to the remote server.
 *
 * @since 2018/11/14
 */
public final class ServiceConnection
	implements RemoteConnection
{
	/** Execution pool, used to perform long running tasks. */
	private static final ThreadPoolExecutor _THREAD_POOL =
		new ThreadPoolExecutor(250, 2000, 25, TimeUnit.SECONDS,
			new LinkedBlockingQueue<Runnable>());
	
	/** The socket address. */
	protected final InetSocketAddress sockaddr;
	
	/** The host to send to (in the request). */
	private final byte[] _hosty;
	
	/** The path and query used. */
	private final byte[] _pathy;
	
	/** The authentication token, pre-encoded as bytes. */
	private final byte[] _auth;
	
	/**
	 * Initializes the service connection.
	 *
	 * @param __url The URL to connect to.
	 * @param __auth The authentication code, is optional.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/14
	 */
	public ServiceConnection(String __url, String __auth)
		throws NullPointerException
	{
		if (__url == null)
			throw new NullPointerException();
		
		try
		{
			// Parse URI to extract all the needed bits, assumes HTTPS
			URI uri = URI.create(__url);
			
			// Initialize connection address
			String host = uri.getHost();
			int port = uri.getPort();
			this.sockaddr = new InetSocketAddress(host, (port < 0 ? 443 : port));
			this._hosty = host.getBytes("utf-8");
			
			// Obtain the raw values because we want to keep stuff such as
			// %20, it has to be kept how it is
			String path = uri.getRawPath(),
				query = uri.getRawQuery();
			this._pathy = (path + (query == null ? "" :
				"?" + query)).getBytes("utf-8");
			
			// This needs to be checked to make sure it does not contain
			// newlines and such
			if (__auth != null)
			{
				for (int i = 0, n = __auth.length(); i < n; i++)
					switch (__auth.charAt(i))
					{
						case '\r':
						case '\n':
							throw new RemoteException("Authorization token has newline character.");
					}
				this._auth = __auth.getBytes("utf-8");
			}
			else
				this._auth = null;
		}
		catch (NullPointerException|IllegalArgumentException|IOException e)
		{
			throw new RemoteException("Could not parse URL or authentication code.", e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/11/14
	 */
	@Override
	public final RemoteResult send(RequestType __t, RemoteRequest __r)
		throws NullPointerException, RemoteException
	{
		if (__t == null || __r == null)
			throw new NullPointerException();
		
		try (SocketChannel basechan = SocketChannel.open(this.sockaddr))
		{
			// Make it non-blocking
			basechan.configureBlocking(false);
			
			// Needs to explicitly be initialized!!
			SSLContext sslc = SSLContext.getInstance("TLSv1.2");
			sslc.init(null, null, null);
			SSLEngine ssle = sslc.createSSLEngine();
			
			// We are NOT a server
			ssle.setUseClientMode(true);
			
			// Open SSL connection to server
			try (SocketChannel chan = new SSLSocketChannel(basechan, ssle,
				_THREAD_POOL, null))
			{
				// Finish connecting now
				Logger.debug("Will finish connecting");
				chan.finishConnect();
				
				// Build request to send to the server
				byte[] sendy;
				try (ByteArrayOutputStream baos =
					new ByteArrayOutputStream(1048576);
					PrintStream out = new PrintStream(baos, true, "utf-8"))
				{
					// Start HTTP request
					out.print(__t.name());
					out.print(' ');
					out.write(this._pathy);
					out.print(" HTTP/1.1\r\n");
					
					// Our user agent
					out.print("User-Agent: IOpipeJavaAgent/");
					out.print(IOpipeConstants.AGENT_VERSION);
					out.print("\r\n");
					
					// The remote host
					out.print("Host: ");
					out.write(this._hosty);
					out.print("\r\n");
					
					// Authorization token?
					byte[] auth = this._auth;
					if (auth != null)
					{
						out.print("Authorization: ");
						out.write(auth);
						out.print("\r\n");
					}
					
					// Content type
					String mime = __r.mimeType();
					if (mime != null)
					{
						out.print("Content-Type: ");
						out.print(mime);
						out.print("\r\n");
					}
					
					// Write content length
					byte[] content = __r.body();
					out.print("Content-Length: ");
					out.print(content.length);
					out.print("\r\n");
					
					// End of properties
					out.print("\r\n");
					
					// Send the body
					out.write(content);
					
					// Flush to send it
					out.flush();
					sendy = baos.toByteArray();
				}
				
				// Debug
				Logger.debug("HTTP Request: {}", new String(sendy, "utf-8"));
				
				// Send all the bytes to the remote end
				ByteBuffer bbsendy = ByteBuffer.wrap(sendy);
				while (bbsendy.hasRemaining())
				{
					Logger.debug("Sending: {}", bbsendy);
					chan.write(bbsendy);
				}
				
				// We no longer need to send to the remote side
				Logger.debug("Shutting down");
				chan.shutdownOutput();
				Logger.debug("Shut down");
				
				// Just try to read a bunch into some byte buffer
				byte[] rawread = new byte[16916];
				ByteBuffer read = ByteBuffer.wrap(rawread);
				chan.read(read);
				
				Logger.debug("BB got: {}", read);
				
				// Flip it because we want to actually read it now
				read.flip();
				
				Logger.debug("BB said: {}", new String(rawread, "utf-8"));
				
				return new RemoteResult(
					999,
					"nothing-yet",
					rawread);
			}
		}
		
		// Failed to read/write
		catch (IOException|NoSuchAlgorithmException|KeyManagementException e)
		{
			System.err.println("************** OOPS! ***************");
			e.printStackTrace();
			System.err.println("************************************");
			
			throw new RemoteException("Could not send request.", e);
		}
	}
}

