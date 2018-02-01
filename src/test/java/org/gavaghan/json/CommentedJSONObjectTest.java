package org.gavaghan.json;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class CommentedJSONObjectTest
{
	@Test
	public void testRead() throws Exception
	{
		try (InputStream instr = CommentedJSONObjectTest.class.getResourceAsStream("CommentedJSONObjectTest.json"); InputStreamReader rdr = new InputStreamReader(instr))
		{
			JSONObject json = (JSONObject) CommentedJSONValueFactory.COMMENTED_DEFAULT.read(rdr);

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
