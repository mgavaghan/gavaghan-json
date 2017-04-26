package org.gavaghan.json;

import java.io.PushbackReader;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;

public class JSONStringTest
{
	@Test
	public void testString() throws Exception
	{
		try (StringReader rdr = new StringReader("\"ABC\\n\\\"DE\\u123DF\" "); PushbackReader pbr = new PushbackReader(rdr, 1))
		{
			JSONString str = new JSONString();
			str.read("$", pbr);

			Assert.assertEquals("ABC\n\"DE\u123DF", str.getValue().toString());
		}
	}
}
