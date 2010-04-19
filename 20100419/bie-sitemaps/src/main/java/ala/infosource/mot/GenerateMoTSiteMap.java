/**
 * 
 */
package ala.infosource.mot;

import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ala.infosource.ColumnHeaders;
import ala.infosource.ExtractUtils;
import ala.infosource.SiteMapGenerator;
import ala.infosource.WebUtils;

/**
 * Site map generator for http://www.molluscsoftasmania.net
 *
 * @author Dave Martin (David.Martin@csiro.au)
 */
@SiteMapGenerator(longName="Molluscs of Tasmania", shortName="mot")
public class GenerateMoTSiteMap {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		String url = "http://www.molluscsoftasmania.net/Index%20pages/epithetListAZa.html";
		
		String content = WebUtils.getUrlContentAsString(url);
		
		Pattern p = Pattern.compile("(epithetListAZ[a-z].html)");
		
		Matcher m = p.matcher(content);
		
		Set<String> urls = new LinkedHashSet<String>();
		
		int searchIdx = 0;

		//find all images
		while(m.find(searchIdx)){
			int endIdx = m.end();
			if(m.groupCount()==1){
				String idxUrl = "http://www.molluscsoftasmania.net/Index%20pages/"+m.group(1);
				urls.add(idxUrl);
			}
			// move the search start index on to current position
			searchIdx = endIdx;
		}

		p = Pattern.compile(
//				"(?:<p>- <I>)?" +
//				"(["+ExtractUtils.SCIENTIFIC_NAME_PATTERN+"]{1,})" + //synonym
//				"(?:</I> )?" +
//				"(["+ExtractUtils.SCIENTIFIC_NAME_PATTERN+"]{1,})" + //author
				"(?:<a href = \"\\.\\.)?" +
				"(Species pages/["+ExtractUtils.SCIENTIFIC_NAME_PATTERN+"]{1,}\\.html)" + //URL
				"(?:\"><I>)?" +
				"(["+ExtractUtils.SCIENTIFIC_NAME_PATTERN+"]{1,})" + //scientific name
				"(?:</I>)?" 
//				"(["+ExtractUtils.SCIENTIFIC_NAME_PATTERN+"]{1,})" + //family name
//				"(?: &#8211; )?" +
//				"(?:["+ExtractUtils.SCIENTIFIC_NAME_PATTERN+"]*)?" + //synonym family name
//				"(?:\\)</p>)?"
				);
		
		Writer writer = ExtractUtils.getSiteMapWriter("mot");
		ExtractUtils.writeColumnHeaders(writer, new String[]{ColumnHeaders.URI, ColumnHeaders.SCIENTIFIC_NAME
				/*, ColumnHeaders.AUTHOR*/});
		
		for(String idxUrl: urls){
			String pageContent = WebUtils.getUrlContentAsString(idxUrl);
			m = p.matcher(pageContent);
			searchIdx = 0;
			
			//find all images
			while(m.find(searchIdx)){
				int endIdx = m.end();
				if(m.groupCount()==2){
//					if(!ignoreUrls.contains(m.group(1))){
						writer.write("http://www.molluscsoftasmania.net/"+m.group(1));
						writer.write("\t");
						writer.write(m.group(2));
//						writer.write("\t");
//						writer.write(m.group(1));
//						writer.write("\t");
//						writer.write(m.group(5));
						writer.write("\n");
//					}
				}
				// move the search start index on to current position
				searchIdx = endIdx;
			}
		}
		writer.flush();
		writer.close();
	}
}
