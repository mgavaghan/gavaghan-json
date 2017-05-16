package org.gavaghan.json;

import java.io.PushbackReader;
import java.io.StringReader;
import java.io.StringWriter;

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

	@Test
	public void testLinefeedOutput() throws Exception
	{
		try (StringReader rdr = new StringReader("\"\\n\" "); PushbackReader pbr = new PushbackReader(rdr, 1); StringWriter wrt = new StringWriter();)
		{
			JSONString str = new JSONString();
			str.read("$", pbr);

			str.write("", wrt, true);

			String output = wrt.toString();

			Assert.assertEquals("\"\\n\"", output);
		}
	}

	@Test
	public void testQuoteOutput() throws Exception
	{
		try (StringReader rdr = new StringReader("\"\\\"\" "); PushbackReader pbr = new PushbackReader(rdr, 1); StringWriter wrt = new StringWriter();)
		{
			JSONString str = new JSONString();
			str.read("$", pbr);

			Assert.assertEquals("\"", str.getValue().toString());

			str.write("", wrt, true);

			String output = wrt.toString();

			Assert.assertEquals("\"\\\"\"", output);
		}
	}

	@Test
	public void testStringOutput() throws Exception
	{
		try (StringReader rdr = new StringReader("\"ABC\\b\\n\\\"DE\\u123DF\\\\\" "); PushbackReader pbr = new PushbackReader(rdr, 1); StringWriter wrt = new StringWriter();)
		{
			JSONString str = new JSONString();
			str.read("$", pbr);

			str.write("", wrt, true);

			String output = wrt.toString();

			Assert.assertEquals("\"ABC\\b\\n\\\"DE\\u123dF\\\\\"", output);
		}
	}
}
