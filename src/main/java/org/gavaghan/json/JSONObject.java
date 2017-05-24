package org.gavaghan.json;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Writer;
import java.util.LinkedHashMap;

/**
 * A JSON object as defined by http://www.json.org/
 * 
 * @author <a href="mailto:mike@gavaghan.org">Mike Gavaghan</a>
 */
public class JSONObject extends LinkedHashMap<String, JSONValue> implements JSONValue
{
	/** End of line delimiter. */
	static public final String EOL = System.getProperty("line.separator");

	/** JSONValueFactory for reading from a Reader. */
	private JSONValueFactory mFactory;

	/**
	 * Create a new JSONObject.
	 * 
	 * @param factory
	 */
	protected JSONObject(JSONValueFactory factory)
	{
		mFactory = factory;
	}

	/**
	 * Create a new JSONObject.
	 * 
	 * @param factory
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
	 * Read a JSON value (presumes the key has already been read).
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
			JSONValueFactory.skipWhitespace(pbr);
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
			JSONValueFactory.skipWhitespace(pbr);
			c = JSONValueFactory.demand(pbr);
			if (c != ':') throw new JSONException(path + "." + key, "Expected ':' after key value");
			JSONValueFactory.skipWhitespace(pbr);

			// next, read a JSONValue
			JSONValue value = mFactory.read(path + "." + key, pbr);

			// add it to the map
			put(key, value);

			// next must be comma or close
			JSONValueFactory.skipWhitespace(pbr);
			c = JSONValueFactory.demand(pbr);

			if (c == ',') continue;
			if (c == '}') break;

			throw new JSONException(path, "JSON object is not grammatically correct.  Unexpected: " + c);
		}
		
		mFactory = null;
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
				if (pretty)  writer.write(newIndent);
				writer.write('\"');
				writer.write(key);
				writer.write("\":");
				if (pretty)  writer.write(" ");

				get(key).write(newIndent, writer, pretty);

				if (count != size()) writer.write(',');

				if (pretty)  writer.write(EOL);
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
