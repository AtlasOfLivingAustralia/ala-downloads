package ala.infosource.ntts;

import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ala.infosource.ColumnHeaders;
import ala.infosource.ExtractUtils;
import ala.infosource.SiteMapGenerator;
import ala.infosource.WebUtils;

/**
 * Site map generator for Northern Territory Threatened Species List 
 * 
 * @author Tommy Wang (twang@wollemisystems.com)
 */
@SiteMapGenerator(longName="Northern Territory Threatened Species List", shortName="ntts")
public class GenerateNttsSiteMap {

	public static void main(String[] args) throws Exception{

		String speciesIndexPage = "http://www.nt.gov.au/nreta/wildlife/animals/threatened/specieslist.html";
		String source = "http://www.nt.gov.au/nreta/wildlife/animals/threatened/";
		String content = WebUtils.getUrlContentAsString(speciesIndexPage);

		Pattern p = Pattern.compile(
				"(?:<A href=\")?" +
				"(pdf/[a-zA-Z]{1,}/[a-zA-Z_]{1,}\\.pdf)" +
				"(?:\" target=\"_blank\">)" +
				"(["+ExtractUtils.SCIENTIFIC_NAME_PATTERN+"_]{1,})" +
				"(?:</A>)");

		Writer writer = ExtractUtils.getSiteMapWriter("ntts");
		//write column headings
		ExtractUtils.writeColumnHeaders(writer, new String[]{ColumnHeaders.URI, ColumnHeaders.SCIENTIFIC_NAME});

		//System.out.println(content);

		content = content.replaceAll("<EM>", "");
		content = content.replaceAll("</EM>", "");
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
			String generatedUrl = source + url; 
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

