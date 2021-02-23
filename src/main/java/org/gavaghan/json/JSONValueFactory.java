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

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

/**
 * <p>
 * Factory for determining the proper <code>JSONValue</code> implementation
 * based on the incoming stream.
 * </p>
 * 
 * <p>
 * Overriding this class allows for custom JSON data types as well as the
 * redefinition of whitespace.
 * </p>
 * 
 * @see CommentedJSONValueFactory
 * 
 * @author <a href="mailto:mike@gavaghan.org">Mike Gavaghan</a>
 */
public class JSONValueFactory
{
   /** The default implementation. */
   static public final JSONValueFactory DEFAULT = new JSONValueFactory();

   /** GC safe empty parameters. */
   static protected final Class<?> NO_PARAMS[] = new Class<?>[0];

   /** GC safe empty argument. */
   static protected final Object NO_ARGS[] = new Object[0];

   /**
    * Skip to first non-whitespace character. Derived implementations may choose to
    * override this in order to redefine whitespace.
    * 
    * @see org.gavaghan.json.CommentedJSONValueFactory
    * 
    * @param pbr a pushback reader
    * @throws IOException
    * @throws JSONException
    */
   public void skipWhitespace(PushbackReader pbr) throws IOException, JSONException
   {
      for (;;)
      {
         int c = pbr.read();

         if (c < 0) break; // bail on EOF

         // if non-whitespace found, push it back and exit
         if (!Character.isWhitespace(c))
         {
            pbr.unread(c);
            break;
         }
      }
   }

   /**
    * Demand a character from the reader and throw a <code>JSONException</code> if
    * EOF.
    * 
    * @param rdr a reader
    * @return the next available character
    * @throws IOException
    * @throws JSONException
    */
   static public char demand(Reader rdr) throws IOException, JSONException
   {
      int c = rdr.read();
      if (c < 0) throw new JSONException("$", "Out of data while reading JSON object.");
      return (char) c;
   }

   /**
    * Callback when a string is encountered.
    * 
    * @param path current path into the JSON object
    * @param pbr  input reader
    * @return <code>JSONValue</code> implementation for a string
    * @throws IOException
    * @throws JSONException
    */
   protected JSONValue onString(String path, PushbackReader pbr) throws IOException, JSONException
   {
      return new JSONString();
   }

   /**
    * Callback when a number is encountered.
    * 
    * @param path current path into the JSON object
    * @param pbr  input reader
    * @return <code>JSONValue</code> implementation for a number
    * @throws IOException
    * @throws JSONException
    */
   protected JSONValue onNumber(String path, PushbackReader pbr) throws IOException, JSONException
   {
      return new JSONNumber();
   }

   /**
    * Callback when an array is encountered.
    * 
    * @param path current path into the JSON object
    * @param pbr  input reader
    * @return <code>JSONValue</code> implementation for an array
    * @throws IOException
    * @throws JSONException
    */
   protected JSONValue onArray(String path, PushbackReader pbr) throws IOException, JSONException
   {
      return new JSONArray(this);
   }

   /**
    * Callback when an object is encountered.
    * 
    * @param path current path into the JSON object
    * @param pbr  input reader
    * @return <code>JSONValue</code> implementation for an object
    * @throws IOException
    * @throws JSONException
    */
   protected JSONValue onObject(String path, PushbackReader pbr) throws IOException, JSONException
   {
      return new JSONObject(this);
   }

   /**
    * Callback when a boolean is encountered.
    * 
    * @param path current path into the JSON object
    * @param pbr  input reader
    * @return <code>JSONValue</code> implementation for a boolean
    * @throws IOException
    * @throws JSONException
    */
   protected JSONValue onBoolean(String path, PushbackReader pbr) throws IOException, JSONException
   {
      return new JSONBoolean();
   }

   /**
    * Callback when a null is encountered.
    * 
    * @param path current path into the JSON object
    * @param pbr  input reader
    * @return <code>JSONValue</code> implementation for a null
    * @throws IOException
    * @throws JSONException
    */
   protected JSONValue onNull(String path, PushbackReader pbr) throws IOException, JSONException
   {
      return JSONNull.INSTANCE;
   }

   /**
    * Callback for the start of an unknown type. The base implementation throws a
    * <code>JSONException</code>
    * 
    * @param path current path into the JSON object
    * @param pbr  input reader
    * @param c    unhandled character
    * @return <code>JSONValue</code> implementation for a non-standard data type
    * @throws IOException
    * @throws JSONException
    */
   protected JSONValue onUnknown(String path, PushbackReader pbr, char c) throws IOException, JSONException
   {
      throw new JSONException(path, "Illegal start of JSON value: " + c);
   }

