package org.gavaghan.json;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.LinkedHashMap;

/**
 * A JSON object as define by http://www.json.org/
 * 
 * @author <a href="mailto:mike@gavaghan.org">Mike Gavaghan</a>
 */
public class JSONObject extends LinkedHashMap<String, JSONValue> implements JSONValue
{
	/**
	 * Skip to first non-whitespace character.
	 * 
	 * @param pbr
	 * @throws IOException
	 */
	static void skipWhitespace(PushbackReader pbr) throws IOException
	{
		for (;;)
		{
			int c = pbr.read();

			if (c < 0) break; // bail on EOF

			// if non-whitespace found, push it back and exit
			if (!Character.isWhitespace(c))
			{
				pbr.unread(c);
				break;
			}
		}
	}

	/**
	 * Demand a characters and throw a JSONException if EOF.
	 * 
	 * @param rdr
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	static char demand(Reader rdr) throws IOException, JSONException
	{
		int c = rdr.read();
		if (c < 0) throw new JSONException("Out of data while reading JSON object.");
		return (char) c;
	}

	/**
	 * Read a JSONObject from a Reader.
	 * 
	 * @param reader
	 * @return a JSONObject, or null is EOF is reached.
	 * @throws IOException
	 * @throws JSONException
	 */
	static public JSONObject read(Reader reader) throws IOException, JSONException
	{
		JSONObject json = new JSONObject();
		PushbackReader pbr = new PushbackReader(reader);

		// look for start of object
		skipWhitespace(pbr);
		int c = pbr.read();

		// bail out early if EOF
		if (c < 0) return null;

		pbr.unread(c);

		json.read(pbr);

		return json;
	}
	
	/**
	 * Read a JSONValue.
	 * 
	 * @param pbr
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	static JSONValue readJSONValue(PushbackReader pbr) throws IOException, JSONException
	{
		JSONValue value;
		char c = JSONObject.demand(pbr);
		
		pbr.unread(c);
		
		// is it a string?
		if (c == '\"')
		{
			value = new JSONString();
		}
		// is it a number?
		else if (Character.isDigit(c) || (c == '-'))
		{
			value = new JSONNumber();
		}
		// is it an array?
		else if (c == '[')
		{
			value = new JSONArray();
		}
		// is it an object?
		else if (c == '{')
		{
			value = new JSONObject();
		}
		// is it a boolean?
		else if ((c == 't') || (c == 'f'))
		{
			value = new JSONBoolean();
		}
		// is it a null?
		else if (c == 'n')
		{
			value = new JSONNull();
		}
		// else, grammar error
		else
		{
			throw new JSONException("Illegal start of JSON value: " + c);
		}
		
		value.read(pbr);
		
		return value;
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
		// assert we have an opening brace
		char c = JSONObject.demand(pbr);
		if (c != '{') throw new JSONException("Failed to find '{' at start of JSON object.");

		for (;;)
		{
			String key;
			
			// next is either a key or a closing brace
			JSONObject.skipWhitespace(pbr);
			c = JSONObject.demand(pbr);

			// is it a string?
			if (c == '\"')
			{
				pbr.unread(c);
				key = JSONString.readString(pbr);
			}
			// is it a closing brace?
			else if (c == '}')
			{
				break;
			}
			// else, it's poorly formed
			else
			{
				throw new JSONException("JSON object is not grammatically correct.  Unexpected: " + (int)c);
			}
			
			// next ought to be a colon
			JSONObject.skipWhitespace(pbr);
			c = JSONObject.demand(pbr);
			if (c != ':')  throw new JSONException("Expected ':' after key value");
			JSONObject.skipWhitespace(pbr);
			
			// next, read a JSONValue
			JSONValue value = readJSONValue(pbr);
			
			// add it to the map
			put(key,value);
			
			// next must be comma or close
			JSONObject.skipWhitespace(pbr);
			c = JSONObject.demand(pbr);
			
			if (c == ',')  continue;
			if (c == '}')  break;
			
			throw new JSONException("JSON object is not grammatically correct.  Unexpected: " + c);
		}
	}
}
