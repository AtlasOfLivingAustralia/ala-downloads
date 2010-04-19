/**
 * 
 */
package ala.infosource.florabase;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

import org.gbif.ecat.model.ParsedName;
import org.gbif.ecat.parser.NameParser;

import ala.infosource.ColumnHeaders;
import ala.infosource.ExtractUtils;
import ala.infosource.fishnames.GenerateFishnameSiteMap;
import au.com.bytecode.opencsv.CSVReader;

/**
 * @author davejmartin2
 *
 */
public class GenerateFloraBaseSiteMap {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		InputStream input = GenerateFishnameSiteMap.class.getResourceAsStream("/florabase/siteMap.txt");
		CSVReader reader = new CSVReader(new InputStreamReader(input),'\t', '"');
		
		Writer writer = ExtractUtils.getSiteMapWriter("florabase");
		ExtractUtils.writeColumnHeaders(writer, new String[]{ColumnHeaders.URI,ColumnHeaders.SCIENTIFIC_NAME});
		String[] columnHeaders = reader.readNext();
		String[] fields = null;
		NameParser nameParser = new NameParser();
		
		while((fields = reader.readNext())!=null){
			writer.write(fields[0]);
			ParsedName parsedName = nameParser.parseIgnoreAuthors(fields[1]);
			writer.write('\t');
			if(parsedName!=null){
				writer.write(parsedName.buildFullName());
			}
			writer.write('\n');
		}
		writer.flush();
		reader.close();
	}
}
