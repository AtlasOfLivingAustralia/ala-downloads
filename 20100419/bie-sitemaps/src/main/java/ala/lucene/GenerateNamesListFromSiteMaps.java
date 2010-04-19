package ala.lucene;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import ala.infosource.ColumnHeaders;
import ala.infosource.ExtractUtils;
import au.com.bytecode.opencsv.CSVReader;

/**
 * Generate a names list from the site maps.
 *
 * @author Dave Martin (David.Martin@csiro.au)
 */
public class GenerateNamesListFromSiteMaps {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		File file = new File(ExtractUtils.DATA_DIRECTORY_PATH);
		if(!file.exists()){
			System.out.println("No data directory available currently. Path: "
				+ExtractUtils.DATA_DIRECTORY_PATH);
		}
		File[] subDirs = file.listFiles();
		FileWriter snfw = new FileWriter(
				ExtractUtils.DATA_DIRECTORY_PATH
				+File.separator
				+"scientificNames.txt");

		FileWriter gfw = new FileWriter(
				ExtractUtils.DATA_DIRECTORY_PATH
				+File.separator
				+"genericNames.txt");
		
		for(File subDir: subDirs){
			if(!subDir.isDirectory())
				continue;
			
			File siteMapFile = new File(subDir.getAbsolutePath()+File.separator+ExtractUtils.SITEMAP_FILENAME);
			if(siteMapFile.exists()){
				System.out.println("Checking site map for :"+subDir.getCanonicalPath());
				FileReader reader = new FileReader(siteMapFile);
				CSVReader csvReader = new CSVReader(reader, '\t', '"');
				String[] columnHeaders = csvReader.readNext();
				int scientificNameCol = -1;
				for(int i=0; i<columnHeaders.length; i++){
					if(ColumnHeaders.SCIENTIFIC_NAME.equals(columnHeaders[i])){
						scientificNameCol=i;
						break;
					}
				}
				
				if(scientificNameCol>=0){
					int i=0;
					//we have a scientific name field, read the file
					while((columnHeaders = csvReader.readNext())!=null){
						i++;
						String sciName = columnHeaders[scientificNameCol];
						snfw.write(columnHeaders[scientificNameCol]);
						snfw.write('\n');
						
						String generic = ExtractUtils.extractGenericName(sciName);
						if(generic!=null){
							gfw.write(generic);
							gfw.write('\n');
						}
					}
					System.out.println("Names written: "+i);
				}
				
			} else {
				System.err.println("Site map unavailable for :"+subDir.getCanonicalPath());
			}
		}
		snfw.close();
		gfw.close();
		System.exit(1);
	}
}