package ala.infosource.ausmoths;

import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ala.infosource.ColumnHeaders;
import ala.infosource.ExtractUtils;
import ala.infosource.SiteMapGenerator;
import ala.infosource.WebUtils;

/**
 * Site map generator for Australian Moths Online  
 * 
 * This is for the Australian Moths Online site at
 * www1.ala.org.au
 * 
 * @author Tommy Wang (twang@wollemisystems.com)
 */
@SiteMapGenerator(longName="Australian Moths Online", shortName="ausmothsonline")
public class GenerateAusmothsSiteMap {

	public static void main(String[] args) throws Exception{

		String familyIndexPage = "http://www1.ala.org.au/gallery2/main.php";
		String source = "http://www1.ala.org.au/gallery2/";
		String source2 = "http://www1.ala.org.au";

		Pattern p = Pattern.compile(
				"(?:<a href=\")" +
				"(v/["+ExtractUtils.SCIENTIFIC_NAME_PATTERN+"]{1,}/)" +
				"(?:\">[\\s]{0,}<img src=\"d/[0-9\\-]{1,}/[a-zA-Z]{1,}\\.jpg\" width=\"[0-9]{1,}\" height=\"[0-9]{1,}\" id=\"[a-zA-Z0-9]{1,}\" class=\"[a-zA-Z_ ]{1,}\" alt=\")" + 
				"([\\w]{1,})" + 
				"(?:\"/>)");

		Pattern p2 = Pattern.compile(
				"(?:<a href=\")" +
				"(/gallery2/v/["+ExtractUtils.SCIENTIFIC_NAME_PATTERN+"/]{1,})" +
				"(?:\">[\\s]{0,}<img src=\"/gallery2/d/[0-9\\-]{1,}/[a-zA-Z]{1,}\\.jpg\" width=\"[0-9]{1,}\" height=\"[0-9]{1,}\" id=\"[a-zA-Z0-9]{1,}\" class=\"[a-zA-Z_ ]{1,}\" alt=\")" + 
				"([\\w ]{1,})" + 
				"(?:\"/>)");

		Pattern p3 = Pattern.compile(
				"(?:<a href=\")" +
				"(/gallery2/v/[\\w]{1,}/[\\w]{1,}/[\\w]{1,}.jpg.html)" +
				"(?:\">)");

		Writer writer = ExtractUtils.getSiteMapWriter("ausmothsonline");
		//write column headings
		ExtractUtils.writeColumnHeaders(writer, new String[]{ColumnHeaders.URI, ColumnHeaders.SCIENTIFIC_NAME, ColumnHeaders.FAMILY});

		//System.out.println(content);

		String familyIndex = WebUtils.getUrlContentAsString(familyIndexPage);

		Matcher m = p.matcher(familyIndex);
		int searchIdx = 0;

		//find all links
		while(m.find(searchIdx)){
			int endIdx = m.end();
			String genusIndexPage = m.group(1);
			String familyName = m.group(2);

			genusIndexPage = source + genusIndexPage; 

			String genusIndex = WebUtils.getUrlContentAsString(genusIndexPage);
			//System.out.println("GENUS!!" + genusIndexPage);
			//System.out.println(familyName);
			Matcher m2 = p2.matcher(genusIndex);
			int searchIdx2 = 0;

			while(m2.find(searchIdx2)){
				int endIdx2 = m2.end();
				String speciesIndexPage = m2.group(1);
				String scientificName = m2.group(2);

				speciesIndexPage = source2 + speciesIndexPage; 
				//System.out.println("SPECIES!!" + speciesIndexPage);

				String speciesIndex = WebUtils.getUrlContentAsString(speciesIndexPage);
				Matcher m3 = p3.matcher(speciesIndex);
				int searchIdx3 = 0;
				while(m3.find(searchIdx3)){
					int endIdx3 = m3.end();
					String url = m3.group(1);
					
					url = source2 + url; 
					System.out.println(url);

					writer.write(url);
					writer.write('\t');
					writer.write(scientificName);
					writer.write('\t');
					writer.write(familyName);
					writer.write('\n');

					searchIdx3 = endIdx3;
				}
				searchIdx2 = endIdx2;

			}


			// move the search start index on to current position
			searchIdx = endIdx;
		}

		writer.flush();
		writer.close();

	}
}

