package org.gavaghan.json;

import java.math.BigDecimal;

/**
 * 
 * @author <a href="mailto:mike@gavaghan.org">Mike Gavaghan</a>
 */
public class TestTypedJSONObject extends TypedJSONObject
{
   public TestTypedJSONObject()
   {
   }
   
   public void setString(String myString)
   {
      put("string", new JSONString(myString));
   }
   
   public String getString()
   {
      return ((String) JSONValueFactory.getOrSet(this, "string", JSONString.class));
   }
   
   public void setBigDecimal(BigDecimal myBigDecimal)
   {
      put("bigdecimal", new JSONNumber(myBigDecimal));
   }
   
   public BigDecimal getBigDecimal()
   {
      return ((BigDecimal) JSONValueFactory.getOrSet(this, "bigdecimal", JSONNumber.class));
   }
}
