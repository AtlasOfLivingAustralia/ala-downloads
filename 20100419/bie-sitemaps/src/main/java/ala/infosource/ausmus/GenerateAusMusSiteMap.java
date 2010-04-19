/**
 * 
 */
package ala.infosource.ausmus;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ala.infosource.ColumnHeaders;
import ala.infosource.ExtractUtils;
import ala.infosource.SiteMapGenerator;
import ala.infosource.WebUtils;

/**
 * Australian Museum Factsheets site map generator.
 * 
 * @author Dave Martin (David.Martin@csiro.au)
 */
@SiteMapGenerator(longName="Australian Museum Factsheets", shortName="ausmus")
public class GenerateAusMusSiteMap {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		String[] siteIndexPages = new String[]{
			"http://australianmuseum.net.au/animalfinder/Australian-bat-finder",
			"http://australianmuseum.net.au/animalfinder/Coelacanth-and-lungfish-finder",
			"http://australianmuseum.net.au/animalfinder/Eutherian-mammal-finder",
			"http://australianmuseum.net.au/animalfinder/Family-Lamnidae",
			"http://australianmuseum.net.au/animalfinder/Family-Serranidae",
			"http://australianmuseum.net.au/animalfinder/Family-Chaetodontidae",
			"http://australianmuseum.net.au/animalfinder/Frog-finder",
			"http://australianmuseum.net.au/animalfinder/Insect-finder",
			"http://australianmuseum.net.au/animalfinder/Marsupial-finder",
			"http://australianmuseum.net.au/animalfinder/Monotreme-finder",
			"http://australianmuseum.net.au/animalfinder/Ray-finned-fishes-finder",
			"http://australianmuseum.net.au/animalfinder/Shark-ray-and-chimaera-finder"
		};

		Writer writer = ExtractUtils.getSiteMapWriter("ausmus");
		ExtractUtils.writeColumnHeaders(writer, 
				new String[]{ColumnHeaders.URI, ColumnHeaders.SCIENTIFIC_NAME, ColumnHeaders.COMMON_NAME,ColumnHeaders.FAMILY
				,ColumnHeaders.ORDER});

		//build up a full list of index pages
		List<String> urls = new ArrayList<String>();
		for(String siteIndexPage: siteIndexPages){
			Pattern pattern = Pattern.compile("\\?page=[0-9]{1,}");
			String content = WebUtils.getUrlContentAsString(siteIndexPage);
			Set<String> extractedUrls = ExtractUtils.extractUrls(content, pattern, siteIndexPage, null);
			urls.add(siteIndexPage);
			urls.addAll(extractedUrls);
		}
		
		Pattern urlPattern = Pattern.compile(
				"(?:<td>.*<a href=\"/)" + //group 1
				"([A-Z]{1}[A-Za-z\\-]{1,})" +
				"(?:\">)" +
				"(?:<em>)?" +
				"([A-Za-z\\-' ]*)" +
				"(?:</em>)?" +
				"(?:<em>)?" +
				"([A-Za-z\\- ]*)" +
				"(?:</em>)?" +
				"(</a>.*</td>)"+
				"(?:[ \t\r\n]*<td>)?"+
				"(.*)" +
				"(?:</td>)?"+
				"(?:[ \t\r\n]*<td>)?"+
				"([A-Za-z\\-' ]*)" +
				"(?:</td>)?"+
				"(?:[ \t\r\n]*<td>)?"+
				"([A-Za-z\\- ]*)" +
				"(?:</td>)?"
		);
		//
//		//extract all family page URLs
		for(String siteIndexPage: urls){
//			String content = FileUtils.readFileToString(new File("/Users/davejmartin2/ausmus.txt"));
			String content = WebUtils.getUrlContentAsString(siteIndexPage);
			Set<String> pageUrls = ExtractUtils.extractUrls(content, urlPattern);
			for(String url:pageUrls){
				Matcher m = urlPattern.matcher(url);
				boolean found = m.find();
				if(found){
					if(m.groupCount()==7){
						String pageUrl = "http://australianmuseum.net.au/"+ m.group(1);
						String commonName = m.group(2);
						String scientificName = m.group(5).replaceAll("<em>|</em>|</td>", "");
						String family = m.group(6);
						String order = m.group(7).replaceFirst("</td>", "");
						//write out
						writer.write(pageUrl.replaceAll("\t", ""));
						writer.write('\t');
						writer.write(scientificName.replaceAll("\t", ""));
						writer.write('\t');
						writer.write(commonName.replaceAll("\t", ""));
						writer.write('\t');
						writer.write(family.replaceAll("\t", ""));
						writer.write('\t');
						writer.write(order.replaceAll("\t", ""));
						writer.write('\n');
					}
				}
			}
		}
		writer.flush();
		writer.close();
	}
}