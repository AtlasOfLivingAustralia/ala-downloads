/**************************************************************************
 *  Copyright (C) 2010 Atlas of Living Australia
 *  All Rights Reserved.
 * 
 *  The contents of this file are subject to the Mozilla Public
 *  License Version 1.1 (the "License"); you may not use this file
 *  except in compliance with the License. You may obtain a copy of
 *  the License at http://www.mozilla.org/MPL/
 * 
 *  Software distributed under the License is distributed on an "AS
 *  IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  rights and limitations under the License.
 ***************************************************************************/
package org.ala.bie.anbg

/**
 * String helper to work with tab files
 */
class TabStringHelper (string : String){

  import TabStringHelper._
  
  def removeQuotes : String = {
    if(string.startsWith("\"") && string.endsWith("\"")){
    	return string.substring(1, string.length()-1)
    }
    return string;
  }
  
  def addQuotes : String = {
    return "\""+string+"\"";
  }
  
  def splitTabDelim : Array[String] = {
    var parts = string.split('\t')
    parts = for (
      part <- parts
    ) yield part.removeQuotes
    return parts
  }
}

/**
 * Define a extensions to java.lang.String
 */
object TabStringHelper{
  implicit def tabStringhelper(string : String) = new TabStringHelper(string)
}