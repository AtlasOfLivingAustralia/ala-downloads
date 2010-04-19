/**
 * 
 */
package ala.infosource.larvalfishes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ala.infosource.ColumnHeaders;
import ala.infosource.ExtractUtils;
import ala.infosource.Response;
import ala.infosource.SiteMapGenerator;
import ala.infosource.WebUtils;

/**
 * Generate a site map for the Larval Fishes of Australia website.
 * 
 * @author Dave Martin (David.Martin@csiro.au)
 */
@SiteMapGenerator(longName="Larval Fishes of Australia", shortName="larvalfishes")
public class GenerateLarvalFishesSiteMap {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		String rootUrl = "http://larval-fishes.australianmuseum.net.au/database.cfm?&method=search&family=";
		
		String simpleName = "larvalfishes";
		BufferedReader reader = new BufferedReader(
				new FileReader(
						System.getProperty("user.dir")
						+File.separator+
						ExtractUtils.DATA_DIRECTORY+
						File.separator+
						simpleName+
						File.separator+
						"families.txt"));
		
		Writer writer = ExtractUtils.getSiteMapWriter("larvalfishes");
		//write column headings
		ExtractUtils.writeColumnHeaders(writer, new String[]{ColumnHeaders.URI, ColumnHeaders.SCIENTIFIC_NAME, ColumnHeaders.FAMILY});

		// <a href="database.cfm?fish_id=18">ACROPOMATIDAE <em>Howella brodiei</em></a>
		Pattern p = Pattern.compile(
				"(?:<a[ ]{1,}href=\")?" +
				"(database\\.cfm\\?fish_id=[0-9]{1,})" //URL
				+"(?:\">)?" 
				+"([A-Z/]{1,})" //Family name
				+"(?:[ ]{1,}<em>)?" 
				+"([A-Za-z\\.\\(\\) ]{1,})" //Species name
//				+"(?:</em></a>)?"
				);
		
		Pattern titlePattern = Pattern.compile(
				"(?:<h2>[\r\t\n ]*<em>)" +
				"([A-Za-z ,']*)" +
				"(</em>)");
		
		
		String familyName ="";
		while((familyName=reader.readLine())!=null){
			System.out.println(rootUrl+familyName);
			Response response = WebUtils.getUrlContentAsBytes(rootUrl+familyName, true);
			//strip out species pages URLs
			// <a href="database.cfm?fish_id=1566">TRACHIPTERIDAE <em>Desmodema polystictum</em></a>
			
			if(response.getResponseUrl().contains("database.cfm?fish_id=")){
				System.out.println("redirected to species page: "+response.getResponseUrl());
				
				String content = WebUtils.getUrlContentAsString(response.getResponseUrl());
				
				Matcher m = titlePattern.matcher(content);
				String scientificName = "";
				if(m.find()){
					scientificName = m.group(1);
				}
				
				//todo parse and retrieve scientific name ???
				String pageUrl = response.getResponseUrl();
				writer.write(pageUrl);
				writer.write('\t');
				writer.write(scientificName);
				writer.write('\t');
				writer.write(familyName);
				writer.write('\n');
				
			} else {
				
				//else retrieve species page URLs
				String content = new String(response.getResponseAsBytes());
				
				Matcher m = p.matcher(content);
				int start = 0;
				while( m.find(start)){
					
					start = m.end();
//					for(int i=1; i<=m.groupCount(); i++){
//						System.out.println(m.group(i));
//					}
					
					if(m.groupCount()==3){
						String pageUrl = "http://larval-fishes.australianmuseum.net.au/"+ m.group(1);
						String scientificName = m.group(3);
						writer.write(pageUrl);
						writer.write('\t');
						writer.write(scientificName);
						writer.write('\t');
						writer.write(familyName);
						writer.write('\n');
					}
				}// loop through results
			}
		}
		
		writer.flush();
		writer.close();
	}
}