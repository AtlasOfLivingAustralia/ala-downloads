package ala.infosource.anps;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ala.infosource.ColumnHeaders;
import ala.infosource.ExtractUtils;
import ala.infosource.SiteMapGenerator;
import ala.infosource.WebUtils;

/**
 * Site map generator for Australian Native Plants Society  
 * 
 * @author Tommy Wang (twang@wollemisystems.com)
 */
@SiteMapGenerator(longName="Australian Native Plants Society", shortName="anps")
public class GenerateAnpsSiteMap implements Runnable {

	public static void main(String[] args) throws Exception{

		String galleryIndexPage = "http://asgap.org.au/gallery.html";
		String gallerySource = "http://asgap.org.au/";
		String galleryIndex = WebUtils.getUrlContentAsString(galleryIndexPage);
		String phylumIndexPages = new String();
		String lastUrl = new String();
		String lastSciName = new String();
		List<String> ignores = new ArrayList<String>();
		ignores.add("question.html");

		Pattern p = Pattern.compile(
				"(?:<a class=\"bold\" href=\")?" +
				"([a-zA-Z0-9]{1,}\\.html)" +
				"(?:\">[\\s]{0,})" +
				"([" + ExtractUtils.SCIENTIFIC_NAME_PATTERN + "]{1,})" +
				"(?:[\\s]{0,}</a>)");

		Pattern p2 = Pattern.compile(
				"(?:<a href=\")" +
				"([a-zA-Z0-9\\-]{1,}\\.html)" +
				"(?:\">[\\s]{0,})" +
				"([" + ExtractUtils.SCIENTIFIC_NAME_PATTERN + "\"]{1,})" +
				"(?:[\\s]{0,}</a>)?");

		Matcher m = p.matcher(galleryIndex);	

		int searchIdx = 0;

		Writer writer = ExtractUtils.getSiteMapWriter("anps");
		//write column headings
		ExtractUtils.writeColumnHeaders(writer, new String[]{ColumnHeaders.URI, ColumnHeaders.SCIENTIFIC_NAME});

		// Get Phylum links		
		while(m.find(searchIdx)){
			int endIdx = m.end();
			//			String found = content.substring(startIdx, endIdx);
			String phylumUrl = m.group(1);
//			String phylumName = m.group(2);

			phylumIndexPages = gallerySource + phylumUrl;

			//System.out.println("URL" + phylumIndexPages);
			String phylumIndex = null;

			try{
				phylumIndex = WebUtils.getUrlContentAsString(phylumIndexPages);
			} catch (Exception e){
				System.err.println("Error retrieving page: "+phylumIndexPages);
			}
			if(phylumIndex!=null){
				
				phylumIndex = phylumIndex.replaceAll("<i>", "");
				phylumIndex = phylumIndex.replaceAll("</i>", "");
				phylumIndex = phylumIndex.replaceAll("<br />", " ");
				//System.out.println(phylumIndex);
				Matcher m2 = p2.matcher(phylumIndex);	
				// move the search start index on to current position

				int searchIdx2 = 0;

				// Get species page links
				while(m2.find(searchIdx2)){
					int endIdx2 = m2.end();
				
					String url = m2.group(1);
					String scientificName = m2.group(2);
					
					if (url.equals(lastUrl) && scientificName.equals(lastSciName)) {
						searchIdx2 = endIdx2;
						continue;
					}
					
					if(!ignores.contains(url)){
						String generatedUrl = gallerySource + url; 
						writer.write(generatedUrl);
						writer.write('\t');
						writer.write(scientificName);
						writer.write('\n');
						writer.flush();
						System.out.println("URL:" + generatedUrl);
						System.out.println("NAME:" + scientificName);
					}
					
					lastUrl = url;
					lastSciName = scientificName;

					searchIdx2 = endIdx2;
				}
				searchIdx = endIdx;
			}
		}

		writer.flush();
		writer.close();
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub

	}
}
