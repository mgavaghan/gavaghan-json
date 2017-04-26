package org.gavaghan.json;

import java.io.IOException;
import java.io.PushbackReader;

/**
 * A JSON boolean.
 * 
 * @author <a href="mailto:mike@gavaghan.org">Mike Gavaghan</a>
 */
public class JSONBoolean implements JSONValue
{
	/** The underlying value. **/
	private Boolean mValue;

	/**
	 * Create a new JSONBoolean.
	 * 
	 * @param value
	 */
	public JSONBoolean(Boolean value)
	{
		if (value == null) throw new NullPointerException("Null value not allowed.  Use JSONNull instead.");
		mValue = value;
	}

	/**
	 * Create a new JSONBoolean.
	 */
	public JSONBoolean()
	{
		mValue = Boolean.FALSE;
	}

	/**
	 * Set the underlying value.
	 * 
	 * @param value
	 */
	public void setValue(Boolean value)
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
		char c = JSONObject.demand(pbr);
		
		if (c == 't')
		{
			if (JSONObject.demand(pbr) != 'r')  throw new JSONException(path, "Content does not appear to be a boolean.");
			if (JSONObject.demand(pbr) != 'u')  throw new JSONException(path, "Content does not appear to be a boolean.");
			if (JSONObject.demand(pbr) != 'e')  throw new JSONException(path, "Content does not appear to be a boolean.");
			mValue = Boolean.TRUE;
		}
		
		else if (c == 'f')
		{
			if (JSONObject.demand(pbr) != 'a')  throw new JSONException(path, "Content does not appear to be a boolean.");
			if (JSONObject.demand(pbr) != 'l')  throw new JSONException(path, "Content does not appear to be a boolean.");
			if (JSONObject.demand(pbr) != 's')  throw new JSONException(path, "Content does not appear to be a boolean.");
			if (JSONObject.demand(pbr) != 'e')  throw new JSONException(path, "Content does not appear to be a boolean.");
			mValue = Boolean.FALSE;
		}
		
		else throw new JSONException(path, "Content does not appear to be a boolean.");
	}
}