package ala.infosource.rdu;

import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ala.infosource.ColumnHeaders;
import ala.infosource.ExtractUtils;
import ala.infosource.SiteMapGenerator;
import ala.infosource.WebUtils;

/**
 * Site map generator for Reptiles Down Under   
 * 
 * @author Tommy Wang (twang@wollemisystems.com)
 */
@SiteMapGenerator(longName="Reptiles Down Under", shortName="rdu")
public class GenerateRduSiteMap {

	public static void main(String[] args) throws Exception{

		String orderIndexPage = "http://www.reptilesdownunder.com/arod/";
		String rduSource = "http://www.reptilesdownunder.com";
		String familyIndex = WebUtils.getUrlContentAsString(orderIndexPage);

		Pattern p = Pattern.compile(
				"(?:<a href=\")?" +
				"(/arod/reptilia/[a-zA-Z/]{1,})" +
				"(?:\"><img)");
		
		Pattern p2= Pattern.compile(
				"(?:<a href=\")?" +
				"(/arod/reptilia/[a-zA-Z/]{1,})" +
				"(?:\">\\s)?" +
				"(?:<i>)" +
				"([" + ExtractUtils.SCIENTIFIC_NAME_PATTERN + "]{1,})" +
				"(?:</i>\\s)" +
				"(?:</a>)?");
		
		Pattern p3= Pattern.compile(
				"(?:<a href=\")?" +
				"(/arod/reptilia/[" + ExtractUtils.SCIENTIFIC_NAME_PATTERN + "/]{1,})" +
				"(?:\"><i>)" +
				"([" + ExtractUtils.SCIENTIFIC_NAME_PATTERN + "]{1,})" +
				"(?:</i></a>)?");

		Writer writer = ExtractUtils.getSiteMapWriter("rdu");
		//write column headings
		ExtractUtils.writeColumnHeaders(writer, new String[]{ColumnHeaders.URI, ColumnHeaders.SCIENTIFIC_NAME, ColumnHeaders.GENUS});
		
		// Remove HTML comments
		familyIndex = removeComments(familyIndex);
		
		// Split the pages to avoid duplicate entries.	
		familyIndex = familyIndex.split("<strong>Getting started</strong>")[0];
		
		Matcher m = p.matcher(familyIndex);
		int searchIdx = 0;
		// get all the order links
		while(m.find(searchIdx)){
			int endIdx = m.end();

			String familyUrl = m.group(1);
			String generatedFamilyUrl = rduSource + familyUrl; 
			//System.out.println("URL" + generatedFamilyUrl);
			// move the search start index on to current position
			
			String genusIndex = WebUtils.getUrlContentAsString(generatedFamilyUrl);
			
			// Split the pages to avoid duplicate entries.			
			String[] tmpFamily = genusIndex.split("Number of Australian species:");
			
			if (tmpFamily.length > 1) {
				genusIndex = genusIndex.split("Number of Australian species:")[1]; 
			} 
			
			Matcher m2 = p2.matcher(genusIndex);
			int searchIdx2 = 0;	
			
			//get all the family links
			while(m2.find(searchIdx2)){
				int endIdx2 = m2.end();

				String genusUrl = m2.group(1);
				String genusName = m2.group(2);
				String generatedGenusUrl = rduSource + genusUrl; 
				//System.out.println("GENUS_URL!!!" + generatedGenusUrl);
				// move the search start index on to current position
				
				String speciesIndex = WebUtils.getUrlContentAsString(generatedGenusUrl);
				
				String[] tmpSpecies = speciesIndex.split("Number of Australian species:");
				
				if (tmpSpecies.length > 1) {
					speciesIndex = tmpSpecies[1]; 
				} 
				
				Matcher m3 = p3.matcher(speciesIndex);
				int searchIdx3 = 0;		
				
				// get all the species links and write them to sitemap
				while(m3.find(searchIdx3)){
					int endIdx3 = m3.end();
					
					String scientificName = m3.group(2);
					String speciesUrl = m3.group(1);
					String generatedSpeciesUrl = rduSource + speciesUrl; 
					//System.out.println("URL" + generatedSpeciesUrl);
					// move the search start index on to current position
					
					writer.write(generatedSpeciesUrl);
					writer.write('\t');
					writer.write(scientificName);
					writer.write('\t');
					writer.write(genusName);
					writer.write('\n');
					writer.flush();
					searchIdx3 = endIdx3;
				}
				searchIdx2 = endIdx2;
			} 
			searchIdx = endIdx;
		}
		writer.flush();
		writer.close();
	}
	
	private static String removeComments(String str) {
		String result = str;
		String commentStartsWith = "<!--";
		String commentEndsWith = "-->";
		
		int startIndex = result.indexOf(commentStartsWith);
		int endIndex = 0;
		
		while (startIndex != -1) {
			endIndex = result.indexOf(commentEndsWith) + 3;
			result = result.substring(0,startIndex) + result.substring(endIndex, result.length());
			
			startIndex = result.indexOf(commentStartsWith);
		}
		//System.out.println(result);
		
		return result;
	}
	
}

