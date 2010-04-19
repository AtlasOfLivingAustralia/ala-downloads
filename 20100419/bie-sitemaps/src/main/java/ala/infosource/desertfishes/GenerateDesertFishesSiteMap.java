/**
 * 
 */
package ala.infosource.desertfishes;

import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ala.infosource.ColumnHeaders;
import ala.infosource.ExtractUtils;
import ala.infosource.SiteMapGenerator;
import ala.infosource.WebUtils;

/**
 * Site map generator for Australian Desert Fishes.
 * 
 * @author Dave Martin (David.Martin@csiro.au)
 */
@SiteMapGenerator(longName="Australian Desert Fishes", shortName="desertfishes")
public class GenerateDesertFishesSiteMap {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String rootUrl = "http://www.desertfishes.org/australia/fish/index.html";
		Writer writer = ExtractUtils.getSiteMapWriter("desertfishes");
		ExtractUtils.writeColumnHeaders(writer, new String[]{ColumnHeaders.URI, ColumnHeaders.SCIENTIFIC_NAME, ColumnHeaders.COMMON_NAME});
		
		String content = WebUtils.getUrlContentAsString(rootUrl);
		Pattern p = Pattern.compile(
				"(?:HREF=\")?"
				+"([A-Za-z\\-]{1,}\\.[s]{0,}html)" //URL
				+"(?:\"><i>)?"
				+"([A-Za-z'\\.\\- ]{1,})" //Scientific name
				+"(?:</i>)?"
				+"([A-Za-z'\\.\\- ]{1,})" //Common name
				+"(?:</a>)?"
		);
		Matcher m = p.matcher(content);
		int startIdx = 0;
		while(m.find(startIdx)){
			if(m.groupCount()==3){
				String url = "http://www.desertfishes.org/australia/fish/"+m.group(1);
				String scientificName = m.group(2);
				String commonName = m.group(3);
				writer.write(url);
				writer.write('\t');
				writer.write(scientificName);
				writer.write('\t');
				writer.write(commonName);
				writer.write('\n');
			}
			startIdx=m.end();
		}
		writer.flush();
		writer.close();
	}
}
