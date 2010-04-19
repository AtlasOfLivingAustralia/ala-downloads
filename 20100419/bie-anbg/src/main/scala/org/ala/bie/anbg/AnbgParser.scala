package org.ala.bie.anbg

import scala.xml._
import java.io._
import java.util._
import scala.util.matching._
/**
 * Script for parsing the AFD/APNI data as supplied in multiple XML files
 * in RDF/XML format.
 */
object AnbgParser extends Application {
  
  implicit def nodehelper(node : Node) = new NodeHelper(node)
  
  println("Starting parsing of raw RDF/XML export")
  val start = System.currentTimeMillis
  
  val rdfNs = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
  
  val allUpperCasePattern = """([A-Z]{2,})""".r
//  val commonNamePattern = """([A-Z]{1}[a-z\\-\\']{1,})[ ]{1,}([A-Z]{1}[a-z]{1,})""".r
//  val commonNamePattern2 = """([A-Z]{1}[a-z][\\-]{1}[a-z]{1,})""".r
//  val commonNamePattern3 = """([A-Za-z\\-]{1,})[ ]*([A-Z]{1}[a-z]{1,})""".r   //  //Half-and-half Goatfish
  
  //var directory = new File("/Users/davejmartin2/Desktop/rdf-100226T131714/")
  var directory = new File("/data/bie-staging/anbg/rdf/")
//  var directory = new File("/Users/davejmartin2/Desktop/afd-taxa/")
  
  var taxonNamesOutput = new OutputStreamWriter(new FileOutputStream("/data/bie-staging/anbg/taxonNames.txt"), "UTF-8");
  var taxonConceptsOutput = new OutputStreamWriter(new FileOutputStream("/data/bie-staging/anbg/taxonConcepts.txt"), "UTF-8");
  var relationshipsOutput = new OutputStreamWriter(new FileOutputStream("/data/bie-staging/anbg/relationships.txt"), "UTF-8");
  var publicationsOutput = new OutputStreamWriter(new FileOutputStream("/data/bie-staging/anbg/publications.txt"), "UTF-8");
  var vernacularsOutput = new OutputStreamWriter(new FileOutputStream("/data/bie-staging/anbg/commonNames.txt"), "UTF-8");
  var acceptedOutput = new OutputStreamWriter(new FileOutputStream("/data/bie-staging/anbg/acceptedConcepts.txt"), "UTF-8");
  
  if(directory.isDirectory){
    val subfiles = directory.listFiles
    if(subfiles != null){
    	subfiles.foreach{ file => parse(file) }
     }
  }
  
  taxonNamesOutput.flush
  taxonConceptsOutput.flush
  relationshipsOutput.flush
  publicationsOutput.flush
  vernacularsOutput.flush
  acceptedOutput.flush
  
  taxonNamesOutput.close
  taxonConceptsOutput.close
  relationshipsOutput.close
  publicationsOutput.close
  vernacularsOutput.close
  acceptedOutput.close
  
  val finish = System.currentTimeMillis
  
  println("Finished. Time taken: "+((finish-start)/1000)+" seconds")
  
  def parse(file:File){
    
	println(file.getAbsolutePath)
	if(file.getAbsolutePath.endsWith(".xml")){
	  var xml = XML.loadFile(file)
	  parseTaxonNames(xml,taxonNamesOutput);
      parseTaxonConcept(xml,taxonConceptsOutput,relationshipsOutput, vernacularsOutput, acceptedOutput)
      parsePublications(xml,publicationsOutput)
    }
  }
  
  /**
   * Parse Publications
   */
  def parsePublications(xml:Elem, pubOutput:Writer){
    val pubList = xml \\ "PublicationCitation" 
    var lastLsid = ""
    for (publication <- pubList){
      val lsid = publication.attribute(rdfNs, "about").get.toString
      if(lsid==lastLsid){ //the second element is more populated FIXME    
        var title =  (publication \ "title").text
        var authorship =  (publication \ "authorship").text
        var datePublished =  (publication \ "datePublished").text
        val publicationType = publication.singleAttrValue({node => (publication \\ "publicationType")},rdfNs,"resource")
        
        if(title!=null){
        	title = title.replaceAll("\n", "");
        }
        if(authorship!=null){
        	authorship = authorship.replaceAll("\n", "");
        }
        if(datePublished!=null){
        	datePublished = datePublished.replaceAll("\n", "");
        }
        
        writeField(pubOutput,lsid,false) //0
        writeField(pubOutput,title,false) //1
	    writeField(pubOutput,authorship,false) //2
	    writeField(pubOutput,datePublished,false) //3
	    writeField(pubOutput,publicationType,true) //4
      }
      lastLsid = lsid
    }
  }
  
