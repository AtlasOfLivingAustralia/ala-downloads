package ala.infosource.weedalert;

import java.io.Writer;
import java.util.Set;
import java.util.regex.Pattern;

import ala.infosource.ColumnHeaders;
import ala.infosource.ExtractUtils;
import ala.infosource.SiteMapGenerator;
import ala.infosource.WebUtils;

/**
 * Weed Alert site map generator.
 * 
 * @author Dave Martin (David.Martin@csiro.au)
 */
@SiteMapGenerator(longName="Weed Alert", shortName="weedalert")
public class GenerateWeedAlertSiteMap {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		String[] siteIndexPages = new String[]{
			"http://plantnet.rbgsyd.nsw.gov.au/cgi-bin/NSWfl.pl?page=nswfl&weedalert=yes&weedstat=Introduced&dist=&datrange=1460"
		};

		Writer writer = ExtractUtils.getSiteMapWriter("weedalert");
		ExtractUtils.writeColumnHeaders(writer, new String[]{ColumnHeaders.URI, ColumnHeaders.SCIENTIFIC_NAME, ColumnHeaders.PEST_STATUS});
		
		//family url patter
		Pattern urlPattern = Pattern.compile("/cgi\\-bin/NSWfl\\.pl\\?page=nswfl&showsyn=&dist=&constat=&lvl=sp&name=[A-Za-z\\~\\+]{1,}");

		//extract all family page URLs
		for(int i=0; i<siteIndexPages.length; i++){
			String content = WebUtils.getUrlContentAsString(siteIndexPages[i]);
			Set<String> urls = ExtractUtils.extractUrls(content, urlPattern, "http://plantnet.rbgsyd.nsw.gov.au", null);
			for(String url:urls){
				int idx = url.lastIndexOf('=');
				String scientificName = url.substring(idx+1);
				scientificName = scientificName.replaceAll("[\\+\\~]", " ");
				writer.write(url);
				writer.write('\t');
				writer.write(scientificName);
				writer.write('\t');
				writer.write("introduced");
				writer.write('\n');
			}
		}
		writer.flush();
		writer.close();
	}
}