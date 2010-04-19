/**
 * 
 */
package ala.infosource.ausmoths;

import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import ala.infosource.ColumnHeaders;
import ala.infosource.ExtractUtils;
import ala.infosource.SiteMapGenerator;
import ala.infosource.WebUtils;

/**
 * Australian Moths Online Site map generator.
 * 
 * @author Dave Martin (David.Martin@csiro.au)
 */
@SiteMapGenerator(longName="Australian Moths Online", shortName="ausmoths")
public class GenerateAOMSiteMap {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		String[] familyAlbums = new String[]{
				"http://www.ento.csiro.au/gallery/moths/albums.php?set_albumListPage=1",
				"http://www.ento.csiro.au/gallery/moths/albums.php?set_albumListPage=2"
		};
		
		Writer writer = ExtractUtils.getSiteMapWriter("ausmoths");
		ExtractUtils.writeColumnHeaders(writer, new String[]{ColumnHeaders.URI,ColumnHeaders.FAMILY});
		
		//family url patter
		Pattern familyUrlPattern = Pattern.compile("gallery/moths/[A-Z]{1}[a-z]{1,}");
		//species url patter
		Pattern speciesUrlPattern = Pattern.compile("gallery/moths/[A-Z]{1}[a-z]{1,}");

		//extract all family page URLs
		Set<String> familyUrls = new HashSet<String>();
		for(int i=0; i<familyAlbums.length; i++){
			String content = WebUtils.getUrlContentAsString(familyAlbums[i]);
			Set<String> urls = ExtractUtils.extractUrls(content, familyUrlPattern, "http://www.ento.csiro.au/", null);
			familyUrls.addAll(urls);
		}
		
		//for each Genus url
		for(String familyUrl: familyUrls){
			
			int lastIdx = familyUrl.lastIndexOf('/');
			String familyName = familyUrl.substring(lastIdx+1);
			
			String content = WebUtils.getUrlContentAsString(familyUrl);
			Set<String> urls = ExtractUtils.extractUrls(content, speciesUrlPattern, "http://www.ento.csiro.au/", null);
			urls.remove(familyUrl);
			for(String url: urls){
				writer.write(url);
				writer.write('\t');
				writer.write(familyName);
				writer.write('\n');
			}
		}
		writer.flush();
		writer.close();
	}
}