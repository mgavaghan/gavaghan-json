package org.gavaghan.json;

import java.io.IOException;
import java.io.PushbackReader;
import java.math.BigDecimal;

/**
 * 
 * @author <a href="mailto:mike@gavaghan.org">Mike Gavaghan</a>
 */
public class JSONNumber implements JSONValue
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
			c = JSONObject.demand(pbr);
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
	 * @param pbr
	 * @param builder
	 * @throws IOException
	 * @throws JSONException
	 */
	private void readFractionalPart(PushbackReader pbr, StringBuilder builder) throws IOException, JSONException
	{
		char c;
		c = JSONObject.demand(pbr);
		if (c == '.')
		{
			builder.append(c);
			
			for (;;)
			{
				c = JSONObject.demand(pbr);
				if (!Character.isDigit(c))
				{
					if (builder.toString().endsWith("."))  throw new JSONException("Digits expected after decimal points.");
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
	 * @param pbr
	 * @param builder
	 * @throws IOException
	 * @throws JSONException
	 */
	private void readExponent(PushbackReader pbr, StringBuilder builder) throws IOException, JSONException
	{
		char c;
		c = JSONObject.demand(pbr);
		if (c == 'e' || (c == 'E'))
		{
			builder.append(c);
			
			c = JSONObject.demand(pbr);
			
			if (Character.isDigit(c) || (c == '+') || (c == '-'))
			{
				builder.append(c);
				
				for (;;)
				{
					c = JSONObject.demand(pbr);
					if (!Character.isDigit(c))
					{
						pbr.unread(c);
						break;
					}
					
					builder.append(c);
				}
			}
			else throw new JSONException("Content does not appear to be a number");
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
		StringBuilder builder = new StringBuilder();

		char c = JSONObject.demand(pbr);
		if (!Character.isDigit(c) && (c != '-')) throw new JSONException("Content does not appear to be a number.");

		builder.append(c);

		// read the number
		if (c != '0')	readWholePart(pbr, builder);
		readFractionalPart(pbr, builder);
		readExponent(pbr, builder);
		
		// parse and set value
		try
		{
			mValue = new BigDecimal(builder.toString());
		}
		catch (NumberFormatException exc)
		{
			throw new JSONException("Illegal number format: " + builder.toString());
		}
	}
}
