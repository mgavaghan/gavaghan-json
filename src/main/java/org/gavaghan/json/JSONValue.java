package org.gavaghan.json;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Writer;

/**
 * Interface to all JSON types.
 * 
 * @author <a href="mailto:mike@gavaghan.org">Mike Gavaghan</a>
 */
public interface JSONValue
{
	/**
	 * Get the underlying value (as a BigDecimal, a String, a Boolean, etc.)
	 * 
	 * @return value in the implementation specific type
	 */
	public Object getValue();

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
	public void read(String path, PushbackReader pbr) throws IOException, JSONException;

	/**
	 * Create a prototype instance of the same type.
	 * 
	 * @return
	 */
	public JSONValue createPrototype();

	/**
	 * Copy the value of another JSONValue into our underlying value.
	 * 
	 * @param value
	 */
	public void copyValue(JSONValue value);

	/**
	 * Create a deep copy of this instance.
	 * 
	 * @return
	 */
	public JSONValue deepCopy();

	/**
	 * Render this JSON value to a Writer. There's generally no reason to call
	 * this method directly. It is intended to be overridden by an extended type.
	 * 
	 * @param indent
	 *           indent padding
	 * @param writer
	 *           target writer
	 * @param pretty
	 *           'true' for pretty-print, 'false' for flat
	 * @throws IOException
	 *            on read failure
	 */
	public void write(String indent, Writer writer, boolean pretty) throws IOException;

	/**
	 * Render this object as a pretty-printed string.
	 * 
	 * @return
	 */
	public String toPrettyString();

	/**
	 * Render this object as a flattened string.
	 * 
	 * @return
	 */
	public String toFlatString();
}
