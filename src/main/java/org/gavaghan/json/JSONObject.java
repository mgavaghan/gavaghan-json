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
import java.util.LinkedHashMap;

/**
 * A JSON object as defined by <a href="http://www.json.org/">http://www.json.org/</a>
 * 
 * @author <a href="mailto:mike@gavaghan.org">Mike Gavaghan</a>
 */
public class JSONObject extends LinkedHashMap<String, JSONValue> implements JSONValue
{
	/**
	 * System dependent end-of-line delimiter take from system property
	 * "line.separator"
	 */
	static public final String EOL = System.getProperty("line.separator");

	/** JSONValueFactory for reading from a Reader. */
	private JSONValueFactory mFactory;

	/**
	 * Create a new JSONObject.
	 * 
	 * @param factory
	 *           the factory implementation used to read values in the object.
	 */
	protected JSONObject(JSONValueFactory factory)
	{
		mFactory = factory;
	}

	/**
	 * Create a new JSONObject.
	 */
	public JSONObject()
	{
	}

	/**
	 * Get the underlying value.
	 * 
	 * @return
	 */
	@Override
	public Object getValue()
	{
		return this;
	}

   /**
    * Get the underlying value as a JSONObject.
    * 
    * @since 1.2
    * 
    * @return
    */
   public JSONObject getObjectValue()
   {
      return this;
   }

	/**
	 * Create a prototype instance of the same type.
	 * 
	 * @return
	 */
	@Override
	public JSONValue createPrototype()
	{
		return new JSONObject();
	}

	/**
	 * Copy the value of another JSONValue into our underlying value.
	 * 
	 * @param value
	 */
	@Override
	public void copyValue(JSONValue value)
	{
		if (!JSONObject.class.isAssignableFrom(value.getClass())) throw new RuntimeException("Can't assign a " + value.getClass().getName() + " to a " + getClass().getName());

      JSONObject source = (JSONObject)value.getValue();

      for (String key : source.keySet())
      {
        put(key, source.get(key));
      }
	}

	/**
	 * Create a deep copy of this instance.
	 * 
	 * @return
	 */
	@Override
	public JSONValue deepCopy()
	{
      JSONValue copy = createPrototype();
      copy.copyValue(this);
      return copy;

	}

	/**
	 * Read a JSON value (presumes the key has already been read) and set the
	 * underlying value. There's generally no reason to call this method
	 * directly. It is intended to be overridden by an extended type.
	 * 
	 * @param path
	 *           path to the value being read
	 * @param pbr
	 *           source reader
	 * @throws IOException
	 *            on read failure
	 * @throws JSONException
	 *            on grammar error
	 */
	@Override
	public void read(String path, PushbackReader pbr) throws IOException, JSONException
	{
		// assert we have an opening brace
		char c = JSONValueFactory.demand(pbr);
		if (c != '{') throw new JSONException(path, "Failed to find '{' at start of JSON object.");

		for (;;)
		{
			String key;

			// next is either a key or a closing brace
			mFactory.skipWhitespace(pbr);
			c = JSONValueFactory.demand(pbr);

			// is it a string?
			if (c == '\"')
			{
				pbr.unread(c);
				key = JSONString.readString(path, pbr);
			}
			// is it a closing brace?
			else if (c == '}')
			{
				break;
			}
			// else, it's poorly formed
			else
			{
				throw new JSONException(path, "JSON object is not grammatically correct.  Unexpected: " + c);
			}

			// next ought to be a colon
			mFactory.skipWhitespace(pbr);
			c = JSONValueFactory.demand(pbr);
			if (c != ':') throw new JSONException(path + "." + key, "Expected ':' after key value");
			mFactory.skipWhitespace(pbr);

			// next, read a JSONValue
			JSONValue value = mFactory.read(path + "." + key, pbr);

			// add it to the map
			put(key, value);

			// next must be comma or close
			mFactory.skipWhitespace(pbr);
			c = JSONValueFactory.demand(pbr);

			if (c == ',') continue;
			if (c == '}') break;

			throw new JSONException(path, "JSON object is not grammatically correct.  Unexpected: " + c);
		}
	}

	/**
	 * Render this JSON value to a Writer.
	 * 
	 * @param indent
	 *           indent padding
	 * @param writer
	 *           target writer
	 * @param pretty
	 *           'true' for pretty-print, 'false' for flat
	 * @throws IOException
	 */
	@Override
	public void write(String indent, Writer writer, boolean pretty) throws IOException
	{
		String newIndent = indent + "   ";
		
		// if JSONObject has been subtyped, included the class name in the JSON
		if (this instanceof TypedJSONObject)
		{
		   ((TypedJSONObject) this).getType();
		}

		if (size() == 0)
		{
			writer.write("{}");
		}
		else
		{
			writer.write('{');

			int count = 1;

			if (pretty) writer.write(EOL);

			for (String key : keySet())
			{
				if (pretty) writer.write(newIndent);
				writer.write('\"');
				writer.write(key);
				writer.write("\":");
				if (pretty) writer.write(" ");

				get(key).write(newIndent, writer, pretty);

				if (count != size()) writer.write(',');

				if (pretty) writer.write(EOL);
				count++;
			}

			writer.write(indent);
			writer.write('}');
		}
	}

	/**
	 * Render this object as a string.
	 */
	@Override
	public String toString()
	{
		return toPrettyString();
	}

	/**
	 * Render this object as a pretty-printed string.
	 */
	@Override
	public String toPrettyString()
	{
		return AbstractJSONValue.toString(this, true);
	}

	/**
	 * Render this object as a flattened string.
	 */
	@Override
	public String toFlatString()
	{
		return AbstractJSONValue.toString(this, false);
	}
}
