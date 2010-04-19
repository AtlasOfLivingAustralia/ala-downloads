/**
 * 
 */
package ala.infosource.seaslug;

import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ala.infosource.ColumnHeaders;
import ala.infosource.ExtractUtils;
import ala.infosource.SiteMapGenerator;
import ala.infosource.WebUtils;

/**
 * Sea slug forum site map generator.
 * 
 * @author Dave Martin (David.Martin@csiro.au)
 */
@SiteMapGenerator(longName="Sea Slug Forum", shortName="seaslug")
public class GenerateSeaSlugSiteMap {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{

		String speciesIndexPage = "http://www.seaslugforum.net/specieslist.cfm";
		String content = WebUtils.getUrlContentAsString(speciesIndexPage);
		
		//setup writer
		Writer writer = ExtractUtils.getSiteMapWriter("seaslug");
		ExtractUtils.writeColumnHeaders(writer, new String[]{ColumnHeaders.URI, ColumnHeaders.SCIENTIFIC_NAME});
		
		//<dd><em><a href="/acteeloi.htm">Acteon eloiseae</a></em></dd>
		Pattern p1 = Pattern.compile("(?:<dd><em><a href=\")?"
				+"(" +
				"/[a-z0-9]{1,}\\.htm"
				+")"
				+"(?:\">)?"
				+"(["+ExtractUtils.SCIENTIFIC_NAME_PATTERN+"]{1,})"
				+"(?:</a></em>.*</dd>)?");
		
		Pattern p2 = Pattern.compile("(?:<dd><em><a href=\")?"
				+"(" +
					"/find\\.cfm\\?id=["+ExtractUtils.SCIENTIFIC_NAME_PATTERN+"]{1,}"
				+")"
				+"(?:\">)?"
				+"(["+ExtractUtils.SCIENTIFIC_NAME_PATTERN+"]{1,})"
				+"(?:</a></em>.*</dd>)?");
		
		Pattern[] ps = new Pattern[]{p1,p2};
		
		for(Pattern p: ps){
			Matcher m = p.matcher(content);
			int startIdx = 0;
			while(m.find(startIdx)){
				if(m.groupCount()==2){
					String url = "http://www.seaslugforum.net"+m.group(1);
					String scientificName = m.group(2);
					writer.write(url);
					writer.write('\t');
					if(scientificName.length()>3){
						writer.write(scientificName);
					} else {
						writer.write("");
					}
					writer.write('\n');
				}
				startIdx=m.end();
			}
		}
		writer.flush();
		writer.close();
	}
}