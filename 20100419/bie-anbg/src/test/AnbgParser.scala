package test

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
  
  println("Starting")
  val start = System.currentTimeMillis
  
  val rdfNs = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
  
  val allUpperCasePattern = """([A-Z]{2,})""".r
  val commonNamePattern = """([A-Z]{1}[a-z\\-\\']{1,})[ ]{1,}([A-Z]{1}[a-z]{1,})""".r
  val commonNamePattern2 = """([A-Z]{1}[a-z][\\-]{1}[a-z]{1,})""".r
  val commonNamePattern3 = """([A-Za-z\\-]{1,})[ ]*([A-Z]{1}[a-z]{1,})""".r   //  //Half-and-half Goatfish
  
  var directory = new File("/Users/davejmartin2/Desktop/20091204-1/")
//  var directory = new File("/Users/davejmartin2/Desktop/afd.taxon/")
  
  var taxonNamesOutput = new OutputStreamWriter(new FileOutputStream("/tmp/taxonNames.txt"), "UTF-8");
  var taxonConceptsOutput = new OutputStreamWriter(new FileOutputStream("/tmp/taxonConcepts.txt"), "UTF-8");
  var relationshipsOutput = new OutputStreamWriter(new FileOutputStream("/tmp/relationships.txt"), "UTF-8");
  var publicationsOutput = new OutputStreamWriter(new FileOutputStream("/tmp/publications.txt"), "UTF-8");
  
  if(directory.isDirectory){
    val subfiles = directory.listFiles
    if(subfiles != null)
      subfiles.foreach{ file => parse(file) }
  }
  
//  concept2NameOutput.flush
  taxonNamesOutput.flush
//  commonNamesOutput.flush
  taxonConceptsOutput.flush
  relationshipsOutput.flush
  publicationsOutput.flush
  
//  concept2NameOutput.close
  taxonNamesOutput.close
//  commonNamesOutput.close
  taxonConceptsOutput.close
  relationshipsOutput.close
  publicationsOutput.close
  
  val finish = System.currentTimeMillis
  
  println("Time taken: "+((finish-start)/1000)+" seconds")
  
  def parse(file:File){
    
	//println(file.getAbsolutePath)
	if(file.getAbsolutePath.endsWith(".xml")){
	  var xml = XML.loadFile(file)
	  parseTaxonNames(xml,taxonNamesOutput);
      parseTaxonConcept(xml,taxonConceptsOutput,relationshipsOutput)
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
        val title =  (publication \ "title").text
        val authorship =  (publication \ "authorship").text
        val datePublished =  (publication \ "datePublished").text
        val publicationType = publication.singleAttrValue({node => (publication \\ "publicationType")},rdfNs,"resource")
        
        writeField(pubOutput,lsid,false)
        writeField(pubOutput,title,false)
	    writeField(pubOutput,authorship,false)
	    writeField(pubOutput,datePublished,false)
	    writeField(pubOutput,publicationType,true)
      }
      lastLsid = lsid
    }
  }
  
  /**
   * Parse Taxon Concepts
   */
  def parseTaxonConcept(xml:Elem,tcOutput:Writer,relOutput:Writer){
    
    val taxonConceptList = xml \\ "TaxonConcept" 
    var lastLsid = ""
    for (taxonConcept <- taxonConceptList){
      
      val lsid = taxonConcept.attribute(rdfNs, "about").get.toString
      
      if(lsid!=lastLsid){
	      
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
       
	      //write out relationship details
	      var rels = taxonConcept \ "hasRelationship";
	      for (rel <- rels){
	        val toTaxon = rel.singleAttrValue({node=>(rel \\ "toTaxon")},rdfNs,"resource");
	    	val relType = rel.singleAttrValue({node=>(rel \\ "relationshipCategory")},rdfNs,"resource");  
	    	
	    	if(relType.endsWith("IsVernacularFor")){
	    	  isVernacularFor = toTaxon
	    	} else if (relType.endsWith("IsSynonymFor")){
	    	  isSynonymFor = toTaxon
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
	      writeField(tcOutput,isSynonymFor,false) //7
	      writeField(tcOutput,isVernacularFor,true) //8
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

class NodeHelper (node : Node){

  def singleAttrValue(proc:Node=>NodeSeq, namespace:String, attributeName:String) : String = {
    val hasNodes = proc(node)
	for(hasNameNode <- hasNodes){
	  return hasNameNode.attribute(namespace,attributeName).get.toString
	}
    return ""
  }
}

