/**
 * 
 */
package ala.infosource.floraonline;

import java.io.Writer;
import java.util.Set;
import java.util.regex.Pattern;

import ala.infosource.ExtractUtils;
import ala.infosource.SiteMapGenerator;
import ala.infosource.WebUtils;

/**
 * Site map generator Flora Online.
 * 
 * @author Dave Martin (David.Martin@csiro.au)
 */
@SiteMapGenerator(longName="Flora Online", shortName="floraonline")
public class GenerateFloraOnlineSiteMap {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		String[] siteIndexPages = new String[]{
			"http://plantnet.rbgsyd.nsw.gov.au/cgi-bin/NSWfl.pl?page=nswfl&search=yes&namesearch=*&showsyn=OK&dist="
		};

		Writer writer = ExtractUtils.getSiteMapWriter("floraonline");
		//write column headings
		writer.write("URI");
		writer.write('\t');
		writer.write("scientificName");
		writer.write('\n');
		
		//family url patter
		Pattern urlPattern = Pattern.compile("NSWfl\\.pl\\?page=nswfl&lvl=(?:sp|in)?&name=[A-Za-z\\~\\+]{1,}");

		//extract all family page URLs
		for(int i=0; i<siteIndexPages.length; i++){
			String content = WebUtils.getUrlContentAsString(siteIndexPages[i]);
//			String content = FileUtils.readFileToString(new File("/Users/davejmartin2/floraonline.txt"));
			Set<String> urls = ExtractUtils.extractUrls(content, urlPattern, "http://plantnet.rbgsyd.nsw.gov.au/cgi-bin/", null);
			for(String url:urls){
				int idx = url.lastIndexOf('=');
				String scientificName = url.substring(idx+1);
				scientificName = scientificName.replaceAll("[\\+\\~]", " ");
				writer.write(url);
				writer.write('\t');
				writer.write(scientificName);
				writer.write('\n');
			}
		}
		writer.flush();
		writer.close();
	}
}