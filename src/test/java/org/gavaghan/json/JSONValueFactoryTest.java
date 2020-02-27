package org.gavaghan.json;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author <a href="mailto:mike@gavaghan.org">Mike Gavaghan</a>
 */
public class JSONValueFactoryTest
{
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
}
