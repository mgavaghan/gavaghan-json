package org.gavaghan.json;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

/**
 * A <code>JSONValueFactory</code> implementation that can read and write
 * <code>JSONObject</code> subtypes. All types derived from
 * <code>TypedJSONObject</code> include a type value of the concrete derived
 * class name when converting to a String. When reading a String, the type value
 * is used to instantiate the proper subtype.
 * 
 * @author <a href="mailto:mike@gavaghan.org">Mike Gavaghan</a>
 */
public class TypedJSONValueFactory extends JSONValueFactory
{
   /**
    * Look for the 'type' value in a populated <code>JSONObject</code> and create a
    * default instance of it. If 'value' is not a <code>JSONObject</code> or if
    * 'type' value is not found, defer to the super class.
    * 
    * @param path  JSON path to the value we're reading
    * @param value the value to possibly recast
    * @return the recast value or 'null' if no recast was required.
    * @throws JSONException
    */
   @Override
   protected JSONValue recast(String path, JSONValue value) throws JSONException
   {
      TypedJSONObject retval;

      // if it's not a JSONObject, we have nothing to do
      if (!(value instanceof JSONObject)) return super.recast(path, value);

      // look for a type value
      JSONObject valueObj = (JSONObject) value;
      JSONValue typeValue = valueObj.get(TypedJSONObject.TYPE_KEY);

      // if no type found, defer to superclass
      if (typeValue == null) return super.recast(path, value);

      // ensure typeValue is a string
      if (!(typeValue instanceof JSONString))
      {
         throw new JSONException(path, MessageFormat.format("'type' value is a ''{0}'' but a JSONString was expected", typeValue.getClass().getName()));
      }

      // load the new class
      String typeName = ((JSONString) typeValue).getStringValue();
      Class<?> klass;

      try
      {
         klass = Class.forName(typeName);
      }
      catch (ClassNotFoundException exc)
      {
         throw new JSONException(path, MessageFormat.format("Read a JSON object with type attribute ''{0}'' but that class could not be found", typeName), exc);
      }

      // ensure the class is an appropriate subtype
      if (!TypedJSONObject.class.isAssignableFrom(klass))
      {
         throw new JSONException(path, MessageFormat.format("Read an object of type ''{0}'' but that class is not assignable to 'TypedJSONObject'", typeName));
      }

      // instantiate a default instance
      try
      {
         Constructor ctx = klass.getConstructor(NO_PARAMS);
         JSONValue newJSON = (JSONValue) ctx.newInstance(NO_ARGS);
         retval = (TypedJSONObject) newJSON;
      }
      catch (NoSuchMethodException | SecurityException exc)
      {
         throw new JSONException(path, "No default constructor found for: " + klass.getName(), exc);
      }
      catch (InstantiationException | IllegalAccessException | IllegalArgumentException exc)
      {
         throw new JSONException(path, "Constructor for '" + klass.getName() + "' threw an exception", exc);
      }
      catch (InvocationTargetException exc)
      {
         throw new JSONException(path, "Constructor for '" + klass.getName() + "' threw an exception", exc.getCause());
      }

      return retval;
   }
}