  /**
   * Parse Taxon Concepts
   */
  def parseTaxonConcept(xml:Elem,tcOutput:Writer,relOutput:Writer, vernacularsOutput:Writer, acceptedOutput:Writer){
    
    val taxonConceptList = xml \\ "TaxonConcept" 
    var lastLsid = ""
    for (taxonConcept <- taxonConceptList){
      
      val lsid = taxonConcept.attribute(rdfNs, "about").get.toString
      
      if(lsid!=lastLsid){
        
    	  //start a new line for this concept
//    	  if(lastLsid!=""){
//    		  tcOutput.write('\n');
//          }
       
	      val nameLsid = taxonConcept.singleAttrValue({node => (taxonConcept \\ "hasName")},rdfNs,"resource")  
          val scientificName =  (taxonConcept \ "title").text
	      val author =  (taxonConcept \\ "AuthorTeam" \ "Simple").text
	      val authorYear =  (taxonConcept \\ "AuthorTeam" \ "Year").text
	      val publishedInCitation = taxonConcept.singleAttrValue({node => (taxonConcept \\ "publishedInCitation")},rdfNs,"resource")
	      var publishedIn =  (taxonConcept \\ "publishedIn").text
	      if(publishedIn!=null){
	    	  publishedIn = publishedIn.replaceAll("\n", "");
	      }
       
	      var isVernacularFor = "";
	      var isSynonymFor = "";
	      var isChildTaxonOf = "";
       
	      //write out relationship details
	      var rels = taxonConcept \ "hasRelationship";
	      for (rel <- rels){
	        val toTaxon = rel.singleAttrValue({node=>(rel \\ "toTaxon")},rdfNs,"resource");
	        val fromTaxon = rel.singleAttrValue({node=>(rel \\ "fromTaxon")},rdfNs,"resource")
	    	val relType = rel.singleAttrValue({node=>(rel \\ "relationshipCategory")},rdfNs,"resource");
	    	
//	    	if(relType.endsWith("HasVernacular")){
//	    	  isVernacularFor = toTaxon
//	    	  writeField(vernacularsOutput,toTaxon,true)
//	    	} else if (relType.endsWith("IsSynonymFor")){
//	    	  isSynonymFor = toTaxon
//	    	}
      
	    	if(relType.endsWith("IsVernacularFor")){
	    	  isVernacularFor = fromTaxon
	    	} else if(relType.endsWith("HasVernacular")){
	    	  isVernacularFor = toTaxon
	    	  writeField(vernacularsOutput,toTaxon,true)
	    	} else if (relType.endsWith("IsSynonymFor")){
	    	  isSynonymFor = toTaxon
	    	} else if (relType.endsWith("IsChildTaxonOf")){
	    	  isChildTaxonOf = toTaxon
	    	} else if (relType.endsWith("IsCongruentTo")){
	    	  isSynonymFor = toTaxon //mark as synonym
	    	}
      
      	    writeField(relOutput,lsid,false)
	        writeField(relOutput,toTaxon,false)
	        writeField(relOutput,relType,true)
	      }
       
	      //write out concept details
	      writeField(tcOutput,lsid,false) //0
	      writeField(tcOutput,nameLsid,false) //1
	      writeField(tcOutput,scientificName,false) //2
	      writeField(tcOutput,author,false) //3
	      writeField(tcOutput,authorYear,false) //4
	      writeField(tcOutput,publishedInCitation,false) //5
	      writeField(tcOutput,publishedIn,false) //6
	      writeField(tcOutput,isChildTaxonOf,false) //7
	      writeField(tcOutput,isSynonymFor,true) //8
//      
      } else {
        //retrieve the flag indicating its an accepted concept
        val isAcceptedBy = taxonConcept.singleAttrValue({node => (taxonConcept \\ "type")},rdfNs,"resource")
        if(isAcceptedBy!=null){
          if(isAcceptedBy.equals("http://biodiversity.org.au/rdf/ibis/APC#APCConcept")){
        	  writeField(acceptedOutput,lsid, false) //0
        	  writeField(acceptedOutput,"APC", true) //1
          } else if(isAcceptedBy.equals("ahttp://biodiversity.org.au/rdf/ibis/AFD#AFDConcept")){
        	  writeField(acceptedOutput,lsid, false) //0
        	  writeField(acceptedOutput,"AFD", true) //1
          }
        } 
      }
      lastLsid = lsid
    }
  }
  
