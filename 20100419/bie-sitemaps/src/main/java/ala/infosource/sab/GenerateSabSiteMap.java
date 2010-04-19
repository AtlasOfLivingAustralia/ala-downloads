package ala.infosource.sab;

import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ala.infosource.ColumnHeaders;
import ala.infosource.ExtractUtils;
import ala.infosource.SiteMapGenerator;
import ala.infosource.WebUtils;

/**
 * Site map generator for South Australian Biodiversity - Threatened Fauna  
 * 
 * @author Tommy Wang (twang@wollemisystems.com)
 */
@SiteMapGenerator(longName="South Australian Biodiversity - Threatened Fauna ", shortName="sab")
public class GenerateSabSiteMap {

	public static void main(String[] args) throws Exception{

		String[] speciesIndexPages = {"http://www.environment.sa.gov.au/biodiversity/threatened-species/threatened-flora.html",
										"http://www.environment.sa.gov.au/biodiversity/threatened-species/threatened-fauna.html"};
		String source = "http://www.environment.sa.gov.au/biodiversity";
		String content = new String();
		
		for (String speciesIndexPage : speciesIndexPages) {
			content += WebUtils.getUrlContentAsString(speciesIndexPage);
		}

		Pattern p = Pattern.compile(
				"(?:<A href=\"\\.\\.)?" +
				"([a-zA-Z_/]{1,}\\.pdf)" +
				"(?:\" target=\"_blank\">)" +
				"(["+ExtractUtils.SCIENTIFIC_NAME_PATTERN+"\\(\\)_\\s]{1,})" +
				"(?:<span class=\"pdf\">)?");

		Writer writer = ExtractUtils.getSiteMapWriter("sab");
		//write column headings
		ExtractUtils.writeColumnHeaders(writer, new String[]{ColumnHeaders.URI, ColumnHeaders.SCIENTIFIC_NAME});

		//System.out.println(content);

		//System.out.println(content);
		Matcher m = p.matcher(content);
		int searchIdx = 0;

		//find all images
		while(m.find(searchIdx)){
			int endIdx = m.end();
			//				String found = content.substring(startIdx, endIdx);
			String url = m.group(1);
			System.out.println("URL" + source + url);
			String scientificName = m.group(2);
			scientificName = scientificName.replaceAll("[\\s]{1,}", " ");
			String generatedUrl = source + url.replaceAll("/biodiversity", ""); 
			writer.write(generatedUrl);
			writer.write('\t');
			writer.write(scientificName.replaceAll("[ ]{2,}", " "));
			writer.write('\n');
			// move the search start index on to current position
			searchIdx = endIdx;
		}
		
		writer.flush();
		writer.close();
	}

}

