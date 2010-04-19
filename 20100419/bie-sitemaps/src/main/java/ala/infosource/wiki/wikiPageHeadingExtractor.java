package ala.infosource.wiki;

import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ala.infosource.ColumnHeaders;
import ala.infosource.ExtractUtils;
import ala.infosource.WebUtils;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequestSettings;

public class wikiPageHeadingExtractor {
	
	private final static String sitemapUrl = "http://www2.ala.org.au/sitemaps/wikipedia/siteMap.txt";
	private final static Pattern p = Pattern.compile(
			"(?:[\\s]{0,}<span class=\"mw\\-headline\" id=\"[" + ExtractUtils.SCIENTIFIC_NAME_PATTERN + "]{1,}\">[\\s]{0,})" +
			"([" + ExtractUtils.SCIENTIFIC_NAME_PATTERN + "]{1,})" +
			"(?:[\\s]{0,}</span>)" +
			"(?:[\\s]{0,}</h2>)");
	
	private final static Pattern p2 = Pattern.compile(
			"(?:[\\s]{0,}<span class=\"mw\\-headline\" id=\"[" + ExtractUtils.SCIENTIFIC_NAME_PATTERN + "]{1,}\">[\\s]{0,})" +
			"([" + ExtractUtils.SCIENTIFIC_NAME_PATTERN + "]{1,})" +
			"(?:[\\s]{0,}</span>)" +
			"(?:[\\s]{0,}</h3>)");
	
	private final static Pattern p3 = Pattern.compile(
			"(?:[\\s]{0,}<span class=\"mw\\-headline\" id=\"[" + ExtractUtils.SCIENTIFIC_NAME_PATTERN + "]{1,}\">[\\s]{0,})" +
			"([" + ExtractUtils.SCIENTIFIC_NAME_PATTERN + "]{1,})" +
			"(?:[\\s]{0,}</span>)" +
			"(?:[\\s]{0,}</h4>)");
	
//	private final static Pattern p4 = Pattern.compile(
//			"(?:[\\s]{0,}<h1 id=\"firstHeading\"[\\s]{0,}class=\"firstHeading\">[\\s]{0,})" +
//			"([" + ExtractUtils.SCIENTIFIC_NAME_PATTERN + "Ā\\+×]{1,})" +
//			"(?:[\\s]{0,}</h1>)");
	
	public static void main(String[] args) throws Exception{
		
		String[] wikiPageLinks = getWikiPageLinks(sitemapUrl);
		
		Writer writer = ExtractUtils.getSiteMapWriter("wiki");
		
		ExtractUtils.writeColumnHeaders(writer, new String[]{ColumnHeaders.URI, "Heading", "Rank"});
		
		// iterate through every page link and get all the headings
		for (int i = 0; i < wikiPageLinks.length; i++) {
			
			String wikiPageContent = WebUtils.getUrlContentAsString(wikiPageLinks[i]);
			
			wikiPageContent = wikiPageContent.replaceAll("<i>", "");
			wikiPageContent = wikiPageContent.replaceAll("</i>", "");
			
			Matcher m = p.matcher(wikiPageContent);
			boolean gotH2 = false;			
			int searchIdx = 0;
			
			System.out.println(wikiPageLinks[i]);
			//System.out.println(wikiPageContent);
			
			while(m.find(searchIdx)){
				int endIdx = m.end();
				//			String found = content.substring(startIdx, endIdx);
				String heading = m.group(1);
				
				System.out.println(heading);
				
				writer.write(wikiPageLinks[i]);
				writer.write('\t');
				writer.write(heading);
				writer.write('\t');
				writer.write("H2");
				writer.write('\n');
				
				gotH2 = true;
				searchIdx = endIdx;
			}
			
			Matcher m2 = p2.matcher(wikiPageContent);
			boolean gotH3 = false;			
			int searchIdx2 = 0;
			
			while(m2.find(searchIdx2)){
				int endIdx2 = m2.end();
				//			String found = content.substring(startIdx, endIdx);
				String heading = m2.group(1);
				
				System.out.println(heading);
				
				writer.write(wikiPageLinks[i]);
				writer.write('\t');
				writer.write(heading);
				writer.write('\t');
				writer.write("H3");
				writer.write('\n');
				
				gotH3 = true;
				searchIdx2 = endIdx2;
			}
			
			Matcher m3 = p3.matcher(wikiPageContent);
			boolean gotH4 = false;			
			int searchIdx3 = 0;
			
			while(m3.find(searchIdx3)){
				int endIdx3 = m3.end();
				//			String found = content.substring(startIdx, endIdx);
				String heading = m3.group(1);
				
				System.out.println(heading);
				
				writer.write(wikiPageLinks[i]);
				writer.write('\t');
				writer.write(heading);
				writer.write('\t');
				writer.write("H3");
				writer.write('\n');
				
				gotH4 = true;
				searchIdx3 = endIdx3;
			}
			
//			Matcher m4 = p4.matcher(wikiPageContent);
//			boolean gotH1 = false;			
//			int searchIdx4 = 0;
//			
//			while(m4.find(searchIdx4)){
//				int endIdx4 = m4.end();
//				//			String found = content.substring(startIdx, endIdx);
//				String heading = m4.group(1);
//				
//				System.out.println(heading);
//				
//				writer.write(wikiPageLinks[i]);
//				writer.write('\t');
//				writer.write(heading);
//				writer.write('\t');
//				writer.write("H1");
//				writer.write('\n');
//				
//				gotH4 = true;
//				searchIdx4 = endIdx4;
//			}
			
//			if (!gotH2 && !gotH3 && !gotH4 && !gotH1) {
			if (!gotH2 && !gotH3 && !gotH4) {
				writer.write(wikiPageLinks[i]);
				writer.write('\t');
				writer.write("N/A");
				writer.write('\n');
			}
			
		}
		
	}
	
	public static String[] getWikiPageLinks(String str) throws Exception {
		String sitemapContent;
		WebClient webClient = new WebClient(BrowserVersion.FIREFOX_3);
		webClient.setJavaScriptEnabled(false);
		URL sitemapPage = null;
		// get all the page links from a sitemap		
		try {
			sitemapPage = new URL(str);
			WebRequestSettings reqSettings = new WebRequestSettings(sitemapPage);
			TextPage page = webClient.getPage(reqSettings);
			page.cleanUp();
			sitemapContent = page.getContent();
			//System.out.println(sitemapContent);
			
		} catch (MalformedURLException urlErr) {
			throw new Exception("Supplied wiki page URL is malformed: "+urlErr.getMessage(), urlErr);
		} catch (FailingHttpStatusCodeException e) {
			throw new Exception(e.getMessage(), e);
		} catch (IOException e) {
			throw new Exception(e.getMessage(), e);
		}
		
		String[] pageLinks = sitemapContent.split("\\n");
		
		return pageLinks;
	}
}
