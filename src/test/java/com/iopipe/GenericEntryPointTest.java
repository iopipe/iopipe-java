package com.iopipe;

import com.iopipe.elsewhere.Classes;
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
	private static Object[] _METHODS =
		{
			Classes.PACKAGE_PRIVATE, "instancePrivate",
			Classes.PACKAGE_PRIVATE, "instancePackagePrivate",
			Classes.PACKAGE_PRIVATE, "instanceProtected",
			Classes.PACKAGE_PRIVATE, "instancePublic",
			Classes.PACKAGE_PRIVATE, "staticPrivate",
			Classes.PACKAGE_PRIVATE, "staticPackagePrivate",
			Classes.PACKAGE_PRIVATE, "staticProtected",
			Classes.PACKAGE_PRIVATE, "staticPublic",
			Classes.PUBLIC, "instancePrivate",
			Classes.PUBLIC, "instancePackagePrivate",
			Classes.PUBLIC, "instanceProtected",
			Classes.PUBLIC, "instancePublic",
			Classes.PUBLIC, "staticPrivate",
			Classes.PUBLIC, "staticPackagePrivate",
			Classes.PUBLIC, "staticProtected",
			Classes.PUBLIC, "staticPublic",
		};
	
	/**
	 * Tests the generic entry point.
	 *
	 * @since 2018/08/13
	 */
	@Test
	public void test()
	{
		for (int i = 0, n = _METHODS.length; i < n; i += 2)
		{
			String form = (((Class<?>)_METHODS[i]).getName()) + _METHODS[i + 1];
			
			try
			{
				EntryPoint ep = new EntryPoint((Class)_METHODS[i],
					(String)_METHODS[i + 1]);
				
				if ("squirrel".equals(ep.handle().invokeExact("SQUIRREL")))
					assertTrue(true, form);
				else
					assertTrue(false, form);
			}
			catch (Throwable t)
			{
				t.printStackTrace();
				
				// Failed
				assertTrue(false, form);
			}
		}
	}
}

