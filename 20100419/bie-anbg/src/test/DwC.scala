package test

import scala.xml._
import java.io._
import java.util._
import scala.util.matching._

object DwC extends Application {

	var dwcOutput = new OutputStreamWriter(new FileOutputStream("/tmp/dwc.txt"), "UTF-8");
    
    implicit def nodehelper(node : Node) = new NodeHelper(node)
  
    val rdfNs = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"    
    
    var directory = new File("/Users/davejmartin2/Desktop/20091204-1/")
  
    if(directory.isDirectory){
    	val subfiles = directory.listFiles
    	if(subfiles != null)
    		subfiles.foreach{ file => parse(file) }
    }
    
    def parse(file:File){
    	//println(file.getAbsolutePath)
    	if(file.getAbsolutePath.endsWith(".xml") && file.getAbsolutePath.contains(".taxon")){
    		var xml = XML.loadFile(file)
    		parseTaxonConcept(xml,dwcOutput)
    	}
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
    
    def parseTaxonConcept(xml:Elem,tcOutput:Writer){
    
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
		      var isChildTaxonOf = "";
       
		      //write out relationship details
		      var rels = taxonConcept \ "hasRelationship";
		      for (rel <- rels){
		        val toTaxon = rel.singleAttrValue({node=>(rel \\ "toTaxon")},rdfNs,"resource")
		        val fromTaxon = rel.singleAttrValue({node=>(rel \\ "fromTaxon")},rdfNs,"resource")
		    	val relType = rel.singleAttrValue({node=>(rel \\ "relationshipCategory")},rdfNs,"resource")  
		    	
		    	if(relType.endsWith("IsVernacularFor")){
		    	  isVernacularFor = toTaxon
		    	} else if (relType.endsWith("IsSynonymFor")){
		    	  isSynonymFor = toTaxon
		    	} else if (relType.endsWith("IsChildTaxonOf")){
		    	  isChildTaxonOf = toTaxon
		    	} else if (relType.endsWith("IsCongruentTo")){
		    	  isSynonymFor = toTaxon //mark as synonym
		    	}
		      }       
       
		      //write out concept details
		      writeField(tcOutput,lsid,false) //0
		      writeField(tcOutput,nameLsid,false) //1
		      writeField(tcOutput,scientificName,false) //2
		      writeField(tcOutput,author,false) //3
		      writeField(tcOutput,authorYear,false) //4
		      writeField(tcOutput,isChildTaxonOf,false) //5       
		      writeField(tcOutput,isSynonymFor,false) //6
		      writeField(tcOutput,isVernacularFor,true) //7
    		}
    	lastLsid = lsid
    }
  }
}
