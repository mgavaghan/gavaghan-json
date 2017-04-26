package org.gavaghan.json;

import java.io.IOException;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author <a href="mailto:mike@gavaghan.org">Mike Gavaghan</a>
 */
public class JSONArray implements JSONValue
{
	/** The underlying value. **/
	private List<JSONValue> mValue;

	/**
	 * Create a new JSONArray.
	 * 
	 * @param value
	 */
	public JSONArray(List<JSONValue> value)
	{
		if (value == null)  throw new NullPointerException("Null value not allowed.  Use JSONNull instead.");
		mValue = value;
	}

	/**
	 * Create a new JSONArray.
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
		if (value == null)  throw new NullPointerException("Null value not allowed.  Use JSONNull instead.");
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
	 * Read a JSON value (presumes the key has already been read).
	 * 
	 * @param pbr
	 *           source reader
	 * @throws IOException
	 *            on read failure
	 * @throws JSONException
	 *            on grammar error
	 */
	@Override
	public void read(PushbackReader pbr) throws IOException, JSONException
	{
		char c = JSONObject.demand(pbr);
		if (c != '[') throw new JSONException("Content does not appear to be an array.");
		
		// empty array is an easy out
		JSONObject.skipWhitespace(pbr);
		c = JSONObject.demand(pbr);
		if (c == ']')  return;
		pbr.unread(c);

		// loop through values
		for (;;)
		{
			JSONValue value = JSONObject.readJSONValue(pbr);
			mValue.add(value);
			
			// get next non-whitespace
			JSONObject.skipWhitespace(pbr);
			c = JSONObject.demand(pbr);
			
			// is end?
			if (c == ']')  return;
			
			// is more
			if (c == ',')
			{
				JSONObject.skipWhitespace(pbr);
				continue;
			}
			
			throw new JSONException("Incorrectly formatted array: " + c);
		}
	}
}
