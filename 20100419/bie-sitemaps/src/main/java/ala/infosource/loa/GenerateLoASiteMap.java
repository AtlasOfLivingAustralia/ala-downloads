/**
 * 
 */
package ala.infosource.loa;

import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ala.infosource.ColumnHeaders;
import ala.infosource.ExtractUtils;
import ala.infosource.SiteMapGenerator;
import ala.infosource.WebUtils;

/**
 * Site map generator for Ladybirds of Australia
 * 
 * @author Dave Martin (David.Martin@csiro.au)
 */
@SiteMapGenerator(longName="Ladybirds of Australia", shortName="loa")
public class GenerateLoASiteMap {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		String[] familyAlbums = new String[]{
				"http://www.ento.csiro.au/biology/ladybirds/speciesIndex.php?pageNo=1",
				"http://www.ento.csiro.au/biology/ladybirds/speciesIndex.php?pageNo=2",
				"http://www.ento.csiro.au/biology/ladybirds/speciesIndex.php?pageNo=3"
		};

		Writer writer = ExtractUtils.getSiteMapWriter("loa");
		//write column headings
		ExtractUtils.writeColumnHeaders(writer, new String[]{ColumnHeaders.URI, ColumnHeaders.SCIENTIFIC_NAME, ColumnHeaders.GENUS, ColumnHeaders.SPECIFIC_EPITHET});

		//family url patter
		Pattern urlPattern = Pattern.compile(
				"(lucid/key/lucidKey/Media/Html/[a-zA-Z0-9]{1,}.htm)" +
				"(?:'>)?" +
				"(["+ExtractUtils.SCIENTIFIC_NAME_PATTERN+"]{1,})" +
				"(?:[\t\r\n ]*\\()?"+
				"(["+ExtractUtils.SCIENTIFIC_NAME_PATTERN+"]{1,})"+
				"(?:\\))?");

		//extract all family page URLs
		for(int i=0; i<familyAlbums.length; i++){
			String content = WebUtils.getUrlContentAsString(familyAlbums[i]);
			
			Matcher m = urlPattern.matcher(content);
			int start = 0;
			while(m.find(start)){
				
				String url = "http://www.ento.csiro.au/biology/ladybirds/" +m.group(1);
				String genus = m.group(2);
				String specificEphitet = m.group(3);
				
				writer.write(url);
				writer.write('\t');
				writer.write(genus+" "+specificEphitet);
				writer.write('\t');
				writer.write(genus);
				writer.write('\t');
				writer.write(specificEphitet);
				writer.write('\n');
				
				start = m.end();
			}
		}
		writer.flush();
		writer.close();
	}
}