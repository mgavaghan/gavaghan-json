package org.gavaghan.json;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * A JSON array represented as a List&lt;JSONValue&gt;
 * 
 * @author <a href="mailto:mike@gavaghan.org">Mike Gavaghan</a>
 */
public class JSONArray extends AbstractJSONValue
{
	/** The underlying value. **/
	private List<JSONValue> mValue;

	/** JSONValueFactory for reading from a Reader. */
	private JSONValueFactory mFactory;

	/**
	 * Create a new JSONArray.
	 * 
	 * @param value
	 */
	public JSONArray(List<JSONValue> value)
	{
		if (value == null) throw new NullPointerException("Null value not allowed.  Use JSONNull instead.");
		mValue = value;
	}

	/**
	 * Create a new JSONArray.
	 * 
	 * @param factory
	 */
	protected JSONArray(JSONValueFactory factory)
	{
		mValue = new ArrayList<JSONValue>();
		mFactory = factory;
	}

	/**
	 * Create a new JSONArray for reading.
	 */
	public JSONArray()
	{
		mValue = new ArrayList<JSONValue>();
	}

	/**
	 * Set the underlying value.
	 * 
	 * @param value
	 */
	public void setValue(List<JSONValue> value)
	{
		if (value == null) throw new NullPointerException("Null value not allowed.  Use JSONNull instead.");
		mValue = value;
	}

	/**
	 * Get the underlying value.
	 * 
	 * @return
	 */
	@Override
	public Object getValue()
	{
		return mValue;
	}

	/**
	 * Create a prototype instance of the same type.
	 * 
	 * @return
	 */
	@Override
	public JSONValue createPrototype()
	{
		return new JSONArray();
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

      @SuppressWarnings("unchecked")
		List<JSONValue> source = (List<JSONValue>) value.getValue();
      mValue = new ArrayList<JSONValue>();

      for (JSONValue json : source)
      {
        mValue.add(json.deepCopy());
      }
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
		char c = JSONValueFactory.demand(pbr);
		if (c != '[') throw new JSONException(path, "Content does not appear to be an array.");

		// empty array is an easy out
		JSONValueFactory.skipWhitespace(pbr);
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
				JSONValueFactory.skipWhitespace(pbr);
				c = JSONValueFactory.demand(pbr);

				// is end?
				if (c == ']') return;

				// is more
				if (c == ',')
				{
					JSONValueFactory.skipWhitespace(pbr);
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
}
