package ala.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;

import ala.infosource.ExtractUtils;

public class TestNameMatching {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		//index doc, returning an index...
		StandardAnalyzer sa = new StandardAnalyzer();
//		Directory d = new Dir
		//create index searcher
		IndexSearcher is = new IndexSearcher("/tmp/nameSearch");
		TaxonNameSoundEx taxonNameSoundex = new TaxonNameSoundEx();
		BufferedReader br = new BufferedReader(new FileReader(ExtractUtils.DATA_DIRECTORY+File.separator+"genericNames.txt"));
		int matches = 0;
		int multiMatch = 0;
		int misses = 0;
		int soundEx = 0;
		int names = 0;
		
		String name = "";
		while((name = br.readLine())!=null){
			names++;
//			TermQuery q = new TermQuery(new Term("generic", name));
			QueryParser qp  = new QueryParser("generic", sa);
			Query q = qp.parse("\""+name+"\"");
			TopDocs topDocs = is.search(q, 2);
			
			if(topDocs.totalHits>=1){
				matches++;
				if(topDocs.totalHits>1){
					multiMatch++;
				} 
			} else {
				qp  = new QueryParser("genericSoundEx", sa);
				String parsedName = taxonNameSoundex.soundEx(name);
				q = qp.parse("\""+parsedName+"\"");
				topDocs = is.search(q, 2);
				
				if(topDocs.totalHits>0){
					soundEx++;
					matches++;
				} else {
					misses++;
				}
			}
		}
		
		System.out.println("Matches: "+matches+", Multimatch: "+multiMatch+", Soundex match: "+soundEx+", Misses: "+misses 
				+". Hit rate: "+(((float)matches/(float)names)*100));
	}
}
