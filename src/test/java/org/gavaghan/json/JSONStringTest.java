/******************************************************************************
   Copyright 2018 Mike Gavaghan

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
******************************************************************************/
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
