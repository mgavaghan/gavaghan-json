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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A JSON array represented as a <code>List&lt;JSONValue&gt;</code>.
 * 
 * @author <a href="mailto:mike@gavaghan.org">Mike Gavaghan</a>
 */
public class JSONArray extends AbstractJSONValue implements Iterable<JSONValue>
{
   /** The underlying value. **/
   private List<JSONValue> mValue;

   /** JSONValueFactory for reading from a Reader. */
   private JSONValueFactory mFactory;

   /**
    * Create a new <code>JSONArray</code>.
    * 
    * @param value List object to wrap.
    */
   public JSONArray(List<JSONValue> value)
   {
      if (value == null) throw new NullPointerException("Null value not allowed.  Use JSONNull instead.");
      mValue = value;
   }

   /**
    * Create a new <code>JSONArray</code>.
    * 
    * @param factory the <code>JSONValueFactory</code> implementation used to read
    *                JSON values.
    */
   protected JSONArray(JSONValueFactory factory)
   {
      mValue = new ArrayList<JSONValue>();
      mFactory = factory;
   }

   /**
    * Create a new <code>JSONArray</code> wrapping an empty list.
    */
   public JSONArray()
   {
      mValue = new ArrayList<JSONValue>();
   }

   /**
    * Set the underlying value.
    * 
    * @param value new List to wrap
    */
   public void setValue(List<JSONValue> value)
   {
      if (value == null) throw new NullPointerException("Null value not allowed.  Use JSONNull instead.");
      mValue = value;
   }

   /**
    * Get the underlying value.
    * 
    * @return the <code>List&lt;JSONValue&gt;</code> this object wraps
    */
   @Override
   public Object getValue()
   {
      return mValue;
   }

   /**
    * Get the underlying value as a <code>List&lt;JSONValue&gt;</code>.
    * 
    * @since 1.2
    * 
    * @return typesafe version of <code>getValue()</code>
    */
   public List<JSONValue> getListValue()
   {
      return mValue;
   }

   /**
    * Get the <code>JSONArray</code> at an index.
    * 
    * @since 1.2
    * 
    * @param index position in the underlying list
    * @return the <code>JSONValue</code> at the specified index
    * @throws IndexOutOfBoundsException if the index is out of range(index < 0 ||
    *                                   index >= size())
    */
   public JSONValue get(int index)
   {
      return mValue.get(index);
   }

   /**
    * Set a <code>JSONValue</code> at an index.
    * 
    * @since 1.2
    * 
    * @param index position in the underlying list
    * @param value the <code>JSONValue</code> to set
    * 
    * @throws IndexOutOfBoundsException if the index is out of range(index < 0 ||
    *                                   index >= size())
    */
   public void set(int index, JSONValue value)
   {
      mValue.set(index, value);
   }

   /**
    * Remove a <code>JSONValue</code> at an index.
    * 
    * @since 1.2
    * 
    * @param index index position in the underlying list
    * @return the value removed
    */
   public JSONValue remove(int index)
   {
      return mValue.remove(index);
   }

   /**
    * Get the array size.
    * 
    * @since 1.2
    * 
    * @return array size.
    */
   public int size()
   {
      return mValue.size();
   }

   /**
    * Create a prototype instance of the same type.
    * 
    * @return a default new instance of the same concrete type
    */
   @Override
   public JSONValue createPrototype()
   {
      return new JSONArray();
   }

   /**
    * Copy the value of another <code>JSONValue</code> into our underlying value.
    * 
    * @param value the value to copy
    */
   @Override
   public void copyValue(JSONValue value)
   {
      if (!getClass().isAssignableFrom(value.getClass())) throw new RuntimeException("Can't assign a " + value.getClass().getName() + " to a " + getClass().getName());

      @SuppressWarnings("unchecked")
      List<JSONValue> source = (List<JSONValue>) value.getValue();

      mValue = new ArrayList<JSONValue>();

      for (JSONValue json : source)
      {
         mValue.add(json.deepCopy());
      }
   }

   /**
    * Read a <code>JSONValue</code> (presumes the key has already been read) and
    * set the underlying value. There's generally no reason to call this method
    * directly. It is intended to be overridden by an extended type.
    * 
    * @param path path to the value being read
    * @param pbr  source reader
    * @throws IOException   on read failure
    * @throws JSONException on grammar error
    */
   @Override
   public void read(String path, PushbackReader pbr) throws IOException, JSONException
   {
      char c = JSONValueFactory.demand(pbr);
      if (c != '[') throw new JSONException(path, "Content does not appear to be an array.");

      // empty array is an easy out
      mFactory.skipWhitespace(pbr);
      c = JSONValueFactory.demand(pbr);
      if (c == ']') return;
      pbr.unread(c);

      // loop through values
      try
      {
         for (;;)
         {
            JSONValue value = mFactory.read(path, pbr);
            mValue.add(value);

            // get next non-whitespace
            mFactory.skipWhitespace(pbr);
            c = JSONValueFactory.demand(pbr);

            // is end?
            if (c == ']') return;

            // is more
            if (c == ',')
            {
               mFactory.skipWhitespace(pbr);
               continue;
            }

            throw new JSONException(path, "Incorrectly formatted array: " + c);
         }
      }
      finally
      {
         mFactory = null;
      }
   }

   /**
    * Render this <code>JSONValue</code> to a Writer.
    * 
    * @param indent indent padding
    * @param writer target writer
    * @param pretty 'true' for pretty-print, 'false' for flat
    * @throws IOException on any failure of the <code>Writer</code>
    */
   @Override
   public void write(String indent, Writer writer, boolean pretty) throws IOException
   {
      String newIndent = indent + "   ";

      if (mValue.size() == 0)
      {
         writer.write("[]");
      }
      else
      {
         int count = 1;

         writer.write("[");
         writer.write(JSONObject.EOL);

         for (JSONValue value : mValue)
         {
            writer.write(newIndent);

            value.write(newIndent, writer, pretty);

            if (count != mValue.size()) writer.write(',');

            writer.write(JSONObject.EOL);
            count++;
         }

         writer.write(indent);
         writer.write("]");
      }
   }

   /**
    * Get the iterator for this <code>JSONArray</code>.
    * 
    * @return iterator over <code>JSONValue</code> instances
    */
   @Override
   public Iterator<JSONValue> iterator()
   {
      return mValue.iterator();
   }
}
