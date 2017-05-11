package org.gavaghan.json;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Base implementation of a JSONValue
 * 
 * @author <a href="mailto:mike@gavaghan.org">Mike Gavaghan</a>
 */
public abstract class AbstractJSONValue implements JSONValue
{
	/**
	 * Render this object as a string.
	 * 
	 * @param value
	 */
	static public String toString(JSONValue value)
	{
		String str;

		try (StringWriter writer = new StringWriter())
		{
			value.write("", writer);
			str = writer.toString();
		}
		catch (IOException exc)
		{
			throw new RuntimeException("Unexpectedly failed to render string", exc);
		}

		return str;
	}

	/**
	 * Render this object as a string.
	 */
	@Override
	public String toString()
	{
		return toString(this);
	}
}
