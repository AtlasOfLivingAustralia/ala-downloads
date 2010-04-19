package ala.infosource.birds2000;

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
 * Site map generator for Birds 2000  
 * 
 * @author Tommy Wang (twang@wollemisystems.com)
 */
@SiteMapGenerator(longName="Birds2000", shortName="birds2000")
public class GenerateBirds2000SiteMap implements Runnable {

	public static void main(String[] args) throws Exception{

		List<String> speciesIndexPageList = new ArrayList<String>();

		String speciesIndexPage = "http://www.environment.gov.au/biodiversity/threatened/publications/action/birds2000/ts-list.html";

		Writer writer = ExtractUtils.getSiteMapWriter("birds2000");


		//write column headings
		ExtractUtils.writeColumnHeaders(writer, new String[]{ColumnHeaders.SCIENTIFIC_NAME, ColumnHeaders.COMMON_NAME, ColumnHeaders.CONSERVATION_STATUS});


		String speciesIndex = WebUtils.getUrlContentAsString(speciesIndexPage);

		Pattern p = Pattern.compile(
				"(?:<tr>[\\s]{0,})" +
				"(?:<td><em>)" +
				"([" + ExtractUtils.SCIENTIFIC_NAME_PATTERN + "\\(\\)]{1,})" +
				"(?:</em></td>[\\s]{0,}<td>)" +
				"([" + ExtractUtils.SCIENTIFIC_NAME_PATTERN + "\\(\\)]{1,})" +
				"(?:</td>[\\s]{0,}<td>)" +
				"([" + ExtractUtils.SCIENTIFIC_NAME_PATTERN + "\\(\\)\\+]{1,})" +
				"(?:</td>[\\s]{0,}</tr>)");

		Matcher m = p.matcher(speciesIndex);	

		int searchIdx = 0;




		// Get Phylum links		
		while(m.find(searchIdx)){
			int endIdx = m.end();
			//			String found = content.substring(startIdx, endIdx);


			String scientificName = m.group(1);
			String commonName = m.group(2);
			String conservationStatus = m.group(3);
			
			writer.write(scientificName);
			writer.write('\t');
			writer.write(commonName);
			writer.write('\t');
			writer.write(conservationStatus);
			writer.write('\n');
			writer.flush();
			System.out.println("SCI NAME:" + scientificName + "\tCOMMON NAME:" + commonName + "\tCONSERV STATUS:" + conservationStatus);

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
