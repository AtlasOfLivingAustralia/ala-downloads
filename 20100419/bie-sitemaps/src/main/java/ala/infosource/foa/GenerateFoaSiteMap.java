package ala.infosource.foa;

import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ala.infosource.ColumnHeaders;
import ala.infosource.ExtractUtils;
import ala.infosource.SiteMapGenerator;
import ala.infosource.WebUtils;

/**
 * Site map generator for Fishes of Australia Online  
 * 
 * @author Tommy Wang (twang@wollemisystems.com)
 */
@SiteMapGenerator(longName="Fishes of Australia Online", shortName="foa")
public class GenerateFoaSiteMap {

	public static void main(String[] args) throws Exception{

		String orderIndexPage = "http://foa.webboy.net/fishes/";
		String foaSource = "http://foa.webboy.net";
		String orderIndex = WebUtils.getUrlContentAsString(orderIndexPage);

		Pattern p = Pattern.compile(
				"(?:<a href=\")?" +
				"(/order/[a-zA-Z]{1,})" +
				"(?:\"><strong>)?" +
				"(["+ExtractUtils.SCIENTIFIC_NAME_PATTERN+"]{1,})" +
				"(?:</strong></a>)?");
		
		Pattern p2= Pattern.compile(
				"(?:<a href=\")?" +
				"(/family/[a-zA-Z]{1,})" +
				"(?:\"><strong>)?" +
				"(["+ExtractUtils.SCIENTIFIC_NAME_PATTERN+"]{1,})" +
				"(?:</strong></a>)?");
		
		Pattern p3= Pattern.compile(
				"(?:<em>)?" +
				"(["+ExtractUtils.SCIENTIFIC_NAME_PATTERN+"]{1,})" +
				"(?:</em>["+ExtractUtils.SCIENTIFIC_NAME_PATTERN+"\\(\\)]{1,})?" +
				"(?:<a href=\")?" +
				"(http://www\\.marine\\.csiro\\.au/caabsearch/caab_search\\.caab_report\\?spcode=[0-9]{1,})" +
				"(?:\">[0-9]{1,}</a>)?");

		Writer writer = ExtractUtils.getSiteMapWriter("foa");
		//write column headings
		ExtractUtils.writeColumnHeaders(writer, new String[]{ColumnHeaders.URI, ColumnHeaders.SCIENTIFIC_NAME, ColumnHeaders.FAMILY, ColumnHeaders.ORDER});
		
		Matcher m = p.matcher(orderIndex);
		int searchIdx = 0;
		// get all the order links
		while(m.find(searchIdx)){
			int endIdx = m.end();

			String orderUrl = m.group(1);
			String orderName = m.group(2);
			String generatedOrderUrl = foaSource + orderUrl; 
			//System.out.println("URL" + generatedOrderUrl);
			// move the search start index on to current position
			
			String familyIndex = WebUtils.getUrlContentAsString(generatedOrderUrl);
			
			Matcher m2 = p2.matcher(familyIndex);
			int searchIdx2 = 0;	
			
			//get all the family links
			while(m2.find(searchIdx2)){
				int endIdx2 = m2.end();

				String familyUrl = m2.group(1);
				String familyName = m2.group(2);
				String generatedFamilyUrl = foaSource + familyUrl; 
				//System.out.println("URL" + generatedFamilyUrl);
				// move the search start index on to current position
				
				String speciesIndex = WebUtils.getUrlContentAsString(generatedFamilyUrl);
				
				Matcher m3 = p3.matcher(speciesIndex);
				int searchIdx3 = 0;		
				
				// get all the species links and write them to sitemap
				while(m3.find(searchIdx3)){
					int endIdx3 = m3.end();
					
					String scientificName = m3.group(1);
					String speciesUrl = m3.group(2);
					String generatedSpeciesUrl = speciesUrl; 
					//System.out.println("URL" + generatedSpeciesUrl);
					// move the search start index on to current position
					
					writer.write(generatedSpeciesUrl);
					writer.write('\t');
					writer.write(scientificName);
					writer.write('\t');
					writer.write(familyName);
					writer.write('\t');
					writer.write(orderName);
					writer.write('\n');
					searchIdx3 = endIdx3;
				}
				searchIdx2 = endIdx2;
			}
			searchIdx = endIdx;
		}
		
		writer.flush();
		writer.close();
	}
}

