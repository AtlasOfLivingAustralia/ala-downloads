/**
 * 
 */
package ala.infosource.aicn;

import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import ala.infosource.ColumnHeaders;
import ala.infosource.ExtractUtils;
import ala.infosource.SiteMapGenerator;
import ala.infosource.WebUtils;

/**
 * Site map generator for Australian Insect Common Names.
 * 
 * @author Dave Martin (David.Martin@csiro.au)
 */
@SiteMapGenerator(longName="Australian Insect Common Names", shortName="aicn")
public class GenerateAICNSiteMap {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		String startUrl = "http://www.ento.csiro.au/aicn/name_s/b_1.htm";
		
		//setup writer
		Writer writer = ExtractUtils.getSiteMapWriter("aicn");
		ExtractUtils.writeColumnHeaders(writer, new String[]{ColumnHeaders.URI,ColumnHeaders.SCIENTIFIC_NAME,
				ColumnHeaders.GENUS,ColumnHeaders.AUTHOR});
		
		// get URLs to Alphabet pages
		Pattern p = Pattern.compile("[a-z]_[0-9]{1,}\\.htm(?:\">[A-Z]{1}</a>)");
		String content = WebUtils.getUrlContentAsString(startUrl);
		Set<String> urls = ExtractUtils.extractUrls(content, p);
		Set<String> fullUrls = new HashSet<String>();
		for(String url:urls){
			int idx = url.lastIndexOf('\"');
			String fullUrl = "http://www.ento.csiro.au/aicn/name_s/"
				+ url.substring(0, idx);
			fullUrls.add(fullUrl);
		}
		
		//for each URL extract species pages
		Pattern sp = Pattern.compile("[a-z]_[0-9]{1,}\\.htm(?:><I>.*</I>.*</a>)");
		for(String url: fullUrls){
			String speciesListPage = WebUtils.getUrlContentAsString(url);
			Set<String> speciesPageUrls = ExtractUtils.extractUrls(speciesListPage, sp);
			
			//for each extract the scientific name and construct the full URL
			for(String speciesPageUrl: speciesPageUrls){
//				System.out.println(speciesPageUrl);
			
				int idx= speciesPageUrl.indexOf('>');	
				String fullUrl = "http://www.ento.csiro.au/aicn/name_s/"
					+ speciesPageUrl.substring(0, idx);
				
				String scientificName = speciesPageUrl.replaceFirst("[a-z]_[0-9]{1,}\\.htm><I>", "");
				scientificName = scientificName.replaceFirst("</I> .*</a>", "");
				
				String author = speciesPageUrl.replaceFirst("[a-z]_[0-9]{1,}\\.htm><I>.*</I>", "");
				author = author.replaceFirst("</a>", "");
				
				writer.write(fullUrl);
				writer.write('\t');
				writer.write(scientificName);
				writer.write('\t');
				writer.write(ExtractUtils.extractGenericName(scientificName));
				writer.write('\t');
				writer.write(author);
				writer.write('\n');
			}
		}
		writer.flush();
		writer.close();
	}
}