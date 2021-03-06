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
import java.io.Writer;

/**
 * A JSON string.
 * 
 * @author <a href="mailto:mike@gavaghan.org">Mike Gavaghan</a>
 */
public class JSONString extends AbstractJSONValue
{
   /** The underlying value. **/
   private String mValue;

   /**
    * Read a string value.
    * 
    * @param path path to the value being read
    * @param pbr
    * @return the complete string read from the reader
    * @throws IOException
    * @throws JSONException
    */
   static String readString(String path, PushbackReader pbr) throws IOException, JSONException
   {
      StringBuilder builder = new StringBuilder();

      char c = JSONValueFactory.demand(pbr);
      if (c != '\"') throw new JSONException(path, "Leading quote expected at start of string.");

      for (;;)
      {
         c = JSONValueFactory.demand(pbr);

         // if closing quote
         if (c == '\"') break;

         // if escape
         if (c == '\\')
         {
            c = JSONValueFactory.demand(pbr);

            switch (c)
            {
               case '\"':
               case '/':
               case '\\':
                  builder.append(c);
                  break;
               case 'b':
                  builder.append('\b');
                  break;
               case 'f':
                  builder.append('\f');
                  break;
               case 'n':
                  builder.append('\n');
                  break;
               case 'r':
                  builder.append('\r');
                  break;
               case 't':
                  builder.append('\t');
                  break;
               case 'u':
                  StringBuilder hex = new StringBuilder();
                  hex.append(JSONValueFactory.demand(pbr));
                  hex.append(JSONValueFactory.demand(pbr));
                  hex.append(JSONValueFactory.demand(pbr));
                  hex.append(JSONValueFactory.demand(pbr));
                  try
                  {
                     int uchar = Integer.parseInt(hex.toString(), 16);
                     builder.append((char) uchar);
                  }
                  catch (NumberFormatException exc)
                  {
                     throw new JSONException(path, "Illegal unicode value: " + hex.toString());
                  }
                  break;
               default:
                  throw new JSONException(path, "Illegal escape value in string: " + c);
            }
         }
         else
         {
            builder.append(c);
         }
      }

      return builder.toString();
   }

   /**
    * Create a new JSONString.
    * 
    * @param value
    */
   public JSONString(String value)
   {
      if (value == null) throw new NullPointerException("Null value not allowed.  Use JSONNull instead.");
      mValue = value;
   }

   /**
    * Create a new JSONString.
    */
   public JSONString()
   {
      mValue = "";
   }

   /**
    * Set the underlying value.
    * 
    * @param value
    */
   public void setValue(String value)
   {
      if (value == null) throw new NullPointerException("Null value not allowed.  Use JSONNull instead.");
      mValue = value;
   }

   /**
    * Get the underlying value.
    * 
    * @return the underlying value of this instance
    */
   @Override
   public Object getValue()
   {
      return mValue;
   }

   /**
    * Get the underlying value cast as a String.
    * 
    * @since 1.2
    * 
    * @return the underlying value of this instance
    */
   public String getStringValue()
   {
      return mValue;
   }

   /**
    * Get the string length.
    * 
    * @return the string length
    */
   public int length()
   {
      return mValue.length();
   }

   /**
    * Create a prototype instance of the same type.
    * 
    * @return another object of the same implementation
    */
   @Override
   public JSONValue createPrototype()
   {
      return new JSONString();
   }

   /**
    * Copy the value of another JSONValue into our underlying value.
    * 
    * @param value
    */
   @Override
   public void copyValue(JSONValue value)
   {
      if (!getClass().isAssignableFrom(value.getClass())) throw new RuntimeException("Can't assign a " + value.getClass().getName() + " to a " + getClass().getName());

      mValue = (String) value.getValue();
   }

   /**
    * Read a JSON value (presumes the key has already been read) and set the
    * underlying value. There's generally no reason to call this method directly.
    * It is intended to be overridden by an extended type.
    * 
    * @param path path to the value being read
    * @param pbr  source reader
    * @throws IOException   on read failure
    * @throws JSONException on grammar error
    */
   @Override
   public void read(String path, PushbackReader pbr) throws IOException, JSONException
   {
      mValue = readString(path, pbr);
   }

   /**
    * Render this JSON value to a Writer.
    * 
    * @param indent indent padding
    * @param writer target writer
    * @param pretty 'true' for pretty-print, 'false' for flat
    * @throws IOException
    */
   @Override
   public void write(String indent, Writer writer, boolean pretty) throws IOException
   {
      StringBuilder builder = new StringBuilder();

      for (int i = 0; i < mValue.length(); i++)
      {
         char c = mValue.charAt(i);

         if (c == '\"') builder.append("\\\"");
         else if (c == '\\') builder.append("\\\\");
         else if ((c >= 32) && (c <= 126)) builder.append(c);
         else if (c == '\b') builder.append("\\b");
         else if (c == '\f') builder.append("\\f");
         else if (c == '\n') builder.append("\\n");
         else if (c == '\r') builder.append("\\r");
         else if (c == '\t') builder.append("\\t");
         else
         {
            String hex = "0000" + Integer.toString(c, 16);
            hex = hex.substring(hex.length() - 4);

            builder.append("\\u");
            builder.append(hex);
         }
      }

      writer.write('\"');
      writer.write(builder.toString());
      writer.write('\"');
   }
}
