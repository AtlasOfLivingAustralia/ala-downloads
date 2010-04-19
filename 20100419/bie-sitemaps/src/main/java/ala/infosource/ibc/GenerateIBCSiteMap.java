package ala.infosource.ibc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ala.infosource.ColumnHeaders;
import ala.infosource.ExtractUtils;
import ala.infosource.SiteMapGenerator;
import ala.infosource.WebUtils;

/**
 * Site map generator for Internet Bird Collection
 * 
 * @author Dave Martin (David.Martin@csiro.au)
 */
@SiteMapGenerator(longName="Internet Bird Collection", shortName="ibc")
public class GenerateIBCSiteMap {

	public static void main(String[] args) throws Exception{
		
		// get index URL
		String familiesListPath = System.getProperty("user.dir")+"/data/ibc/families.txt";
		Writer writer = ExtractUtils.getSiteMapWriter("ibc");
		//write column headings
		ExtractUtils.writeColumnHeaders(writer, new String[]{ColumnHeaders.URI,ColumnHeaders.SCIENTIFIC_NAME,ColumnHeaders.COMMON_NAME});
		BufferedReader reader = new BufferedReader(new FileReader(familiesListPath));
		String url = "";
		//get urls
		while((url=reader.readLine())!=null){
			String content = WebUtils.getUrlContentAsString(url);
			// file:///species/greater-rhea-rhea-americana
			Pattern p = Pattern.compile("" +
					"(species/["+ExtractUtils.SCIENTIFIC_NAME_PATTERN+"]{1,})" +
					"(?:\">)?" +
					"(?:[\r\n\t ]*)?" +
					"(?:<span class=\"english\">)?" +
					"(["+ExtractUtils.SCIENTIFIC_NAME_PATTERN+"]{1,})" +
					"(?:</span>)?" +
					"(?:[\r\n\t ]*)?" +
					"(?:\\(<span class=\"scientific\">)?" +
					"(["+ExtractUtils.SCIENTIFIC_NAME_PATTERN+"]{1,})" +
					"(?:</span>\\))?"
			);
			Matcher m = p.matcher(content);
			int searchIdx = 0;

			//find all images
			while(m.find(searchIdx)){
				int endIdx = m.end();
				if(m.groupCount()==3){
	//				String found = content.substring(startIdx, endIdx);
					String generatedUrl = "http://ibc.lynxeds.com/"+m.group(1);
					String scientificName = m.group(2);
					String commonName = m.group(3);
					writer.write(generatedUrl);
					writer.write('\t');
					writer.write(commonName.replaceAll("&#039;", "'"));
					writer.write('\t');
					writer.write(scientificName.replaceAll("&#039;", "'"));
					writer.write('\n');
				}
				searchIdx = endIdx;
			}
		}
		writer.flush();
		writer.close();
		reader.close();
	}
}