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
import java.math.BigDecimal;

/**
 * A JSON number represented as a BigDecimal.
 * 
 * @author <a href="mailto:mike@gavaghan.org">Mike Gavaghan</a>
 */
public class JSONNumber extends AbstractJSONValue
{
	/** The underlying value. **/
	private BigDecimal mValue;

	/**
	 * Read the whole portion of a number.
	 * 
	 * @param pbr
	 * @param builder
	 * @throws IOException
	 * @throws JSONException
	 */
	private void readWholePart(PushbackReader pbr, StringBuilder builder) throws IOException, JSONException
	{
		char c;
		for (;;)
		{
			c = JSONValueFactory.demand(pbr);
			if (Character.isDigit(c))
			{
				builder.append(c);
			}
			else
			{
				pbr.unread(c);
				break;
			}
		}
	}

	/**
	 * Read the fractional part of the number.
	 * 
	 * @param path
	 *           path to the value being read
	 * @param pbr
	 * @param builder
	 * @throws IOException
	 * @throws JSONException
	 */
	private void readFractionalPart(String path, PushbackReader pbr, StringBuilder builder) throws IOException, JSONException
	{
		char c;
		c = JSONValueFactory.demand(pbr);
		if (c == '.')
		{
			builder.append(c);

			for (;;)
			{
				c = JSONValueFactory.demand(pbr);
				if (!Character.isDigit(c))
				{
					if (builder.toString().endsWith(".")) throw new JSONException(path, "Digits expected after decimal points.");
					pbr.unread(c);
					break;
				}

				builder.append(c);
			}
		}
		else
		{
			pbr.unread(c);
		}
	}

	/**
	 * Read the exponent.
	 * 
	 * @param path
	 *           path to the value being read
	 * @param pbr
	 * @param builder
	 * @throws IOException
	 * @throws JSONException
	 */
	private void readExponent(String path, PushbackReader pbr, StringBuilder builder) throws IOException, JSONException
	{
		char c;
		c = JSONValueFactory.demand(pbr);
		if (c == 'e' || (c == 'E'))
		{
			builder.append(c);

			c = JSONValueFactory.demand(pbr);

			if (Character.isDigit(c) || (c == '+') || (c == '-'))
			{
				builder.append(c);

				for (;;)
				{
					c = JSONValueFactory.demand(pbr);
					if (!Character.isDigit(c))
					{
						pbr.unread(c);
						break;
					}

					builder.append(c);
				}
			}
			else throw new JSONException(path, "Content does not appear to be a number");
		}
		else
		{
			pbr.unread(c);
		}
	}

	/**
	 * Create a new JSONNumber.
	 * 
	 * @param value
	 */
	public JSONNumber(BigDecimal value)
	{
		if (value == null) throw new NullPointerException("Null value not allowed.  Use JSONNull instead.");
		mValue = value;
	}

	/**
	 * Create a new JSONNumber.
	 * 
	 * @param value
	 */
	public JSONNumber(long value)
	{
		mValue = new BigDecimal(value);
	}

	/**
	 * Create a new JSONNumber.
	 * 
	 * @param value
	 */
	public JSONNumber(double value)
	{
		mValue = new BigDecimal(value);
	}

	/**
	 * Create a new JSONNumber.
	 * 
	 * @param value
	 */
	public JSONNumber(String value)
	{
		mValue = new BigDecimal(value);
	}

	/**
	 * Create a new JSONNumber.
	 */
	public JSONNumber()
	{
		mValue = BigDecimal.ZERO;
	}

	/**
	 * Set the underlying value.
	 * 
	 * @param value
	 */
	public void setValue(BigDecimal value)
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
	 * Create a prototype instance of the same type.
	 * 
	 * @return
	 */
	@Override
	public JSONValue createPrototype()
	{
		return new JSONNumber();
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

		mValue = (BigDecimal) value.getValue();
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
		StringBuilder builder = new StringBuilder();

		char c = JSONValueFactory.demand(pbr);
		if (!Character.isDigit(c) && (c != '-')) throw new JSONException(path, "Content does not appear to be a number.");

		builder.append(c);

		// read the number
		if (c != '0') readWholePart(pbr, builder);
		readFractionalPart(path, pbr, builder);
		readExponent(path, pbr, builder);

		// parse and set value
		try
		{
			mValue = new BigDecimal(builder.toString());
		}
		catch (NumberFormatException exc)
		{
			throw new JSONException(path, "Illegal number format: " + builder.toString());
		}
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
