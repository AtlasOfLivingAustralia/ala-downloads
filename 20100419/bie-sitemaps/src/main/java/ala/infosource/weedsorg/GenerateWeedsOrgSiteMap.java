package ala.infosource.weedsorg;

import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ala.infosource.ColumnHeaders;
import ala.infosource.ExtractUtils;
import ala.infosource.SiteMapGenerator;
import ala.infosource.WebUtils;

/**
 * Site map generator for weeds.org.au  
 * 
 * @author Tommy Wang (twang@wollemisystems.com)
 */
@SiteMapGenerator(longName="weeds.org.au", shortName="weedsorg")
public class GenerateWeedsOrgSiteMap implements Runnable {

	public static void main(String[] args) throws Exception{

		String speciesIndexPage = "http://www.weeds.org.au/cgi-bin/weedident.cgi?tpl=region.tpl&s=0&region=all";
		String source = "http://www.weeds.org.au/cgi-bin/";
		String speciesIndex = WebUtils.getUrlContentAsString(speciesIndexPage);
//		System.out.println(speciesIndex);
		Pattern p = Pattern.compile(
				"(?:<a href = \")" +
				"(weedident\\.cgi\\?tpl=plant\\.tpl\\&state=\\&s=0\\&region=all\\&card=[a-zA-Z0-9]{1,})" +
				"(?:\"><i>)" +
				"([" + ExtractUtils.SCIENTIFIC_NAME_PATTERN + "]{1,})" +
				"(?:</i>)" + 
				"(?:<i>)?" +
				"([" + ExtractUtils.SCIENTIFIC_NAME_PATTERN + "]{0,})" +
				"(?:</i>)?");

		Matcher m = p.matcher(speciesIndex);	

		int searchIdx = 0;

		Writer writer = ExtractUtils.getSiteMapWriter("weedsorg");
		//write column headings
		ExtractUtils.writeColumnHeaders(writer, new String[]{ColumnHeaders.URI, ColumnHeaders.SCIENTIFIC_NAME});

		// Get Phylum links		
		while(m.find(searchIdx)){
			int endIdx = m.end();
			//			String found = content.substring(startIdx, endIdx);

			String url = m.group(1);
			String scientificName = m.group(2);
			String appendix = m.group(3);
			
			String generatedUrl = source + url; 
			writer.write(generatedUrl);
			writer.write('\t');
			if ("".equals(appendix.trim())) {
				writer.write(scientificName);
			} else {
				writer.write(scientificName + appendix);
			}
			writer.write('\n');
			writer.flush();
			System.out.println("URL:" + generatedUrl);
			System.out.println("NAME:" + scientificName);
			searchIdx = endIdx;
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
