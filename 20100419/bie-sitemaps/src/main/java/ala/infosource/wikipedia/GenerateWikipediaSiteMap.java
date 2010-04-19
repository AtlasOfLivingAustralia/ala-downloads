package ala.infosource.wikipedia;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import ala.infosource.ColumnHeaders;
import ala.infosource.ExtractUtils;
import au.com.bytecode.opencsv.CSVReader;

public class GenerateWikipediaSiteMap {

	public final static String filePath = "/data/dbpedia.txt";
	
	public static final String SCIENTIFIC_NAME_PATTERN = "([a-zA-ZÏËÖÜÄÉÈČÁÀÆŒïëöüäåéèčáàæœóú]{1,})";
	
	//   "(?:<td><a href=\")?"
	static Pattern p = Pattern.compile(SCIENTIFIC_NAME_PATTERN+"(_%28[A-Za-z]{1,}%29)");
	
	/**
	 * DBPedia values are relative links between pages e.g. "Zygaena_(genus)"
	 * 
	 * This method cleans up these names to produce "Zygaena".
	 * 
	 * @param rawValue
	 * @return
	 */
	public static String cleanupString(String rawValue){
		
		rawValue = rawValue.trim();
		if(rawValue.length()==0)
			return rawValue;
		
		Matcher m = p.matcher(rawValue);
		if(m.matches()){
			String nameCleaned = m.group(1);
			return nameCleaned;
		}
		
		//remove the underscores
		return rawValue.replaceAll("_", " ");
	}
	

	private static String cleanupSpeciesName(String genus, String species) {
		
		genus = cleanupString(genus);
		species = cleanupString(species);
		
		species = species.trim();
		if(species.length()>0 && genus.length()>0){
			String abbreviatedGenus = genus.charAt(0)+".";
			if(species.startsWith(abbreviatedGenus)){
				species = species.replace(abbreviatedGenus, genus);
			}
		}
		return species;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		File file = new File("/tmp/output.txt");
		if(file.exists()){
			FileUtils.forceDelete(file);
		}
		FileWriter w = new FileWriter(file);
		
		CSVReader reader = new CSVReader(new FileReader(filePath),'\t');
		String[] triple = null;
		
		String scientificName = "";
		String subspecies = "";
		String species = "";
		String subgenus = "";
		String genus = "";
		String subfamily = "";
		String family = "";
		String superfamily = "";
		String order = "";
		String clazz = "";
		String phylum = "";
		String kingdom = "";
		
		String subject = null;
		
		//write headers
		ExtractUtils.writeColumnHeaders(w, new String[]{
			ColumnHeaders.URI, 
			ColumnHeaders.SCIENTIFIC_NAME,
			ColumnHeaders.SUBSPECIES,
			ColumnHeaders.SPECIES,
			ColumnHeaders.SUBGENUS,
			ColumnHeaders.GENUS,
			ColumnHeaders.SUBFAMILY,
			ColumnHeaders.FAMILY,
			ColumnHeaders.SUPERFAMILY,
			ColumnHeaders.ORDER,
			ColumnHeaders.CLASS,
			ColumnHeaders.PHYLUM,
			ColumnHeaders.KINGDOM
			}
		);
		
		while((triple=reader.readNext())!=null){
			
			if(triple.length==4){
				
				if(subject==null){
					subject = triple[0];
				} else if(!subject.equals(triple[0])){
					//write line
					w.write("http://wikipedia.org/wiki/"+subject);  //0
					
					String cleanedUpScientificName = cleanupSpeciesName(genus, scientificName);
					String cleanedUpSubSpecies = cleanupSpeciesName(genus, subspecies);
					String cleanedUpSpeciesName = cleanupSpeciesName(genus, species);
					
					writeField(w, cleanedUpScientificName);
					writeField(w, cleanedUpSubSpecies);
					writeField(w, cleanedUpSpeciesName);
					writeField(w, subgenus);
					writeField(w, genus);
					writeField(w, subfamily);
					writeField(w, family);
					writeField(w, superfamily);
					writeField(w, order);
					writeField(w, clazz);
					writeField(w, phylum);
					writeField(w, kingdom);
					w.write("\n");
					//change the subject
					subject = triple[0];
					subspecies = species = subgenus = genus = subfamily = family = superfamily = phylum = kingdom = order = clazz = "";
				}

				if(triple[1].endsWith("ScientificName")){
					scientificName=triple[2];
				} else if(triple[1].endsWith("Genus")){
					genus=triple[2];
				} else if (triple[1].endsWith("Species")){
					species=triple[2];
				} else if (triple[1].endsWith("Subspecies")){
					subspecies=triple[2];
				} else if (triple[1].endsWith("Family")){
					family=triple[2];
				} else if (triple[1].endsWith("Subfamily")){
					subfamily=triple[2];
				} else if (triple[1].endsWith("Subgenus")){
					subgenus=triple[2];
				} else if (triple[1].endsWith("Subfamily")){
					subfamily=triple[2];
				} else if (triple[1].endsWith("Phylum")){
					kingdom=triple[2];
				} else if (triple[1].endsWith("Kingdom")){
					kingdom=triple[2];
				} else if (triple[1].endsWith("Class")){
					clazz=triple[2];
				} else if (triple[1].endsWith("Order")){
					order=triple[2];
				}
			}
		}
	}

	private static void writeField(FileWriter w, String species)
			throws IOException {
		w.write("\t");
		w.write(cleanupString(species)); //1
	}
}
