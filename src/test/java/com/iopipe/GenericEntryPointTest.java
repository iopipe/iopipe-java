package com.iopipe;

import com.iopipe.generic.EntryPoint;
import java.lang.invoke.MethodHandle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This tests to ensure that the entry point lookup works.
 *
 * @since 2018/08/13
 */
public class GenericEntryPointTest
{
	/** The methods to be tested for entry points. */
	private static String[] _METHODS =
		{
			"com.iopipe.elsewhere.PackagePrivateClass::instancePrivate",
			"com.iopipe.elsewhere.PackagePrivateClass::instancePackagePrivate",
			"com.iopipe.elsewhere.PackagePrivateClass::instanceProtected",
			"com.iopipe.elsewhere.PackagePrivateClass::instancePublic",
			"com.iopipe.elsewhere.PackagePrivateClass::staticPrivate",
			"com.iopipe.elsewhere.PackagePrivateClass::staticPackagePrivate",
			"com.iopipe.elsewhere.PackagePrivateClass::staticProtected",
			"com.iopipe.elsewhere.PackagePrivateClass::staticPublic",
			"com.iopipe.elsewhere.PublicClass::instancePrivate",
			"com.iopipe.elsewhere.PublicClass::instancePackagePrivate",
			"com.iopipe.elsewhere.PublicClass::instanceProtected",
			"com.iopipe.elsewhere.PublicClass::instancePublic",
			"com.iopipe.elsewhere.PublicClass::staticPrivate",
			"com.iopipe.elsewhere.PublicClass::staticPackagePrivate",
			"com.iopipe.elsewhere.PublicClass::staticProtected",
			"com.iopipe.elsewhere.PublicClass::staticPublic",
		};
	
	/**
	 * Tests the generic entry point.
	 *
	 * @since 2018/08/13
	 */
	@Test
	public void test()
	{
		for (String method : _METHODS)
			try
			{
				EntryPoint ep = new EntryPoint(method);
				
				if ("squirrel".equals(ep.handle().invokeExact("SQUIRREL")))
					assertTrue(true, method);
				else
					assertTrue(false, method);
			}
			catch (Throwable t)
			{
				t.printStackTrace();
				
				// Failed
				assertTrue(false, method);
			}
	}
}

