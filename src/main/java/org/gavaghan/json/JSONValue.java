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
	 * Get the underlying value.
	 * 
	 * @return value in the implementation specific type
	 */
	public Object getValue();

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
	public void read(String path, PushbackReader pbr) throws IOException, JSONException;

	/**
	 * Render this JSON value to a Writer.
	 * 
	 * @param indent
	 * @param writer
	 * @throws IOException
	 */
	public void write(String indent, Writer writer) throws IOException;
}
