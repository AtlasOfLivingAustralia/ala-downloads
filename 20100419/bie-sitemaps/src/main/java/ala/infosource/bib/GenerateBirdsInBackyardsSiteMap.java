package ala.infosource.bib;

import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ala.infosource.ColumnHeaders;
import ala.infosource.ExtractUtils;
import ala.infosource.SiteMapGenerator;
import ala.infosource.WebUtils;

/**
 * Birds in Backyards sitemap generator.
 *
 * @author Dave Martin (David.Martin@csiro.au)
 */
@SiteMapGenerator(longName="Birds in Backyards", shortName="bib")
public class GenerateBirdsInBackyardsSiteMap {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		String speciesPageIndex = "http://www.birdsinbackyards.net/finder/birdlist.cfm";
		Writer writer = ExtractUtils.getSiteMapWriter("bib");
		String content = WebUtils.getUrlContentAsString(speciesPageIndex);
		/*
				<strong>Common name:</strong> <a href="/bird/216">Adelie Penguin</a><br>
				<strong>Scientific name:</strong> <em>Pygoscelis adeliae</em><br>
				<strong>Family:</strong> Spheniscidae<br>
		 */
		Pattern pattern = Pattern.compile(
				"(?:<strong>Common name:</strong>[ ]*<a href=\")?" +
				"(/bird/[0-9]{1,})" + //URL
				"(?:\">)?" +
				"(["+ExtractUtils.SCIENTIFIC_NAME_PATTERN_SIMPLE+"]{1,})" + //Common name
				"(?:</a><br>)?"
				+"(?:["+ExtractUtils.WHITESPACE_PATTERN+"]*<strong>Scientific name:</strong>)?"
				+"(?:["+ExtractUtils.WHITESPACE_PATTERN+"]*<em>)?" +
				"(["+ExtractUtils.SCIENTIFIC_NAME_PATTERN_SIMPLE+"]{1,})"+ // Scientific name
				"(?:</em><br>)?"  
				+"(?:["+ExtractUtils.WHITESPACE_PATTERN+"]*<strong>Family:</strong>)?" +
				"(["+ExtractUtils.SCIENTIFIC_NAME_PATTERN_SIMPLE+"]{1,})" + //Family
				"(?:<br>)?"
				);
		ExtractUtils.writeColumnHeaders(writer, new String[]{ColumnHeaders.URI, ColumnHeaders.COMMON_NAME, 
				ColumnHeaders.SCIENTIFIC_NAME, ColumnHeaders.FAMILY});
		
		Matcher m = pattern.matcher(content);
		int startIdx = 0;
		while(m.find(startIdx)){
			if(m.groupCount()>0){
				String url = "http://www.birdsinbackyards.net" + m.group(1);
				String commonName = m.group(2);
				String scientificName = m.group(3);
				String family = m.group(4);
				writer.write(url);
				writer.write('\t');
				writer.write(commonName);
				writer.write('\t');
				writer.write(scientificName);
				writer.write('\t');
				writer.write(family);
				writer.write('\n');
			}
			startIdx=m.end();
		}
		writer.flush();
		writer.close();
	}
}
