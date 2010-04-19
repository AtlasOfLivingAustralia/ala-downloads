package ala.infosource.wta;

import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ala.infosource.ColumnHeaders;
import ala.infosource.ExtractUtils;
import ala.infosource.SiteMapGenerator;
import ala.infosource.WebUtils;

/**
 * Site map generator for World Thysanoptera, Australasia 
 * 
 * @author Tommy Wang (twang@wollemisystems.com)
 */
@SiteMapGenerator(longName="World Thysanoptera", shortName="wta")
public class GenerateWtaSiteMap {

	public static void main(String[] args) throws Exception{

		String speciesIndexPage = "http://anic.ento.csiro.au/thrips/identifying_thrips/thrips_a-z.html#australia-merothripidae-species";
		String source = "http://anic.ento.csiro.au/thrips/identifying_thrips/";
		String content = WebUtils.getUrlContentAsString(speciesIndexPage);

		Pattern p = Pattern.compile(
				"(?:<a href=\")?" +
				//"(/cgi\\-bin/euctax\\.pl\\?/PlantNet/Euc=\\&name=[a-zA-Z](1,))" +
				"([a-zA-Z_\\.]{1,}\\.htm)" +
				"(?:\" target=\"_parent\"><em>)?" +
				"(["+ExtractUtils.SCIENTIFIC_NAME_PATTERN+"_]{1,})" +
		"(?:</em></a>)?");

		Writer writer = ExtractUtils.getSiteMapWriter("wta");
		//write column headings
		ExtractUtils.writeColumnHeaders(writer, new String[]{ColumnHeaders.URI, ColumnHeaders.SCIENTIFIC_NAME});

		//System.out.println(content);

		content = content.split("Australian Merothripidae Species")[1];
		content = content.split("<div id=\"small\">")[0];
		//System.out.println(content);
		Matcher m = p.matcher(content);
		int searchIdx = 0;

		//find all images
		while(m.find(searchIdx)){
			int endIdx = m.end();
			//				String found = content.substring(startIdx, endIdx);
			String url = m.group(1);
			//System.out.println("URL" + source + url);
			String scientificName = m.group(2);
			String generatedUrl = source + url; 
			writer.write(generatedUrl);
			writer.write('\t');
			writer.write(scientificName);
			writer.write('\n');
			// move the search start index on to current position
			searchIdx = endIdx;
		}
		
		writer.flush();
		writer.close();
	}

}

