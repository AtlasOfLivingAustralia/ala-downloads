/***************************************************************************
 * Copyright (C) 2009 Atlas of Living Australia
 * All Rights Reserved.
 *
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ***************************************************************************/
package org.ala.documentmappers;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.ala.documentmapper.OzAnimalsDocumentMapper;
import org.ala.repository.ParsedDocument;
import org.ala.repository.Triple;
import org.ala.util.WebUtils;

/**
 *
 * @author Tommy Wang (twang@wollemisystems.com)
 */
public class OzAnimalsDocumentMapperTest extends TestCase {

	final String hasScientificName = "Anseranas semipalmata";
	final String hasDescriptiveText = "The Magpie Goose has black and white plumage and yellowish legs. The feet are only partially " +
			"webbed. Can form large and noisy flocks of up to a few thousand individuals. The voice is a loud honking.";
	final String hasDistributionText = "coastal northern Australia";
	final String hasHabitatText = "open wet areas such as floodplains, swamps, dams.";
	final String hasMorphologicalText = "80-85cm, wing span 1.5m";
	final String hasDietText = "sedges, grasses, seeds";
	final String hasFamily = "Anseranatidae";
	final String hasGenus = "Anseranas";
	final String hasSpecies = "semipalmata";
	final String hasClass = "Aves";
	final String hasOrder = "Anseriformes";
	final String hasCommonName = "Magpie Goose";
	final String hasImageLicenseInfo = "ozwildlife - http://creativecommons.org/licenses/by-nc-nd/3.0/";
	final String hasReproductionText = "The nest is on the ground made of trampled vegetation. Typical clutch is 5-14 eggs- average 7.";

	
	
	public void test() throws Exception {
		OzAnimalsDocumentMapper dm = new OzAnimalsDocumentMapper();
		String uri = "http://www.ozanimals.com/Bird/Magpie-Goose/Anseranas/semipalmata.html";
		String xml = WebUtils.getHTMLPageAsXML(uri);
		List<ParsedDocument> parsedDocs = dm.map(uri, xml.getBytes());
		
		for(ParsedDocument pd : parsedDocs){
			
			DebugUtils.debugParsedDoc(pd);
			
			List<Triple<String,String,String>> triples = pd.getTriples();
			for(Triple triple: triples){
				
				String predicate = (String) triple.getPredicate();
				String object = (String) triple.getObject();
				
				if (predicate.endsWith("hasScientificName")) {
					Assert.assertEquals(object, hasScientificName);		    	  
				}
				
				if (predicate.endsWith("hasDistributionText")) {
					Assert.assertEquals(object, hasDistributionText);		    	  
				}
				
				if (predicate.endsWith("hasHabitatText")) {
					Assert.assertEquals(object, hasHabitatText);		    	  
				}
				
				if (predicate.endsWith("hasMorphologicalText")) {
					Assert.assertEquals(object, hasMorphologicalText);		    	  
				}
				
				if (predicate.endsWith("hasDietText")) {
					Assert.assertEquals(object, hasDietText);		    	  
				}
				
				if (predicate.endsWith("hasFamily")) {
					Assert.assertEquals(object, hasFamily);		    	  
				}
				
				if (predicate.endsWith("hasGenus")) {
					Assert.assertEquals(object, hasGenus);		    	  
				}	
				
				if (predicate.endsWith("hasSpecies")) {
					Assert.assertEquals(object, hasSpecies);		    	  
				}	
				
				if (predicate.endsWith("hasCommonName")) {
					Assert.assertEquals(object, hasCommonName);		    	  
				}
				
				if (predicate.endsWith("hasClass")) {
					Assert.assertEquals(object, hasClass);		    	  
				}
				
				if (predicate.endsWith("hasOrder")) {
					Assert.assertEquals(object, hasOrder);		    	  
				}
				
				if (predicate.endsWith("hasImageLicenseInfo")) {
					Assert.assertEquals(object, hasImageLicenseInfo);		    	  
				}
				
				if (predicate.endsWith("hasDescriptiveText")) {
					Assert.assertEquals(object, hasDescriptiveText);		    	  
				}
				
				if (predicate.endsWith("hasReproductionText")) {
					Assert.assertEquals(object, hasReproductionText);		    	  
				}
			}
		}
	}
	
}
