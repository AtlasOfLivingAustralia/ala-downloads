package ala.infosource.epbc;

import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ala.infosource.ColumnHeaders;
import ala.infosource.ExtractUtils;
import ala.infosource.SiteMapGenerator;
import ala.infosource.WebUtils;

/**
 * Site map generator for EPBC Act List of Threatened  
 * 
 * @author Tommy Wang (twang@wollemisystems.com)
 */
@SiteMapGenerator(longName="EPBC Act List of Threatened", shortName="epbc")
public class GenerateEpbcSiteMap {

	public static void main(String[] args) throws Exception{

		String[] speciesIndexPages = {"http://www.environment.gov.au/cgi-bin/sprat/public/publicthreatenedlist.pl?wanted=fauna",
									"http://www.environment.gov.au/cgi-bin/sprat/public/publicthreatenedlist.pl?wanted=flora"};
		String source = "http://www.environment.gov.au/";


		Pattern p = Pattern.compile(
				"(?:<a href=\")?" +
				"(cgi\\-bin/sprat/public/publicspecies\\.pl\\?taxon_id=[0-9]{1,})" +
				"(?:\"><i>)?" +
				"(["+ExtractUtils.SCIENTIFIC_NAME_PATTERN+"]{1,})" +
				"(?:</i></a>)?");

		Writer writer = ExtractUtils.getSiteMapWriter("epbc");
		//write column headings
		ExtractUtils.writeColumnHeaders(writer, new String[]{ColumnHeaders.URI, ColumnHeaders.SCIENTIFIC_NAME});

		//System.out.println(content);
		for (String speciesIndexPage : speciesIndexPages) {		
			String content = WebUtils.getUrlContentAsString(speciesIndexPage);
			
			Matcher m = p.matcher(content);
			int searchIdx = 0;

			//find all links
			while(m.find(searchIdx)){
				int endIdx = m.end();
				
				String url = m.group(1);
				String scientificName = m.group(2);
				String generatedUrl = source + url; 
				System.out.println(generatedUrl);
				writer.write(generatedUrl);
				writer.write('\t');
				writer.write(scientificName);
				writer.write('\n');
				// move the search start index on to current position
				searchIdx = endIdx;
			}
		}
		writer.flush();
		writer.close();
	}

}

