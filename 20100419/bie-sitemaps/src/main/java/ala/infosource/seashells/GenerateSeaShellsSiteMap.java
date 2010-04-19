/**
 * 
 */
package ala.infosource.seashells;

import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ala.infosource.ColumnHeaders;
import ala.infosource.ExtractUtils;
import ala.infosource.SiteMapGenerator;
import ala.infosource.WebUtils;

/**
 * Seashells of NSW site map generator.
 * 
 * @author Dave Martin (David.Martin@csiro.au)
 */
@SiteMapGenerator(longName="Seaeshells of New South Wales", shortName="seashellsnsw")
public class GenerateSeaShellsSiteMap {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		String indexPage = "http://seashellsofnsw.org.au/General/Pages/Book_index.htm";
		String content = WebUtils.getUrlContentAsString(indexPage);

		Writer writer = ExtractUtils.getSiteMapWriter("seashellsnsw");
		ExtractUtils.writeColumnHeaders(writer, new String[]{ColumnHeaders.URI, ColumnHeaders.SCIENTIFIC_NAME});
		
		//<a href="../../Conidae/Pages/Conus_acutangulus.htm">acutangulus, Conus</a>
		Pattern p = Pattern.compile(
				"(<a href=\"\\.\\./\\.\\.)" +
				"(/[A-Za-z_]*/Pages/[A-Za-z_%20]*.htm)"+
				"(\">)"+
				"([A-Za-z\\., \r\t\n]*)"+
				"(</a>)"
				);
		
		//match scientific names of the form annulus, Cypraea
		Pattern sciNamePattern = Pattern.compile(
				"([a-z]{1,})" +
				"(?:,[ ]{0,})?" +
				"([A-Z]{1}[a-z]{1,})");
		
		//search for matches
		Matcher m = p.matcher(content);
		int startIdx = 0;
		while(m.find(startIdx)){
			if(m.groupCount()==5){
				String url = "http://seashellsofnsw.org.au"+m.group(2);
				//clean up the scientific name
				String scientificName = m.group(4).replaceFirst("\r|\n", "").trim();
				scientificName = scientificName.replaceAll("[ ]{2,}", " ");
				Matcher sciNameMatcher = sciNamePattern.matcher(scientificName);
				if(sciNameMatcher.find()){
					String specificEpithet = sciNameMatcher.group(1);
					String genus = sciNameMatcher.group(2);
					scientificName = genus + " " + specificEpithet;
				}
				
				writer.write(url);
				writer.write('\t');
				writer.write(scientificName);
				writer.write('\n');
			}
			startIdx=m.end();
		}
		writer.flush();
		writer.close();
	}
}
