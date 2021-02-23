package org.gavaghan.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.io.Writer;
import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author <a href="mailto:mike@gavaghan.org">Mike Gavaghan</a>
 */
public class JSONValueFactoryTest
{
   static private JSONObject getTestObject() throws IOException, JSONException
   {
      try (InputStream instr = JSONObjectTest.class.getResourceAsStream("JSONObjectTest.json"); InputStreamReader rdr = new InputStreamReader(instr))
      {
         return (JSONObject) JSONValueFactory.DEFAULT.read(rdr);
      }
   }

   @Test
   public void testGetOrSet()
   {
      TestTypedJSONObject json = new TestTypedJSONObject();

      // test return default value
      Assert.assertEquals(BigDecimal.ZERO, json.getBigDecimal());

      // test setting a value
      json.setString("Hello, World!");
      Assert.assertEquals("Hello, World!", json.getString());
   }

   @Test
   public void testKnownGet() throws IOException, JSONException
   {
      JSONObject doc = getTestObject();

      BigDecimal number = (BigDecimal) JSONValueFactory.getOrSet(doc, "number", JSONNumber.class);
      Assert.assertEquals(new BigDecimal(123), number);

      String str = (String) JSONValueFactory.getOrSet(doc, "string", JSONString.class);
      Assert.assertEquals("Hello, World!", str);

      try
      {
         JSONValueFactory.getOrSet(doc, "string", JSONNumber.class);
         Assert.fail("Exception expected");
      }
      catch (RuntimeException exc)
      {
         // expected
      }
   }

   @Test
   public void testUnknownGet() throws IOException, JSONException
   {
      JSONObject doc = getTestObject();

      BigDecimal number = (BigDecimal) JSONValueFactory.getOrSet(doc, "number2", JSONNumber.class);
      Assert.assertEquals(BigDecimal.ZERO, number);

      try
      {
         JSONValueFactory.getOrSet(doc, "number2", JSONString.class);
         Assert.fail("Exception expected");
      }
      catch (RuntimeException exc)
      {
         // expected
      }

      try
      {
         JSONValueFactory.getOrSet(doc, "number3", NoGoodConstructor.class);
      }
      catch (RuntimeException exc)
      {
         // expected
      }
   }
}

class NoGoodConstructor implements JSONValue
{
   public NoGoodConstructor(int a)
   {
   }

   @Override
   public Object getValue()
   {
      return null;
   }

   @Override
   public void read(String path, PushbackReader pbr) throws IOException, JSONException
   {
   }

   @Override
   public JSONValue createPrototype()
   {
      return null;
   }

   @Override
   public void copyValue(JSONValue value)
   {
   }

   @Override
   public JSONValue deepCopy()
   {
      return null;
   }

   @Override
   public void write(String indent, Writer writer, boolean pretty) throws IOException
   {
   }

   @Override
   public String toPrettyString()
   {
      return null;
   }

   @Override
   public String toFlatString()
   {
      return null;
   }
}
