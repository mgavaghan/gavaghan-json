package org.gavaghan.json;

/**
 * Except thrown when a document does not conform to the JSON grammar.
 * 
 * @author <a href="mailto:mike@gavaghan.org">Mike Gavaghan</a>
 */
public class JSONException extends Exception
{
	/**
	 * Create a new JSONException.
	 * 
	 * @param message
	 *           a description of the exception
	 */
	public JSONException(String message)
	{
		super(message);
	}
}
