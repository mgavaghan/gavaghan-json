package org.gavaghan.json;

import java.io.IOException;
import java.io.PushbackReader;

/**
 * Extends the standards-based <code>JSONValueFactory</code> to recognized Java
 * style line and block comments. Comments are treated as whitespace and not
 * stored in the model.
 * 
 * @since 1.1.0
 * 
 * @author <a href="mailto:mike@gavaghan.org">Mike Gavaghan</a>
 */
public class CommentedJSONValueFactory extends JSONValueFactory
{
	/** The default implementation. */
	static public final JSONValueFactory COMMENTED_DEFAULT = new CommentedJSONValueFactory();

	/**
	 * 
	 * @param pbr
	 * @throws IOException
	 */
	private void skipLineComment(PushbackReader pbr) throws IOException
	{
		for (;;)
		{
			int c = pbr.read();

			// out of data? quit
			if (c < 0) break;

			// end of line? quit
			if ((c == '\n') || (c == '\r')) break;
		}
	}

	/**
	 * 
	 * @param pbr
	 * @throws IOException
	 * @throws JSONException
	 */
	private void skipBlockComment(PushbackReader pbr) throws IOException, JSONException
	{
		boolean star = false;

		for (;;)
		{
			int c = pbr.read();

			// out of data? throw exception
			if (c < 0) throw new JSONException("$", "Unterminated block comment at end of file");

			if (star && (c == '/')) break;

			star = (c == '*');
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.gavaghan.json.JSONValueFactory#getPushbackBufferSize()
	 */
	@Override
	public int getPushbackBufferSize()
	{
		// We need an extra pushback to identify a comment.
		return Math.max(super.getPushbackBufferSize(), 2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gavaghan.json.JSONValueFactory#skipWhitespace(java.io.PushbackReader)
	 */
	@Override
	public void skipWhitespace(PushbackReader pbr) throws IOException, JSONException
	{
		for (;;)
		{
			int c = pbr.read();

			if (c < 0) break; // bail on EOF

			if (!Character.isWhitespace(c))
			{
				// it's not whitespace, so see if it's the start of a comment
				if (c == '/')
				{
					int next = pbr.read();

					// is it a line comment?
					if (next == '/')
					{
						skipLineComment(pbr);
						continue;
					}
					// is it a block comment?
					else if (next == '*')
					{
						skipBlockComment(pbr);
						continue;
					}

					// else, unread - it's the end of the whitespace
					pbr.unread(c);
				}

				pbr.unread(c);
				break;
			}
		}
	}
}
