/**
 * 
 */
package ala.infosource.padil;

import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gbif.ecat.model.ParsedName;
import org.gbif.ecat.parser.NameParser;

import ala.infosource.ColumnHeaders;
import ala.infosource.ExtractUtils;
import ala.infosource.SiteMapGenerator;
import ala.infosource.WebUtils;

/**
 * Pest and Diseases Image Library sitemap generator.
 *
 * @author Dave Martin (David.Martin@csiro.au)
 */
@SiteMapGenerator(longName="Pest and Disease Image Library", shortName="padil")
public class GeneratePaDILSiteMap {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		//<td><a href="viewPestDiagnosticImages.aspx?id=1031">Elsinoë australis</a></td><td style="font-style:italic;"> 
		// Sweet Orange Fruit Scab 
		// </td><td style="font-style:italic;">Ascomycetes</td>
		
		String speciesPageIndexUrl = "http://www.padil.gov.au/speciesList.aspx?onepage=1";
		String content = WebUtils.getUrlContentAsString(speciesPageIndexUrl);

		Pattern p = Pattern.compile(
				"(?:<td><a href=\")?" +
				"(viewPestDiagnosticImages\\.aspx\\?id=[0-9]{1,})" + // URL
				"(?:\">)?"+
				"(["+ExtractUtils.SCIENTIFIC_NAME_PATTERN+"\\?]*)" + // scientific name
				"(?:</a></td><td style\\=\"font\\-style\\:italic;\">)?" +
				"(["+ExtractUtils.SCIENTIFIC_NAME_PATTERN+"\\?]*)"  // common name
				+"(?:</td>)?"
//				+"(<td style=\"font\\-style:italic;\">)?" +
//				"(["+ExtractUtils.speciesNameChars+" \\(\\)\\-]{1,})"+ //higher taxa
//				"(?:</td>)?"
				);
		
		Writer writer = ExtractUtils.getSiteMapWriter("padil");
		String[] colHeaders = new String[]{ColumnHeaders.URI, ColumnHeaders.SCIENTIFIC_NAME, ColumnHeaders.GENUS, ColumnHeaders.COMMON_NAME};
		ExtractUtils.writeColumnHeaders(writer, colHeaders);
		
		NameParser nameParser = new NameParser();
		
		//search for matches
		Matcher m = p.matcher(content);
		int startIdx = 0;
		while(m.find(startIdx)){
			if(m.groupCount()==3){
				String url = "http://www.padil.gov.au/"+m.group(1);
				url = url.replaceFirst("viewPestDiagnosticImages","viewPest");

				//clean up the scientific name
				String scientificName = m.group(2).trim();
				scientificName = scientificName.replaceAll("\\(\\?\\)", "");
				scientificName = scientificName.replaceAll("[ ]{2,}", " ");
				
				String genus = ExtractUtils.extractGenericName(scientificName);
				
				String commonName = m.group(3).trim();
				commonName = commonName.replaceAll("\\(\\?\\)", "");
				commonName = commonName.replaceAll("[ ]{2,}", " ");
//				scientificName = scientificName.replaceAll("[ ]{2,}", " ");
//				Matcher sciNameMatcher = sciNamePattern.matcher(scientificName);
//				if(sciNameMatcher.find()){
//					String specificEpithet = sciNameMatcher.group(1);
//					String genus = sciNameMatcher.group(2);
//					scientificName = genus + " " + specificEpithet;
//				}
				
				writer.write(url);
				writer.write('\t');
				
				ParsedName parsedName = nameParser.parseIgnoreAuthors(scientificName);
				if(parsedName!=null){
					writer.write(parsedName.buildCanonicalName());
				}else{
					writer.write(scientificName);
				}
				writer.write('\t');
				if(genus!=null){
					writer.write(genus);
				} 
				writer.write('\t');
				writer.write(commonName);
				writer.write('\n');
			}
			startIdx=m.end();
		}
		writer.flush();
		writer.close();
	}
}