   /**
    * <p>
    * Give subtypes a chance to recast the loaded value as a <code>JSONValue</code>
    * subtype. Default implementation returns 'null' because no recast is needed.
    * </p>
    * <p>
    * Subtypes only need to return a default instance. The <code>read()</code>
    * method handles copying of data.
    * </p>
    * 
    * @param value the value to potentially recast
    * @return the recast value or 'null' if no recast was required.
    * @throws IOException
    * @throws JSONException
    */
   protected JSONValue recast(JSONValue value) throws IOException, JSONException
   {
      return null;
   }

   /**
    * Create a new <code>JSONValueFactory</code>.
    */
   public JSONValueFactory()
   {
   }

   /**
    * Get a named value from a <code>JSONObject</code>. If the value doesn't exist,
    * make a default instance and add it.
    * 
    * @param jsonObj  the <code>JSONObject</code> to get a value from
    * @param name     the name of the value
    * @param jsonType if the value doesn't exist, this is the type to add
    * @return the named value
    */
   static public Object getOrSet(JSONObject jsonObj, String name, Class<? extends JSONValue> jsonType)
   {
      Object retval;

      // look to see if the value already exists
      JSONValue jsonValue = jsonObj.get(name);

      // if it exists, it's easy - just return it after a type check
      if (jsonValue != null)
      {
         // make sure we got the right object type
         if (!jsonType.isAssignableFrom(jsonValue.getClass()))
         {
            throw new RuntimeException(MessageFormat.format("Value named ''{0}'' is of type ''{1}'' which is not assignable from ''{2}''", name, jsonValue.getClass().getName(), jsonType.getName()));
         }

         retval = jsonValue.getValue();
      }

      // otherwise, create a default
      else
      {
         try
         {
            Constructor ctx = jsonType.getConstructor(NO_PARAMS);
            JSONValue newJSON = (JSONValue) ctx.newInstance(NO_ARGS);
            jsonObj.put(name, newJSON);
            retval = newJSON.getValue();
         }
         catch (NoSuchMethodException exc)
         {
            throw new RuntimeException("'" + jsonType.getName() + "' does not have a public default constructor");
         }
         catch (SecurityException exc)
         {
            throw new RuntimeException("Security exception thrown for default constructor found for: " + jsonType.getName());
         }
         catch (InstantiationException | IllegalAccessException | IllegalArgumentException exc)
         {
            throw new RuntimeException("Constructor for '" + jsonType.getName() + "' threw an exception", exc);
         }
         catch (InvocationTargetException exc)
         {
            throw new RuntimeException("Constructor for '" + jsonType.getName() + "' threw an exception", exc.getCause());
         }
      }

      return retval;
   }

   /**
    * Get the minimum size of the pushback buffer.
    * 
    * @return the size of the required pushback buffer
    */
   public int getPushbackBufferSize()
   {
      return 1;
   }

   /**
    * Read the <code>JSONValue</code> that comes after the whitespace (if any).
    * 
    * @param reader
    * @return the next <code>JSONValue</code>
    * @throws IOException
    * @throws JSONException
    */
   final public JSONValue read(Reader reader) throws IOException, JSONException
   {
      PushbackReader pbr = new PushbackReader(reader, getPushbackBufferSize());

      // look for start of value
      skipWhitespace(pbr);
      int c = pbr.read();

      // bail out early if EOF
      if (c < 0) return null;

      pbr.unread(c);

      return read("$", pbr);
   }

   /**
    * Read a <code>JSONValue</code>.
    * 
    * @param path JSON path to the value we're reading
    * @param pbr  a pushback reader
    * @return the next <code>JSONValue</code>
    * @throws IOException
    * @throws JSONException
    */
   final public JSONValue read(String path, PushbackReader pbr) throws IOException, JSONException
   {
      JSONValue value;
      char c = demand(pbr);

      // is it a string?
      if (c == '\"')
      {
         value = onString(path, pbr);
      }
      // is it a number?
      else if (Character.isDigit(c) || (c == '-'))
      {
         value = onNumber(path, pbr);
      }
      // is it an array?
      else if (c == '[')
      {
         value = onArray(path, pbr);
      }
      // is it an object?
      else if (c == '{')
      {
         value = onObject(path, pbr);
      }
      // is it a boolean?
      else if ((c == 't') || (c == 'f'))
      {
         value = onBoolean(path, pbr);
      }
      // is it a null?
      else if (c == 'n')
      {
         value = onNull(path, pbr);
      }
      // else, value type
      else
      {
         value = onUnknown(path, pbr, c);
      }

      // unread trigger character
      pbr.unread(c);

      // implementation specific read
      value.read(path, pbr);

      // give subtype a chance to select a different implementation
      JSONValue recast = recast(value);

      // if value was recast, copy over original data
      if (recast != null)
      {
         recast.copyValue(value);
         value = recast;
      }

      return value;
   }
}
