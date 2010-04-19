package ala.infosource.abrs;

import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ala.infosource.ColumnHeaders;
import ala.infosource.ExtractUtils;
import ala.infosource.SiteMapGenerator;
import ala.infosource.WebUtils;

/**
 * Site map generator for SpeciesBank - ABRS  
 * 
 * @author Tommy Wang (twang@wollemisystems.com)
 */
@SiteMapGenerator(longName="SpeciesBank - ABRS", shortName="abrs")
public class GenerateAbrsSiteMap implements Runnable {
	
	public static void main(String[] args) throws Exception{

		String kingdomIndexPage = "http://www.environment.gov.au/biodiversity/abrs/online-resources/species-bank/records.html";
		String kingdomSource = "http://www.environment.gov.au/biodiversity/abrs/online-resources/species-bank/";
		String phylumSource = "http://www.environment.gov.au";
		String speciesSource = "http://www.environment.gov.au/cgi-bin/species-bank/";
		String kingdomIndex = WebUtils.getUrlContentAsString(kingdomIndexPage);
		String phylumIndexPages = new String();
		String speciesIndexPages = new String();
		
		Pattern p = Pattern.compile(
				"(?:<i>)?" +
				"([" + ExtractUtils.SCIENTIFIC_NAME_PATTERN + "\\s\\n]{1,})" +
				"(?:[\\s]{0,}</i>[\\s]{0,})?" +
				"(?:\\(<a href=\")?" +
				"(sbank\\-higher\\.pl\\?alt=Y\\&amp;restrict=Y\\&amp;htaxon=[a-zA-Z]{1,})" +
				"(?:\">)?" +
				"([" + ExtractUtils.SCIENTIFIC_NAME_PATTERN + "]{1,})" +
				"(?:</a>\\))?" +
				"(?:[\\s]{0,})?" +
				"(?:<a href=\")?" + 
				"(sbank\\-treatment\\.pl\\?id=[0-9]{1,})" + 
				"(?:\">)?");
		
		Pattern p2 = Pattern.compile(
				"(Phylum: <a href=\")" +
				"(records\\-[a-zA-Z]{1,}\\.html)" +
				"(?:\">)?" +
				"(["+ExtractUtils.SCIENTIFIC_NAME_PATTERN+"]{1,})" +
				"(?:</a>)?");
		
		Pattern p3 = Pattern.compile(
				"(?:<a href=\")?" +
				"(/cgi\\-bin/species\\-bank/sbank\\-s1\\.pl\\?sr=1\\&amp;nr=10\\&amp;prev=\\%25\\&amp;term=[a-zA-Z\\%0-9]{1,}\\&amp;submit=Submit)" +
				"(?:\">)?" +
				"(["+ExtractUtils.SCIENTIFIC_NAME_PATTERN+"]{1,})" +
				"(?:</a>)?");
		
		Matcher m2 = p2.matcher(kingdomIndex);	
		
		int searchIdx2 = 0;
		
		Writer writer = ExtractUtils.getSiteMapWriter("abrs");
		//write column headings
		ExtractUtils.writeColumnHeaders(writer, new String[]{ColumnHeaders.URI, ColumnHeaders.SCIENTIFIC_NAME, ColumnHeaders.FAMILY, ColumnHeaders.PHYLUM});
		
		// Get Phylum links		
		while(m2.find(searchIdx2)){
			int endIdx2 = m2.end();
//			String found = content.substring(startIdx, endIdx);
			String phylumUrl = m2.group(2);
			String phylumName = m2.group(3);
			
			phylumIndexPages = kingdomSource + phylumUrl;
			
			//System.out.println("URL" + phylumIndexPages);
			String phylumIndex = null;
			
			try{
				phylumIndex = WebUtils.getUrlContentAsString(phylumIndexPages);
			} catch (Exception e){
				System.err.println("Error retrieving page: "+phylumIndexPages);
			}
			if(phylumIndex!=null){
			
				//System.out.println(phylumIndex);
				Matcher m3 = p3.matcher(phylumIndex);	
				// move the search start index on to current position
				
				int searchIdx3 = 0;
				
				// Get species page links			
				while(m3.find(searchIdx3)){
					int endIdx3 = m3.end();
	//				String found = content.substring(startIdx, endIdx);
					String speciesUrl = m3.group(1);
					
					speciesIndexPages = phylumSource + speciesUrl;
					
					//System.out.println("URL" + speciesIndexPages);
					String speciesIndex = WebUtils.getUrlContentAsString(speciesIndexPages);	
					
					speciesIndex = speciesIndex.replaceAll("<br/>", "");
					speciesIndex = speciesIndex.replaceAll("\n", "");
					
					//System.out.println(speciesIndex);
					Matcher m = p.matcher(speciesIndex);
					
					int searchIdx4 = 0;
					
					// write site map				
					while(m.find(searchIdx4)){
						int endIdx4 = m.end();
	//					String found = content.substring(startIdx, endIdx);
						String scientificName = m.group(1);
						scientificName = scientificName.replaceAll("  ", "");
						scientificName = scientificName.replaceAll("\n", "");
						scientificName = scientificName.replaceAll("\t", "");
						scientificName = scientificName.replaceAll("\f", "");
						scientificName = scientificName.replaceAll("\r", "");
						String url = m.group(4);
						String familyName = m.group(3);
						String generatedUrl = speciesSource + url; 
						writer.write(generatedUrl);
						writer.write('\t');
						writer.write(scientificName);
						writer.write('\t');
						writer.write(familyName);
						writer.write('\t');
						writer.write(phylumName);
						writer.write('\n');
						writer.flush();
						System.out.println("URL:" + generatedUrl);
						System.out.println("NAME:" + scientificName);
					
						searchIdx4 = endIdx4;
					}
					searchIdx3 = endIdx3;
				}
				searchIdx2 = endIdx2;
			}
		}

		writer.flush();
		writer.close();
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
