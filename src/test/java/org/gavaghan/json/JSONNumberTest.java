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

import org.junit.Assert;
import org.junit.Test;

public class JSONNumberTest
{
	@Test
	public void testZero()  throws Exception
	{
		try (StringReader rdr = new StringReader("0 "); PushbackReader pbr = new PushbackReader(rdr, 1))
		{
			JSONNumber number = new JSONNumber();
			number.read("$", pbr);

			Assert.assertEquals("0", number.getValue().toString());
		}
	}

	@Test
	public void testWhole()  throws Exception
	{
		try (StringReader rdr = new StringReader("123 "); PushbackReader pbr = new PushbackReader(rdr,1))
		{
			JSONNumber number = new JSONNumber();
			number.read("$", pbr);
			
			Assert.assertEquals("123", number.getValue().toString());
		}
	}

	@Test
	public void testJustNegative()  throws Exception
	{
		try (StringReader rdr = new StringReader("- "); PushbackReader pbr = new PushbackReader(rdr,1))
		{
			JSONNumber number = new JSONNumber();
			number.read("$", pbr);
			
			Assert.fail("Exception expected");
		}
		catch(JSONException expected)
		{
		}
	}

	@Test
	public void testNegativeWhole()  throws Exception
	{
		try (StringReader rdr = new StringReader("-123 "); PushbackReader pbr = new PushbackReader(rdr,1))
		{
			JSONNumber number = new JSONNumber();
			number.read("$", pbr);
			
			Assert.assertEquals("-123", number.getValue().toString());
		}
	}

	@Test
	public void testNoDecimal()  throws Exception
	{
		try (StringReader rdr = new StringReader("123. "); PushbackReader pbr = new PushbackReader(rdr,1))
		{
			JSONNumber number = new JSONNumber();
			number.read("$", pbr);
			
			Assert.fail("Exception expected");
		}
		catch(JSONException expected)
		{
		}
	}

	@Test
	public void testDecimal()  throws Exception
	{
		try (StringReader rdr = new StringReader("-123.456 "); PushbackReader pbr = new PushbackReader(rdr,1))
		{
			JSONNumber number = new JSONNumber();
			number.read("$", pbr);
			
			Assert.assertEquals("-123.456", number.getValue().toString());
		}
	}

	@Test
	public void testExponent()  throws Exception
	{
		try (StringReader rdr = new StringReader("123.456E+2 "); PushbackReader pbr = new PushbackReader(rdr,1))
		{
			JSONNumber number = new JSONNumber();
			number.read("$", pbr);
			
			Assert.assertEquals("12345.6", number.getValue().toString());
		}
		
		try (StringReader rdr = new StringReader("123.456E-2 "); PushbackReader pbr = new PushbackReader(rdr,1))
		{
			JSONNumber number = new JSONNumber();
			number.read("$", pbr);
			
			Assert.assertEquals("1.23456", number.getValue().toString());
		}
		
		try (StringReader rdr = new StringReader("123.456E2 "); PushbackReader pbr = new PushbackReader(rdr,1))
		{
			JSONNumber number = new JSONNumber();
			number.read("$", pbr);
			
			Assert.assertEquals("12345.6", number.getValue().toString());
		}
		
		try (StringReader rdr = new StringReader("123e2 "); PushbackReader pbr = new PushbackReader(rdr,1))
		{
			JSONNumber number = new JSONNumber();
			number.read("$", pbr);
			
			Assert.assertEquals("1.23E+4", number.getValue().toString());
		}
	}

	@Test
	public void testExponentFail()  throws Exception
	{
		try (StringReader rdr = new StringReader("123E "); PushbackReader pbr = new PushbackReader(rdr,1))
		{
			JSONNumber number = new JSONNumber();
			number.read("$", pbr);
			
			Assert.fail("Exception expected");
		}
		catch(JSONException expected)
		{
		}

		try (StringReader rdr = new StringReader("123E+ "); PushbackReader pbr = new PushbackReader(rdr,1))
		{
			JSONNumber number = new JSONNumber();
			number.read("$", pbr);
			
			Assert.fail("Exception expected");
		}
		catch(JSONException expected)
		{
		}
	}
}
