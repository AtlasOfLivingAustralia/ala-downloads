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

import java.io._
import java.util._
import java.util.regex._
import scala.util.matching._
import org.apache.lucene.analysis._
import org.apache.lucene.document._
import org.apache.lucene.index._
import org.apache.lucene.search._
import org.apache.commons.io._
/**
 * Reads the outputs from AnbgParser and produces 3 files
 * ready for importing into ChecklistBank. These 3 files are:
 *
 * 1) AFD extract
 * 2) APC extract - these are the elements of APNI marked as accepted concepts
 * 3) APNI extract
 *
 * This works by:
 * 1) producing a lucene index from the taxonNames.txt and acceptedConcepts.txt produced by AnbgParser
 * 2) reading taxonConcepts.txt line by line
 * 3) lookup name details from taxonNames.txt
 * 4) lookup if the concept is "accepted" in acceptedConcepts.txt - this really just tells us if we have
 * an APC or APNI concept, not relevant for AFD
 * 5) outputting into /tmp/afd.txt, /tmp/apc.txt or /tmp/apni.txt
 */
object DwcCreator extends Application {

  import FileHelper._
  import TabStringHelper._
  
  //create lucene index for taxonNames.txt and acceptedConcepts.txt
  println("starting...")
  
  val tnIdx = "/tmp/taxonNameIndex"
  val acIdx = "/tmp/acceptedIndex"
  val cnIdx = "/tmp/commonNamesIndex"
  
  createIndicies(tnIdx, acIdx)
  
  //read the concepts
  val tc = new File("/data/bie-staging/anbg/taxonConcepts.txt")
  val tnis = new IndexSearcher(tnIdx)
  val accis = new IndexSearcher(acIdx)
  val cnis = new IndexSearcher(cnIdx)

  val afdw = new OutputStreamWriter(new FileOutputStream("/data/bie-staging/anbg/afd.txt"), "UTF-8")
  val apcw = new OutputStreamWriter(new FileOutputStream("/data/bie-staging/anbg/apc.txt"), "UTF-8")
  val apniw = new OutputStreamWriter(new FileOutputStream("/data/bie-staging/anbg/apni.txt"), "UTF-8")

  tc.foreachLine{ line => processTc(line, tnis, accis, cnis, afdw, apcw, apniw) }

  afdw.flush
  apcw.flush
  apniw.flush
  
  afdw.close
  apcw.close
  apniw.close
  
  println("done")
  
  def createIndicies(tnIdx:String, acIdx:String){
    
	//create a name index
	val tnFile = new File(tnIdx).deleteAndCreate
    //initialise lucene
	val analyzer = new KeywordAnalyzer()
	val tniw = new IndexWriter(tnFile, analyzer, IndexWriter.MaxFieldLength.UNLIMITED)
	//taxon names files to index
	val tr = new File("/data/bie-staging/anbg/taxonNames.txt")
	tr.foreachLine{ line => addToTnIndex(tniw,line) }
	tniw.flush
	tniw.close
 
	//accepted files to index
	val acFile = new File(acIdx).deleteAndCreate
    val aciw = new IndexWriter(acFile, analyzer, IndexWriter.MaxFieldLength.UNLIMITED)
 	val acc = new File("/data/bie-staging/anbg/acceptedConcepts.txt")
 	acc.foreachLine{ line => addToAccIndex(aciw,line) }
    aciw.flush
	aciw.close
 
	//create common names index
	val cnFile = new File(cnIdx).deleteAndCreate
    val cniw = new IndexWriter(cnFile, analyzer, IndexWriter.MaxFieldLength.UNLIMITED)
 	val cnn = new File("/data/bie-staging/anbg/commonNames.txt")
 	cnn.foreachLine{ line => addToCnIndex(cniw,line) }
    cniw.flush
	cniw.close
 
	println("completed index creation.")
  }
  
  def processTc(line:String, tnis:IndexSearcher, accis:IndexSearcher, cnis:IndexSearcher, afdw:Writer, apcw:Writer, apniw:Writer) {
	
    val parts = line.splitTabDelim
    val lsid = parts(0)
    val nameLsid = parts(1)

    //look it up rank using the index
    val rank = indexLookup("lsid", nameLsid, "rank", tnis)
    val authority = indexLookup("lsid", lsid, "authority", accis)
    
    val commonNameLsid = indexLookup("lsid", lsid, "lsid", cnis)
    
    //if its not a common name, write it out
    if(commonNameLsid==null){
	    var writer = null
	    if("AFD".equals(authority)){ writeTcOut(afdw, line, rank)} 
	    else if("APC".equals(authority)){ writeTcOut(apcw, line, rank) }
	    else {
		    if(nameLsid.contains("afd.")){
		      writeTcOut(afdw, line, rank)
		    } else {
		      writeTcOut(apniw, line, rank)
		    }
		}
     }
  }
  
  def indexLookup(lookupKey:String, lookupValue:String, value2get:String, is:IndexSearcher) : String = {
    val query = new TermQuery(new Term(lookupKey, lookupValue))
	val topDocs = is.search(query, 1)
	for(scoreDoc <- topDocs.scoreDocs){
		val doc = is.doc(scoreDoc.doc)
		return doc.get(value2get)
	}
    return null //this isnt the scala way!!
  }
  
  def writeTcOut(writer:Writer, line:String, rank:String) {
    writer.write(line)
    writer.write('\t')
    if(rank!=null){
      writer.write(rank.addQuotes)
    } else {
      writer.write("\"\"")
    }
    writer.write('\n')
  }
  
  def addToTnIndex(iw:IndexWriter, line:String){
    val parts = line.splitTabDelim
    val doc = new Document
    doc.add(new Field("lsid", parts(0), Field.Store.YES, Field.Index.ANALYZED))
    doc.add(new Field("nameString", parts(1), Field.Store.YES, Field.Index.NO))
    doc.add(new Field("rank", parts(4), Field.Store.YES, Field.Index.NO))
    iw.addDocument(doc)
  }
  
  def addToCnIndex(iw:IndexWriter, line:String){
    val parts = line.splitTabDelim
    val doc = new Document
    doc.add(new Field("lsid", parts(0), Field.Store.YES, Field.Index.ANALYZED))
    iw.addDocument(doc)
  }
  
  def addToAccIndex(iw:IndexWriter, line:String){
	val parts = line.splitTabDelim
	val doc = new Document
    if(parts.length==2){
	    doc.add(new Field("lsid", parts(0), Field.Store.YES, Field.Index.ANALYZED))
	    doc.add(new Field("authority", parts(1), Field.Store.YES, Field.Index.ANALYZED))
	    iw.addDocument(doc)
    } else {
      println("Malformed entry in acceptedConcepts:  "+line)
    }
  }
}
