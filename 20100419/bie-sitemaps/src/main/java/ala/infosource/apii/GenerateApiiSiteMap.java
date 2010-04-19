package ala.infosource.apii;

import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ala.infosource.ColumnHeaders;
import ala.infosource.ExtractUtils;
import ala.infosource.SiteMapGenerator;
import ala.infosource.WebUtils;

/**
 * Site map generator for Australian Plant Image Index  
 * 
 * @author Tommy Wang (twang@wollemisystems.com)
 */
@SiteMapGenerator(longName="SpeciesBank - ABRS", shortName="apii")
public class GenerateApiiSiteMap implements Runnable {

	public static void main(String[] args) throws Exception{

		String genusIndexPage = "http://www.anbg.gov.au/photo/apii/genus";
		String source = "http://www.anbg.gov.au";
		String genusIndex = WebUtils.getUrlContentAsString(genusIndexPage);

		Pattern p = Pattern.compile(
				"(?:<a href=)?" +
				"(/photo/apii/genus/[a-zA-Z0-9]{1,})" +
				"(?:>[\\s]{0,})" +
				"([" + ExtractUtils.SCIENTIFIC_NAME_PATTERN + "]{1,})" +
				"(?:[\\s]{0,}</a>)");

		Pattern p2 = Pattern.compile(
				"(?:<a href=\")?" +
				"(/photo/apii/id/[a-zA-Z0-9/]{1,})" +
				"(?:\">[\\s]{0,})" +
				"([" + ExtractUtils.SCIENTIFIC_NAME_PATTERN + "]{1,})" +
				"(?:[\\s]{0,}</a>)");

		Matcher m = p.matcher(genusIndex);	

		int searchIdx = 0;

		Writer writer = ExtractUtils.getSiteMapWriter("apii");
		//write column headings
		ExtractUtils.writeColumnHeaders(writer, new String[]{ColumnHeaders.URI, ColumnHeaders.SCIENTIFIC_NAME, ColumnHeaders.GENUS});

		// Get Phylum links		
		while(m.find(searchIdx)){
			int endIdx = m.end();
			//			String found = content.substring(startIdx, endIdx);
			String phylumUrl = m.group(1);
			String phylumName = m.group(2);

			String speciesIndexPage = source + phylumUrl;

			//System.out.println("URL" + speciesIndexPage);
			String speciesIndex = null;

			try{
				speciesIndex = WebUtils.getUrlContentAsString(speciesIndexPage);
			} catch (Exception e){
				System.err.println("Error retrieving page: "+speciesIndexPage);
			}
			if(speciesIndex!=null){
				
//				phylumIndex = phylumIndex.replaceAll("<i>", "");
//				phylumIndex = phylumIndex.replaceAll("</i>", "");
//				phylumIndex = phylumIndex.replaceAll("<br />", " ");
				//System.out.println(phylumIndex);
				Matcher m2 = p2.matcher(speciesIndex);	
				// move the search start index on to current position

				int searchIdx2 = 0;
				

				// Get species page links			
				while(m2.find(searchIdx2)){
					int endIdx2 = m2.end();
				
					String url = m2.group(1);
					String scientificName = m2.group(2);
					
					String generatedUrl = source + url; 
					writer.write(generatedUrl);
					writer.write('\t');
					writer.write(scientificName);
					writer.write('\t');
					writer.write(phylumName);
					writer.write('\n');
					writer.flush();
					System.out.println("URL:" + generatedUrl);
					System.out.println("NAME:" + scientificName);

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
