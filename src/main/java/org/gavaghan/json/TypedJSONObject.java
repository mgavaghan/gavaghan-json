package org.gavaghan.json;

import java.text.MessageFormat;

/**
 * Superclass of classes that embed their concrete type class in the json
 * output. This enables reconstituting the proper type when reading from
 * serialized form.
 * 
 * @author <a href="mailto:mike@gavaghan.org">Mike Gavaghan</a>
 */
public class TypedJSONObject extends JSONObject
{
   /** The key to the type value of a <code>TypedJSONObject</code>. */
   static public final String TYPE_KEY = "jsonObjectSubclass";

   /**
    * Only allow instantiation by subtypes.
    */
   protected TypedJSONObject()
   {
   }

   /**
    * Get the type name of this instance.
    * 
    * @return the type name of this instance
    */
   public String getType()
   {
      String retval;

      JSONValue type = get(TYPE_KEY);

      // if 'type' isn't set, create it
      if (type == null)
      {
         retval = getClass().getName();

         put(TYPE_KEY, new JSONString(retval));
      }

      // otherwise, use previously set value
      else
      {
         if (!(type instanceof JSONString))
         {
            throw new RuntimeException(MessageFormat.format("Type value of ''{0}'' is not an instance of a 'JSONString'", type.getClass()));
         }

         retval = ((JSONString) type).getStringValue();
      }

      return retval;
   }
}
