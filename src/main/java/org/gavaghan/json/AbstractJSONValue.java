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
import java.io.StringWriter;

/**
 * Base implementation of a <code>JSONValue</code>.
 * 
 * @author <a href="mailto:mike@gavaghan.org">Mike Gavaghan</a>
 */
public abstract class AbstractJSONValue implements JSONValue
{
	/**
	 * Render a <code>JSONValue</code> as a string.
	 * 
	 * @param value
	 *           the <code>JSONValue</code> to render
	 * @param pretty
	 *           'true' to pretty-print with line feeds and indentation, 'false'
	 *           to render on a single line.
	 * @return the rendered value
	 */
	static public String toString(JSONValue value, boolean pretty)
	{
		String str;

		try (StringWriter writer = new StringWriter())
		{
			value.write("", writer, pretty);
			str = writer.toString();
		}
		catch (IOException exc)
		{
			throw new RuntimeException("Unexpectedly failed to render string", exc);
		}

		return str;
	}

	/*
	 * @Override(non-Javadoc)
	 * 
	 * @see org.gavaghan.json.JSONValue#deepCopy()
	 */
	@Override
	public JSONValue deepCopy()
	{
		JSONValue copy = createPrototype();
		copy.copyValue(this);
		return copy;

	}

	/**
	 * Render this object as a pretty-printed string.
	 * 
	 * @return this instance rendered as a JSON string
	 */
	@Override
	public String toString()
	{
		return toPrettyString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gavaghan.json.JSONValue#toPrettyString()
	 */
	@Override
	public String toPrettyString()
	{
		return toString(this, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gavaghan.json.JSONValue#toFlatString()
	 */
	@Override
	public String toFlatString()
	{
		return toString(this, false);
	}
}
