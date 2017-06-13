package org.gavaghan.json;

/**
 * Exception thrown when a document does not conform to the JSON grammar.
 * 
 * @author <a href="mailto:mike@gavaghan.org">Mike Gavaghan</a>
 */
public class JSONException extends Exception
{
	/** Path to the offending content. */
	private final String mPath;

	/**
	 * Create a new JSONException.
	 * 
	 * @param path
	 *           path to the value being read
	 * @param message
	 *           a description of the exception
	 */
	public JSONException(String path, String message)
	{
		super(path + ": " + message);

		mPath = path;
	}

	/**
	 * Get path to the offending content.
	 * 
	 * @return
	 */
	public String getPath()
	{
		return mPath;
	}
}
