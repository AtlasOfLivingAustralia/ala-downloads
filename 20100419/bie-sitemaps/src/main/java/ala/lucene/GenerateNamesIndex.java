package ala.lucene;

import java.io.File;
import java.io.FileReader;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;

import ala.infosource.ExtractUtils;
import au.com.bytecode.opencsv.CSVReader;

/**
 * Generate a names index from the name sources.
 *
 * @author Dave Martin (David.Martin@csiro.au)
 */
public class GenerateNamesIndex {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		System.out.println("Generating name index.....");
		long start = System.currentTimeMillis();
		String filePath = System.getProperty("user.dir")
			+File.separator
			+"name-data"
			+File.separator
			+"anbg"
			+File.separator
			+"DarwinCore.txt";
		
		File file = new File("/tmp/nameSearch");
		if(file.exists()){
			FileUtils.forceDelete(file);
		}
		
		CSVReader csvReader = new CSVReader(new FileReader(filePath), '\t', '\"', 2);
		IndexWriter writer = new IndexWriter(file, new StandardAnalyzer(), true, MaxFieldLength.UNLIMITED);
		String[] row = null;
		TaxonNameSoundEx taxonNameSoundEx = new TaxonNameSoundEx();
		int i = 0;
		while((row=csvReader.readNext())!=null){
			i++;
			Document doc = new Document();
			doc.add(new Field("taxonConceptID", row[0], Store.YES, Index.NOT_ANALYZED));
			doc.add(new Field("taxonNameID", row[1], Store.YES, Index.NOT_ANALYZED));
			doc.add(new Field("canonical", row[2], Store.YES, Index.ANALYZED));
			doc.add(new Field("canonicalSoundEx", taxonNameSoundEx.soundEx(row[2]), Store.YES, Index.ANALYZED));
			
			String genericName = ExtractUtils.extractGenericName(row[2]);
			if(genericName!=null){
				doc.add(new Field("generic", genericName, Store.YES, Index.ANALYZED));
				doc.add(new Field("genericSoundEx", taxonNameSoundEx.soundEx(genericName), Store.YES, Index.ANALYZED));
			}
			
			doc.add(new Field("rank", row[3], Store.YES, Index.NOT_ANALYZED));
			doc.add(new Field("author", row[4], Store.YES, Index.NOT_ANALYZED));
			writer.addDocument(doc);
		}
		writer.close();
		long end = System.currentTimeMillis();
		System.out.println("Index generated in: "+((end-start)/1000) +" seconds for "+i+" records.");
	}
}