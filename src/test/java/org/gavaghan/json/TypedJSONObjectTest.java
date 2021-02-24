package org.gavaghan.json;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author <a href="mailto:mike@gavaghan.org">Mike Gavaghan</a>
 */
public class TypedJSONObjectTest
{
   @Test
   public void testGetType()
   {
      TestTypedJSONObject json = new TestTypedJSONObject();

      Assert.assertEquals("org.gavaghan.json.TestTypedJSONObject", json.getType());
   }

   @Test
   public void testSerDeser() throws IOException, JSONException
   {
      TestTypedJSONObject json = new TestTypedJSONObject();
      JSONValueFactory jsonFactory = new TypedJSONValueFactory();

      // populated the object
      json.setString("Hello, World!");
      json.setBigDecimal(new BigDecimal("123"));

      // serialize the object
      String asString = json.toPrettyString();

      // try to deserialize
      TestTypedJSONObject json2;

      try (StringReader rdr = new StringReader(asString))
      {
         json2 = (TestTypedJSONObject) jsonFactory.read(rdr);
      }
      
      Assert.assertEquals(json.getString(), json2.getString());
   }
}
