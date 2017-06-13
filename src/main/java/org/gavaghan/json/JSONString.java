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
	 * @param path
	 *           path to the value being read
	 * @param pbr
	 * @return
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
	 * @return
	 */
	@Override
	public Object getValue()
	{
		return mValue;
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
		mValue = readString(path, pbr);
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
