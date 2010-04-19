package ala.infosource.nimpis;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import ala.infosource.ColumnHeaders;
import ala.infosource.ExtractUtils;
import ala.infosource.SiteMapGenerator;

/**
 * Site map generator for NIMPIS
 * 
 * @author Dave Martin (David.Martin@csiro.au)
 */
@SiteMapGenerator(longName="National Introduced Marine Pest Information System", shortName="nimpis")
public class GenerateNimpisSiteMap {
	
	public static void main(String[] args) throws Exception{

		//  <a href="index.cfm?fa=main.spDetailsDB&sp=6000006243">Carcinus maenas</a>
		Pattern p = Pattern.compile(
				"(?:<a href=\")?" +
				"(index.cfm\\?fa=main\\.spDetailsDB&sp=[0-9]{1,})" +
				"(?:\">)?" +
				"(["+ExtractUtils.SCIENTIFIC_NAME_PATTERN+"]{1,})" +
				"(?:</a>)?");
		
		File directory = new File(System.getProperty("user.dir")+"/data/nimpis");
		String[] fileNames = directory.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".html");
			}
		});
		
		Writer writer = ExtractUtils.getSiteMapWriter("nimpis");
		//write column headings
		ExtractUtils.writeColumnHeaders(writer, new String[]{ColumnHeaders.URI, ColumnHeaders.SCIENTIFIC_NAME});
		
		for(int i=0; i<fileNames.length; i++){
			
			String content = FileUtils.readFileToString(new File(directory.getAbsolutePath()+File.separator+fileNames[i]));
			Matcher m = p.matcher(content);
			int searchIdx = 0;
	
			//find all images
			while(m.find(searchIdx)){
				int endIdx = m.end();
//				String found = content.substring(startIdx, endIdx);
				String url = m.group(1);
				String scientificName = m.group(2);
				String generatedUrl = "http://adl.brs.gov.au/marinepests/"+url; 
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
