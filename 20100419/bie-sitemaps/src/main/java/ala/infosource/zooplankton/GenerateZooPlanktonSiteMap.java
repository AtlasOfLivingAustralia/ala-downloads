package ala.infosource.zooplankton;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ala.infosource.ColumnHeaders;
import ala.infosource.ExtractUtils;
import ala.infosource.SiteMapGenerator;
import ala.infosource.WebUtils;

/**
 * Generate Zoo Plankton site map. 
 *
 * @author Dave Martin (David.Martin@csiro.au)
 */
@SiteMapGenerator(longName="Zoo Plankton", shortName="zooplankton")
public class GenerateZooPlanktonSiteMap {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		String rootUrl = "http://www.tafi.org.au/zooplankton/imagekey/list.html";
		
		String content = WebUtils.getUrlContentAsString(rootUrl);
		Writer writer = ExtractUtils.getSiteMapWriter("zooplankton");
		//write column headings
		ExtractUtils.writeColumnHeaders(writer, new String[]{ColumnHeaders.URI, ColumnHeaders.SCIENTIFIC_NAME});
		
		Pattern p1 = getPattern("(?:<a href=\")([a-z]{1,}/index.html)");
		Pattern p2 = getPattern("(?:<a href=\")([a-z]{1,}/[a-z]{1,}/index.html)");
		Pattern p3 = getPattern("(?:<a href=\")([a-z]{1,}/[a-z]{1,}/[a-z]{1,}/index.html)");
		Pattern p4 = getPattern("(?:<a href=\")([a-z]{1,}/[a-z]{1,}/[a-z]{1,}/[a-z_]{1,}.html)");
//		Pattern[] ps = new Pattern[]{p1};
		Pattern[] ps = new Pattern[]{p1,p2,p3,p4};
		
		List<String> ignoreUrls = new ArrayList<String>();
		ignoreUrls.add("diagnostickey/index.html");
		ignoreUrls.add("about/index.html");
		ignoreUrls.add("about/index.html");
		ignoreUrls.add("references/index.html");
		ignoreUrls.add("projectinfo/index.html");
		ignoreUrls.add("contact/index.html");
		ignoreUrls.add("links/index.html");
		ignoreUrls.add("other/index.html");
		
		for(Pattern p: ps){
		
			Matcher m = p.matcher(content);
			int searchIdx = 0;
	
			//find all images
			while(m.find(searchIdx)){
				int endIdx = m.end();
				if(m.groupCount()==2){
					if(!ignoreUrls.contains(m.group(1))){
						writer.write("http://www.tafi.org.au/zooplankton/imagekey/"+m.group(1));
						writer.write("\t");
						writer.write(m.group(2));
						writer.write("\n");
					}
				}
				// move the search start index on to current position
				searchIdx = endIdx;
			}
		}
		writer.flush();
		writer.close();
		// copepoda/index.html
	}

	public static Pattern getPattern(String urlPattern){
		return Pattern.compile(
				"(?:<a href=\")?" +
				urlPattern+
				"(?:\">)?" +
				"(?:<em>)?" +
				"([A-Za-z\\- ]{1,})" +
				"(?:</em>)?" +
				"(?:</a>)?");
	}
}