  /**
   * Parse Taxon Names in XML element
   */
  def parseTaxonNames(xml:Elem, taxonNamesOutput:Writer){
    
	  val taxonNameList = xml \\ "TaxonName"    
	  //iterate through list
	  var lastLsid = ""
	  for (taxonName <- taxonNameList){
	
	    val currentLsid = taxonName.attribute("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "about").get.toString
	    val scientificName = (taxonName \ "title").text
	    
	    //handle repeated elements in the RDF/XML from ANBG
	    if(currentLsid!=lastLsid){
	    	//check for common names ?
	    	scientificName match {
//	    		case allUpperCasePattern(upperCased) => {writeTaxonName(taxonNamesOutput,taxonName,currentLsid,Character.toUpperCase(scientificName.charAt(0)) + scientificName.substring(1))}
//	    		case commonNamePattern(firstPart, secondPart) => {writeCommonName(commonNamesOutput,taxonName,currentLsid,scientificName)}
//	    		case commonNamePattern2(commonName) => {writeCommonName(commonNamesOutput,taxonName,currentLsid,scientificName)}
//	    		case commonNamePattern3(firstPart, secondPart) => {writeCommonName(commonNamesOutput,taxonName,currentLsid,scientificName)}
	    		case _ => { writeTaxonName(taxonNamesOutput,taxonName,currentLsid,scientificName)}
	    	}	      
	     }
	    lastLsid = currentLsid     
	  }
   }

  def writeCommonName(writer:Writer,taxonName:Node,lsid:String,scientificName:String){
    writeField(writer,lsid, false)
	writeField(writer,scientificName, false)
	writeField(writer,(taxonName \ "rankString").text, true)
  }  
  
  def writeTaxonName(writer:Writer,taxonName:Node,lsid:String,scientificName:String){

	val publishedIn = taxonName.singleAttrValue({node=>(taxonName \ "publishedInCitation")},rdfNs,"resource")
	val nomenclaturalCode = taxonName.singleAttrValue({node=>(taxonName \ "nomenclaturalCode")},rdfNs,"resource")
	var typString = (taxonName \ "typificationString").text
	if(typString!=null){
		typString = typString.replaceAll("\n", ""); 
	}
 
    writeField(writer,lsid, false) //0
	writeField(writer,scientificName, false) //1
	writeField(writer,(taxonName \ "nameComplete").text, false) //2
	writeField(writer,(taxonName \ "authorship").text, false) //3
	writeField(writer,(taxonName \ "rankString").text, false) //4
	writeField(writer,publishedIn, false) //5
	writeField(writer,nomenclaturalCode, false) //6
	writeField(writer,typString, true) //7
  }
  
  def writeField(writer:Writer,value:String,newLine:Boolean){
    writer.write('"');
    if(!value.isEmpty){
      writer.write(value.replaceAll("\"", "\\\""))
    }
    writer.write('"');
    if(newLine) 
      writer.write('\n')
    else 
      writer.write('\t')
  }
}

/**
 * Implicit converter for the scala.xml.Node class
 */
class NodeHelper (node : Node){

  def singleAttrValue(proc:Node=>NodeSeq, namespace:String, attributeName:String) : String = {
    val hasNodes = proc(node)
	for(hasNameNode <- hasNodes){
	  return hasNameNode.attribute(namespace,attributeName).get.toString
	}
    return ""
  }
}