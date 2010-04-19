package ala.infosource.nswecualypts;

import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ala.infosource.ColumnHeaders;
import ala.infosource.ExtractUtils;
import ala.infosource.SiteMapGenerator;
import ala.infosource.WebUtils;

/**
 * Site map generator for NSW Ecualypts
 * 
 * @author Tommy Wang (twang@wollemisystems.com)
 */
@SiteMapGenerator(longName="NSW Ecualypts", shortName="nswecualypts")
public class GenerateNswEcualyptsSiteMap {
	
	final static String[] speciesIndexPages = {"http://plantnet.rbgsyd.nsw.gov.au/cgi-bin/eucclass.pl?st=Angophorinae",
												"http://plantnet.rbgsyd.nsw.gov.au/cgi-bin/eucclass.pl?st=Eucalyptinae",
												"http://plantnet.rbgsyd.nsw.gov.au/cgi-bin/eucclass.pl?st=Arillastreae"};
	final static String source = "http://plantnet.rbgsyd.nsw.gov.au";
	
	public static void main(String[] args) throws Exception{

		Pattern p = Pattern.compile(
				"(?:<a href=)?" +
				//"(/cgi\\-bin/euctax\\.pl\\?/PlantNet/Euc=\\&name=[a-zA-Z](1,))" +
				"(/cgi\\-bin/euctax\\.pl\\?/PlantNet/Euc=\\&name=[a-zA-Z\\+\\\"\\*\\- \\(\\)\\?\\%0-9]{1,})" +
				"(?:>)?" +
				"(["+ExtractUtils.SCIENTIFIC_NAME_PATTERN+"\\+\\\"\\*\\- \\(\\)\\?\\%0-9]{1,})" +
				"(?:</a>)?");
		
	
		Writer writer = ExtractUtils.getSiteMapWriter("nswecualypts");
		//write column headings
		ExtractUtils.writeColumnHeaders(writer, new String[]{ColumnHeaders.URI, ColumnHeaders.SCIENTIFIC_NAME});
		
		for(int i=0; i<speciesIndexPages.length; i++){
			String content = WebUtils.getUrlContentAsString(speciesIndexPages[i]);
			//System.out.println(content);
			Matcher m = p.matcher(content);
			int searchIdx = 0;
	
			//find all images
			while(m.find(searchIdx)){
				int endIdx = m.end();
//				String found = content.substring(startIdx, endIdx);
				String url = m.group(1);
				//System.out.println("URL" + url);
				String scientificName = m.group(2);
				String generatedUrl = source + url; 
				writer.write(generatedUrl);
				writer.write('\t');
				writer.write(scientificName);
				writer.write('\n');
				// move the search start index on to current position
				searchIdx = endIdx;
			}
		}
		writer.flush();
		writer.close();
	}
}
