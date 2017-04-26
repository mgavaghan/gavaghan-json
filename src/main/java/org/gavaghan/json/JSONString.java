package org.gavaghan.json;

import java.io.IOException;
import java.io.PushbackReader;

/**
 * 
 * @author <a href="mailto:mike@gavaghan.org">Mike Gavaghan</a>
 */
public class JSONString implements JSONValue
{
	/** The underlying value. **/
	private String mValue;

	/**
	 * Read a string value.
	 * 
	 * @param pbr
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	static String readString(PushbackReader pbr) throws IOException, JSONException
	{
		StringBuilder builder = new StringBuilder();

		char c = JSONObject.demand(pbr);
		if (c != '\"') throw new JSONException("Leading quote expected at start of string.");

		for (;;)
		{
			c = JSONObject.demand(pbr);

			// if closing quote
			if (c == '\"') break;

			// if escape
			if (c == '\\')
			{
				c = JSONObject.demand(pbr);

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
					hex.append(JSONObject.demand(pbr));
					hex.append(JSONObject.demand(pbr));
					hex.append(JSONObject.demand(pbr));
					hex.append(JSONObject.demand(pbr));
					try
					{
						int uchar = Integer.parseInt(hex.toString(), 16);
						builder.append((char) uchar);
					}
					catch (NumberFormatException exc)
					{
						throw new JSONException("Illegal unicode value: " + hex.toString());
					}
					break;
				default:
					throw new JSONException("Illegal escape value in string: " + c);
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
		mValue = readString(pbr);
	}
}
