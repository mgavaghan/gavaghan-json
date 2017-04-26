package org.gavaghan.json;

import java.io.IOException;
import java.io.PushbackReader;

/**
 * 
 * @author <a href="mailto:mike@gavaghan.org">Mike Gavaghan</a>
 */
public class JSONNull implements JSONValue
{
	/**
	 * Create a new JSONBoolean.
	 */
	public JSONNull()
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
		return null;
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
		
		if (c == 'n')
		{
			if (JSONObject.demand(pbr) != 'u')  throw new JSONException("Content does not appear to be a null.");
			if (JSONObject.demand(pbr) != 'l')  throw new JSONException("Content does not appear to be a null.");
			if (JSONObject.demand(pbr) != 'l')  throw new JSONException("Content does not appear to be a null.");
		}
	
		else throw new JSONException("Content does not appear to be a null.");
	}
}
