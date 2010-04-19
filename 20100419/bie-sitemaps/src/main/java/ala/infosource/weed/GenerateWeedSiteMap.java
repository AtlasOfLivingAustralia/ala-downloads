package ala.infosource.weed;

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
 * Site map generator for Weed  
 * 
 * @author Tommy Wang (twang@wollemisystems.com)
 */
@SiteMapGenerator(longName="Weed", shortName="weed")
public class GenerateWeedSiteMap implements Runnable {

	public static void main(String[] args) throws Exception{

		List<String> speciesIndexPageList = new ArrayList<String>();
		String source = "http://www.weeds.gov.au/cgi-bin/";

		String speciesLinkPrefix = "http://www.weeds.gov.au/cgi-bin/weedspeciesindex.pl?id=701&startLetter=";
		String speciesLinkAppendix = "&IndexBy=comname";

		Writer writer = ExtractUtils.getSiteMapWriter("weed");

		for (char c = 'A'; c <= 'Z'; c++) {
			speciesIndexPageList.add(speciesLinkPrefix + c + speciesLinkAppendix); 
		}
		
		//write column headings
		ExtractUtils.writeColumnHeaders(writer, new String[]{ColumnHeaders.URI, ColumnHeaders.SCIENTIFIC_NAME});

		for (String speciesIndexPage : speciesIndexPageList) {

			String speciesIndex = WebUtils.getUrlContentAsString(speciesIndexPage);

			Pattern p = Pattern.compile(
					"(?:<a href=\")" +
					"(weeddetails\\.pl\\?taxon_id=[0-9]{1,})" +
					"(?:\">[" + ExtractUtils.SCIENTIFIC_NAME_PATTERN + "]{1,}</a></td>[\\s]{0,})" +
					"(?:<td><i>)" +
					"([" + ExtractUtils.SCIENTIFIC_NAME_PATTERN + "]{1,})" +
					"(?:</i>)");

			Matcher m = p.matcher(speciesIndex);	

			int searchIdx = 0;


			

			// Get Phylum links		
			while(m.find(searchIdx)){
				int endIdx = m.end();
				//			String found = content.substring(startIdx, endIdx);


				String url = m.group(1);
				String scientificName = m.group(2);

				String generatedUrl = source + url; 
				writer.write(generatedUrl);
				writer.write('\t');
				writer.write(scientificName);
				writer.write('\n');
				writer.flush();
				System.out.println("URL:" + generatedUrl);
				System.out.println("NAME:" + scientificName);

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
