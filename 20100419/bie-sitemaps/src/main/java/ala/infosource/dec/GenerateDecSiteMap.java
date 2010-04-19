package ala.infosource.dec;

import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ala.infosource.ColumnHeaders;
import ala.infosource.ExtractUtils;
import ala.infosource.SiteMapGenerator;
import ala.infosource.WebUtils;

/**
 * Site map generator for DEC - NSW threatened species   
 * 
 * @author Tommy Wang (twang@wollemisystems.com)
 */
@SiteMapGenerator(longName="DEC", shortName="dec")
public class GenerateDecSiteMap {

	public static void main(String[] args) throws Exception{

		final String speciesIndexPage = "http://www.threatenedspecies.environment.nsw.gov.au/tsprofile/browse_scientificname.aspx";
		final String source = "http://www.threatenedspecies.environment.nsw.gov.au/tsprofile/";
		String content = WebUtils.getUrlContentAsString(speciesIndexPage);

		Pattern p = Pattern.compile(
				"(?:<tr><td)" +
				"(?: class=\"tableDataHi\")?" +
				"(?:><a href=\")" +
				"(profile\\.aspx\\?id=[0-9]{1,})" +
				"(?:\" class=\"linkBlack80\">)" +
				"(["+ExtractUtils.SCIENTIFIC_NAME_PATTERN+"\\(\\)\\s]{1,})" +
				"(?:</a>)");

		Writer writer = ExtractUtils.getSiteMapWriter("dec");
		//write column headings
		ExtractUtils.writeColumnHeaders(writer, new String[]{ColumnHeaders.URI, ColumnHeaders.SCIENTIFIC_NAME});

		Matcher m = p.matcher(content);

		int searchIdx = 0;

		while(m.find(searchIdx)){
			int endIdx = m.end();
			String url = m.group(1);
			String scientificName = m.group(2);
			scientificName = scientificName.replaceAll("[\\s&&[^ ]]", "");
			String generatedUrl = source + url;
			System.out.println(generatedUrl);
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

