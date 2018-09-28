package com.iopipe;

import com.iopipe.http.RemoteBody;
import com.iopipe.http.RemoteConnection;
import com.iopipe.http.RemoteConnectionFactory;
import com.iopipe.http.RemoteException;
import com.iopipe.http.RemoteRequest;
import com.iopipe.http.RemoteResult;
import com.iopipe.http.RequestType;
import java.io.StringWriter;
import java.util.concurrent.atomic.AtomicReference;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;
import org.pmw.tinylog.Logger;

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
	/** The extension to use. */
	protected final String extension;
	
	/** The AWS ARN. */
	protected final String awsarn;
	
	/** The AWS Request ID. */
	protected final String awsrequestid;
	
	/** The timestamp of the execution. */
	protected final long timestamp;
	
	/** The configuration. */
	protected final IOpipeConfiguration config;
	
	/** The remote to access. */
	private final AtomicReference<__SignerRemote__> _remote =
		new AtomicReference<>();
	
	/**
	 * Initializes the signer.
	 *
	 * @param __ext The extension to use.
	 * @param __arn The AWS ARN.
	 * @param __reqid The AWS Request ID.
	 * @param __ts The timestamp.
	 * @param __conf The configuration.
	 * @throws NullPointerException If no config was specified.
	 * @since 2018/09/24
	 */
	IOpipeSigner(String __ext, String __arn, String __reqid, long __ts,
		IOpipeConfiguration __conf)
		throws NullPointerException
	{
		if (__conf == null)
			throw new NullPointerException();
		
		this.config = __conf;
		this.extension = __ext;
		this.awsarn = __arn;
		this.awsrequestid = __reqid;
		this.timestamp = __ts;
		
		// Need to determine which server to send to, can be done in another
		// thread
		Thread getter = new Thread(__Shared__._SERVICE_THREAD_GROUP,
			this::__getRemote, "IOpipe-SignerGetURL");
		getter.setDaemon(true);
		getter.start();
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
		__SignerRemote__ sr = this.__awaitRemote();
		if (sr == null)
			return null;
		return sr.jwtaccesstoken;
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
		
		// Await remote data to send to
		__SignerRemote__ remote = this.__awaitRemote();
		if (remote == null)
			throw new RemoteException("Could not access the signer.");
		
		// Build request to send to server
		RemoteRequest request = new RemoteRequest("", __b, __o, __l);
		
		// Send request
		RemoteResult result = this.config.getRemoteConnectionFactory().connect(
			remote.url, null).send(RequestType.PUT,
			request);
		
		// Debug result
		Logger.debug("Signer upload returned result {}.", result);
		
		return result;
	}
	
	/**
	 * Waits for a response from the remote signer.
	 *
	 * @return The result from the signer or {@code null} if it is not
	 * available.
	 * @since 2018/09/24
	 */
	private final __SignerRemote__ __awaitRemote()
	{
		AtomicReference<__SignerRemote__> atom = this._remote;
		
		// Burn CPU for a bit waiting for the remote
		__SignerRemote__ rv;
		while ((rv = atom.get()) == null)
			continue;
		
		// If it is not valid then it will not have the right fields
		if (rv.valid)
			return rv;
		return null;
	}
	
	/**
	 * Obtains the remote URL to send a report to.
	 *
	 * @since 2018/02/22
	 */
	private final void __getRemote()
	{
		// Use a connection to an alternative URL using the same connection
		// type as the other.
		try
		{
			IOpipeConfiguration conf = this.config;
			
			// Use URL from the signer
			String desiredurl = conf.getSignerUrl();
			if (desiredurl == null)
				throw new RuntimeException("No signer URL specified.");
			
			// Indicate where the signer is uploading to
			Logger.debug("Requesting signer upload URL from {}.",
				desiredurl);
			
			// Setup connection to the signed service to determine which
			// URL we upload to
			RemoteConnectionFactory fact = conf.getRemoteConnectionFactory();
			RemoteConnection con = fact.connect(desiredurl,
				conf.getProjectToken());
			
			// Build request to remote end
			StringWriter out = new StringWriter();
			try (JsonGenerator gen = Json.createGenerator(out))
			{
				gen.writeStartObject();
				
				String awsarn = this.awsarn;
				if (awsarn != null)
					gen.write("arn", awsarn);
				
				String awsrequestid = this.awsrequestid;
				if (awsrequestid != null)
					gen.write("requestId", awsrequestid);
									
				gen.write("timestamp", this.timestamp);
				
				String extension = this.extension;
				if (extension != null)
					gen.write("extension", extension);
				
				// Finished
				gen.writeEnd();
				gen.flush();
			}
			
			// Ask which URL to send to
			RemoteResult resp = con.send(RequestType.POST,
				new RemoteRequest(RemoteBody.MIMETYPE_JSON, out.toString()));
			
			// Decode response
			JsonObject jo = (JsonObject)resp.bodyAsJsonStructure();
			JsonValue jv = jo.get("signedRequest");
			if (jv == null)
				throw new RuntimeException("Server did not respond with URL.");
			String url = ((JsonString)jv).getString();
			
			// Need access token to tell the dashboard where to find the
			// uploaded file
			JsonValue atv = jo.get("jwtAccess");
			if (atv == null)
				throw new RuntimeException("Server did not access token.");
			String jwtaccesstoken = ((JsonString)atv).getString();
			
			// Debug
			Logger.debug("Signer upload to `{}` with access token `{}`.",
				url, jwtaccesstoken);
			
			// Return it
			this._remote.set(new __SignerRemote__(true, url, jwtaccesstoken));
		}
		
		// Could not send to the remote end
		catch (Throwable e)
		{
			Logger.error(e, "Could not determine the signer upload URL.");
			
			// Mark invalid
			this._remote.set(new __SignerRemote__(false, null, null));
		}
	}
}

