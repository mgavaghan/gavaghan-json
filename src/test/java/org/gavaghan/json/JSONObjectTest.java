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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class JSONObjectTest
{
	@Test
	public void testRead() throws Exception
	{
		try (InputStream instr = JSONObjectTest.class.getResourceAsStream("JSONObjectTest.json"); InputStreamReader rdr = new InputStreamReader(instr))
		{
			JSONObject json = (JSONObject) JSONValueFactory.DEFAULT.read(rdr);

			Assert.assertEquals(new BigDecimal("123"), json.get("number").getValue());
			Assert.assertEquals("Hello, World!", json.get("string").getValue());
			Assert.assertEquals("", json.get("emptystring").getValue());
			Assert.assertEquals(Boolean.TRUE, json.get("true").getValue());
			Assert.assertEquals(Boolean.FALSE, json.get("false").getValue());
			Assert.assertNull(json.get("null").getValue());

			@SuppressWarnings("unchecked")
			List<JSONValue> empty = (List<JSONValue>) json.get("emptyarray").getValue();
			Assert.assertEquals(0, empty.size());

			@SuppressWarnings("unchecked")
			List<JSONValue> array = (List<JSONValue>) json.get("array").getValue();
			Assert.assertEquals(3, array.size());
			
			JSONObject obj = (JSONObject) json.get("object").getValue();
			Assert.assertEquals("red", obj.get("color").getValue());
		}
	}
}
