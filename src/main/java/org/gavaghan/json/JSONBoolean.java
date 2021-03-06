/******************************************************************************
   Copyright 2018 Mike Gavaghan

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
******************************************************************************/
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
    * Get the underlying value as a boolean.
    * 
    * @since 1.2
    * 
    * @return
    */
   public boolean getBooleanValue()
   {
      return mValue.booleanValue();
   }
   
	/**
	 * Create a prototype instance of the same type.
	 * 
	 * @return
	 */
	@Override
	public JSONValue createPrototype()
	{
		return new JSONBoolean();
	}

	/**
	 * Copy the value of another JSONValue into our underlying value.
	 * 
	 * @param value
	 */
	@Override
	public void copyValue(JSONValue value)
	{
		if (!getClass().isAssignableFrom(value.getClass())) throw new RuntimeException("Can't assign a " + value.getClass().getName() + " to a " + getClass().getName());

		mValue = (Boolean) value.getValue();
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
		char c = JSONValueFactory.demand(pbr);

		if (c == 't')
		{
			if (JSONValueFactory.demand(pbr) != 'r') throw new JSONException(path, "Content does not appear to be a boolean.");
			if (JSONValueFactory.demand(pbr) != 'u') throw new JSONException(path, "Content does not appear to be a boolean.");
			if (JSONValueFactory.demand(pbr) != 'e') throw new JSONException(path, "Content does not appear to be a boolean.");
			mValue = Boolean.TRUE;
		}

		else if (c == 'f')
		{
			if (JSONValueFactory.demand(pbr) != 'a') throw new JSONException(path, "Content does not appear to be a boolean.");
			if (JSONValueFactory.demand(pbr) != 'l') throw new JSONException(path, "Content does not appear to be a boolean.");
			if (JSONValueFactory.demand(pbr) != 's') throw new JSONException(path, "Content does not appear to be a boolean.");
			if (JSONValueFactory.demand(pbr) != 'e') throw new JSONException(path, "Content does not appear to be a boolean.");
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
	public void write(String indent, Writer writer, boolean pretty) throws IOException
	{
		writer.write(mValue.toString());
	}
}
