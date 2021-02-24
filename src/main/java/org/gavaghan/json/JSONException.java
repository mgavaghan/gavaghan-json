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
    * @param path    path to the value being read
    * @param message a description of the exception
    */
   public JSONException(String path, String message)
   {
      super(path + ": " + message);

      mPath = path;
   }

   /**
    * Create a new JSONException with a specified cause.
    * 
    * @param path    path to the value being read
    * @param message a description of the exception
    * @param cause   cause of failure
    */
   public JSONException(String path, String message, Throwable cause)
   {
      super(path + ": " + message, cause);

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
