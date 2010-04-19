package ala.infosource.prosea;

import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ala.infosource.ColumnHeaders;
import ala.infosource.ExtractUtils;
import ala.infosource.SiteMapGenerator;
import ala.infosource.WebUtils;

/**
 * Site map generator for PROSEA (Plant Resources of SE Asia)  
 * 
 * @author Tommy Wang (twang@wollemisystems.com)
 */
@SiteMapGenerator(longName="PROSEA", shortName="prosea")
public class GenerateProseaSiteMap {

	public static void main(String[] args) throws Exception{

		final String speciesIndexPageTemplate = "http://proseanet.org/prosea/e-prosea_detail.php?frt=&id=";
		final int pageCount = 3160;
		//String content = WebUtils.getUrlContentAsString(speciesIndexPage);

		Pattern p = Pattern.compile(
				"(?:<span class=\"style36\">)" +
				//"(/cgi\\-bin/euctax\\.pl\\?/PlantNet/Euc=\\&name=[a-zA-Z](1,))" +
				"(["+ExtractUtils.SCIENTIFIC_NAME_PATTERN+"\\(\\)ñ]{1,})" +
				"(?:</span>)");

		Writer writer = ExtractUtils.getSiteMapWriter("prosea");
		//write column headings
		ExtractUtils.writeColumnHeaders(writer, new String[]{ColumnHeaders.URI, ColumnHeaders.SCIENTIFIC_NAME});
		
		for (int i = 1; i <= pageCount; i++) {
			
			String generatedUrl = speciesIndexPageTemplate + i;
			String content = WebUtils.getUrlContentAsString(generatedUrl);
			Matcher m = p.matcher(content);
			
			int searchIdx = 0;
			
			while(m.find(searchIdx)){
				int endIdx = m.end();

				String scientificName = m.group(1);
				System.out.println(scientificName);
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