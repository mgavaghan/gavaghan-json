package org.gavaghan.json;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Writer;

/**
 * A JSON boolean.
 * 
 * @author <a href="mailto:mike@gavaghan.org">Mike Gavaghan</a>
 */
public class JSONBoolean extends AbstractJSONValue
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
		char c = JSONValueFactory.demand(pbr);
		
		if (c == 't')
		{
			if (JSONValueFactory.demand(pbr) != 'r')  throw new JSONException(path, "Content does not appear to be a boolean.");
			if (JSONValueFactory.demand(pbr) != 'u')  throw new JSONException(path, "Content does not appear to be a boolean.");
			if (JSONValueFactory.demand(pbr) != 'e')  throw new JSONException(path, "Content does not appear to be a boolean.");
			mValue = Boolean.TRUE;
		}
		
		else if (c == 'f')
		{
			if (JSONValueFactory.demand(pbr) != 'a')  throw new JSONException(path, "Content does not appear to be a boolean.");
			if (JSONValueFactory.demand(pbr) != 'l')  throw new JSONException(path, "Content does not appear to be a boolean.");
			if (JSONValueFactory.demand(pbr) != 's')  throw new JSONException(path, "Content does not appear to be a boolean.");
			if (JSONValueFactory.demand(pbr) != 'e')  throw new JSONException(path, "Content does not appear to be a boolean.");
			mValue = Boolean.FALSE;
		}
		
		else throw new JSONException(path, "Content does not appear to be a boolean.");
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
	public void write(String indent, Writer writer, boolean pretty)  throws IOException
	{
		writer.write(mValue.toString());
	}
}
