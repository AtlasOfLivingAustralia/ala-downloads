package ala.infosource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

/**
 * Utilities for extract data from websites.
 * 
 * @author Dave Martin (David.Martin@csiro.au)
 */
public class ExtractUtils {

	//TODO move this stuff out to Google Guice Config or spring config
	public static final char DEFAULT_DELIMITER = '\t';
	public static final String DATA_DIRECTORY = "data";
	public static final String DATA_DIRECTORY_PATH = System.getProperty("user.dir") +File.separator +DATA_DIRECTORY;
	public static final String SITEMAP_FILENAME = "siteMap.txt";
	public static final String SCIENTIFIC_NAME_PATTERN = "a-zA-ZÏËÖÜÄÉÈČÁÀÆŒïëöüäåéèčáàæœóú\\.\\-`'%, ;:&#0-9";
	public static final String SCIENTIFIC_NAME_PATTERN_SIMPLE = "a-zA-ZÏËÖÜÄÉÈČÁÀÆŒïëöüäåéèčáàæœóú\\.\\-`'%, ;:&#";
	public static final Pattern GENERIC_NAME_PATTERN = Pattern.compile("([A-ZÏËÖÜÄÉÈČÁÀÆŒ]{1}[a-zïëöüäåéèčáàæœóú]{1,} )");
	public static final String WHITESPACE_PATTERN = "\r\n\t ";
	/**
	 * Extract a generic name if possible.
	 * 
	 * @param scientificName
	 * @return
	 * @throws IOException
	 */
	public static String extractGenericName(String scientificName) throws IOException {
		Matcher m = GENERIC_NAME_PATTERN.matcher(scientificName);
		if(m.find()){
			return m.group(1);
		}
		return null;
	}
	
	/**
	 * Creates a writer for a tab file. 
	 * 
	 * @param infosourceSimpleName
	 * @return
	 * @throws IOException
	 */
	public static Writer getSiteMapWriter(String infosourceSimpleName) throws IOException {
		String dataDir = DATA_DIRECTORY_PATH + File.separator + infosourceSimpleName + File.separator;
		FileUtils.forceMkdir(new File(dataDir));
		File siteMapFile = new File(dataDir+SITEMAP_FILENAME);
		if(siteMapFile.exists())
			FileUtils.forceDelete(siteMapFile);
		return new FileWriter(siteMapFile);
	}
	
	/**
	 * Write column headers.
	 * 
	 * @param writer
	 * @param headers
	 * @throws Exception
	 */
	public static void writeColumnHeaders(Writer writer, String[] headers) throws Exception {
		for(int i=0; i<headers.length; i++){
			if(i>0)
				writer.write(DEFAULT_DELIMITER);
			writer.write(headers[i]);
		}
		writer.write('\n');
	}
	
	/**
	 * Extract URLs from the supplied content.
	 * 
	 * @param content the content to search
	 * @param pattern used to find URLs
	 * @param urlPrefix used to construct the full URL where relative URLs are in use
	 * @param urlSuffix used to construct the full URL where relative URLs are in use
	 * 
	 * @return distinct set of URLs
	 */
	public static Set<String> extractUrls(String content, Pattern pattern, String urlPrefix, String urlSuffix){
		
		Set<String> urls = new LinkedHashSet<String>();
		Matcher m = pattern.matcher(content);
		int searchIdx = 0;

		//find all images
		while(m.find(searchIdx)){
			int startIdx = m.start();
			int endIdx = m.end();
			String found = content.substring(startIdx, endIdx);
			StringBuffer sb = new StringBuffer();
			if(urlPrefix!=null)
				sb.append(urlPrefix);
			sb.append(found);
			if(urlSuffix!=null)
				sb.append(urlSuffix);
			
			urls.add(sb.toString());
			// move the search start index on to current position
			searchIdx = endIdx;
		}
		return urls;
	}
	
	public static Set<String> extractUrls(String content, Pattern pattern){
		return extractUrls(content, pattern, null, null);
	}
	
	/**
	 * Strip out a section of the supplied string buffer. This is almost definitely not
	 * the fastest way to do this.
	 * 
	 * @param buff
	 * @param startSequence
	 * @param endSequence
	 * @param stripStrings
	 * @return
	 */
	public static String extractSectionOfBuffer(StringBuffer buff, String startSequence, String endSequence){
		int startIdx = buff.indexOf(startSequence);
		int endIdx = buff.indexOf(endSequence, startIdx);
		String characters = buff.substring(startIdx+(startSequence.length()), endIdx);
		return characters;
	}

	/**
	 * Remove sections of buffer with the supplied start and end sequence.
	 * 
	 * @param buff
	 * @param startSequence
	 * @param endSequence
	 * @param stripStrings
	 * @return
	 */
	public static void removeSectionOfBuffer(StringBuffer buff, String startSequence, String endSequence){
		
		int startIdx = 0;
		int endIdx = 1;
	
		while(startIdx>0 && endIdx>0 && endIdx>startIdx){
			startIdx = buff.indexOf(startSequence);
			endIdx = buff.indexOf(endSequence, startIdx);
			buff.replace(startIdx, endIdx, "");
		}
	}	
	
	/**
	 * Strip all HTML tags from the supplied string.
	 * 
	 * @param characters
	 * @return
	 */
	private static String stripAnyHtmlTags(String characters) {
		
		StringBuffer sb = new StringBuffer(characters);
		StringBuffer output = new StringBuffer();
		
		int currentIdx = 0;
		boolean eob = false;
		while(!eob){
			int startIdx = sb.indexOf("<", currentIdx);
			if(startIdx<0){
				eob=true;
				break;
			}
			//grab the bit up to the first angled bracket
			output.append(sb.substring(currentIdx, startIdx));
			currentIdx = sb.indexOf(">", currentIdx);
			if(currentIdx<0)
				eob=true;
			currentIdx++;
		}
		return output.toString();
	}
	
	public static void main(String[] args){
		StringBuffer buff = new StringBuffer(" <div class=\"headingSpeciesStatus\"><strong>Status</strong><br/><b><span class=\"redwriting\">Exotic (absent from Australia)</span></b></div>");
		System.out.println(extractSectionOfBuffer(buff, "\"redwriting\">", "</span>"));
	}
}
