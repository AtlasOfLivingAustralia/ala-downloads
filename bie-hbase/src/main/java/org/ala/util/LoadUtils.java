/***************************************************************************
 * Copyright (C) 2010 Atlas of Living Australia
 * All Rights Reserved.
 *
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ***************************************************************************/
package org.ala.util;

import au.org.ala.data.util.RankType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.ala.lucene.LuceneUtils;
import org.ala.model.Publication;
import org.ala.model.TaxonConcept;
import org.ala.model.TaxonName;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.springframework.util.CollectionUtils;
/**
 * This class provides utilities for loading data from source files.
 * This includes creating temporary lucene indexes for loading
 * purposes, and then lookups against these indexes.
 *
 * @author Dave Martin (David.Martin@csiro.au)
 */
public class LoadUtils {

	protected static Logger logger = Logger.getLogger(LoadUtils.class);
	
	public static final String BIE_STAGING_DIR = "/data/bie-staging/";
	public static final String BASE_DIR = "/data/lucene/loading/";
	public static final String TC_INDEX_DIR = BASE_DIR+"taxonConcept";
	public static final String REL_INDEX_DIR = BASE_DIR+"relationship";
	public static final String ACC_INDEX_DIR = BASE_DIR+"accepted";
	public static final String PUB_INDEX_DIR = BASE_DIR+"publication";
        public static final String TN_INDEX_DIR = BASE_DIR +"taxonName";
        public static final String COL_INDEX_DIR = BASE_DIR + "col";
	static Pattern p = Pattern.compile("\t");
	
	private IndexSearcher tcIdxSearcher;
	private IndexSearcher relIdxSearcher;
	private IndexSearcher accIdxSearcher;
	private IndexSearcher pubIdxSearcher;
        private IndexSearcher tnIdxSearcher;
        private IndexSearcher colIdxSearcher;
	
	public LoadUtils() throws Exception {}
	
	/**
	 * Retrieve a list of concepts associated with this name guid.
	 *  
	 * @param nameGuid
	 * @param limit
	 * @return
	 * @throws Exception
	 */
	public List<TaxonConcept> getByNameGuid(String nameGuid, int limit) throws Exception {
		return searchTaxonConceptIndexBy("nameGuid", nameGuid, limit); 
	}

        /**
         * Returns the taxon name for the supplied GUID
         * @param nameGuid
         * @return
         * @throws Exception
         */
        public TaxonName getNameByGuid(String nameGuid) throws Exception {
            List<TaxonName> tns= searchTaxonNameIndexBy(new String[]{"guid"}, new String[]{nameGuid}, null, null,null, 1);
            if(tns.isEmpty())
                return null;
            else
                return tns.get(0);
        }
        /**
         * Returns all the names that are similar to the supplied Taxon Name
         * @param name
         * @param limit
         * @return
         * @throws Exception
         */
        public List<TaxonName> getSimilarNames(TaxonName name, int limit) throws Exception{
            //find all the names that hvae the same authorship uninomial, etc
            List<TaxonName> tns = searchTaxonNameIndexBy(new String[]{"uninomial", "genus", "specificEpithet", "infraspecificEpithet"},
                    new String[]{name.getUninomial(), name.getGenus(), name.getSpecificEpithet(), name.getInfraspecificEpithet()},
                    new String[]{"scientificName", "authorship"}, new String[]{name.getNameComplete(), name.getAuthorship()}, new int[]{4,0}, limit);

            //removed the supplied taxon name from the result
            tns.remove(name);
            return tns;
        }

	/**
	 * Retrieve guids for concepts associated with this publication id.
	 *  
	 * @param publicationGuid
	 * @param limit
	 * @return
	 * @throws Exception
	 */
	public List<String> getGuidsForPublicationGuid(String publicationGuid, int limit) throws Exception {
		
		Query query = new TermQuery(new Term("publishedInCitation", publicationGuid));
		TopDocs topDocs = getTcIdxSearcher().search(query, limit);
		List<String> guids = new ArrayList<String>();
		for(ScoreDoc scoreDoc: topDocs.scoreDocs){
			Document doc = getTcIdxSearcher().doc(scoreDoc.doc);
			guids.add(doc.get("guid"));
		}
		return guids;
	}	
	
	/**
	 * Retrieve the taxon concept associated with this guid.
	 *  
	 * @param nameGuid
	 * @param limit
	 * @return
	 * @throws Exception
	 */
	public TaxonConcept getByGuid(String guid, int limit) throws Exception {
		List<TaxonConcept> tcs =  searchTaxonConceptIndexBy("guid", guid, limit);
		if(tcs.isEmpty())
			return null;
		return tcs.get(0);
	}

	/**
	 * Retrieve the publication for this GUID.
	 * 
	 * @param guid
	 * @return
	 * @throws Exception
	 */
	public Publication getPublicationByGuid(String guid) throws Exception {
		if(guid==null)
			return null;
		Query query = new TermQuery(new Term("guid", guid));
		TopDocs topDocs = getPubIdxSearcher().search(query, 1);
		for(ScoreDoc scoreDoc: topDocs.scoreDocs){
			Document doc = getTcIdxSearcher().doc(scoreDoc.doc);
			Publication p = new Publication();
			p.setGuid(doc.get("guid"));
			p.setTitle(doc.get("title"));
			p.setAuthor(doc.get("authorship"));
			p.setDatePublished(doc.get("datePublished"));
			p.setPublicationType("publicationType");
			return p;
		}
		return null;
	}
	
	/**
	 * Search the index with the supplied value targeting a specific column.
	 * 
	 * @param columnName
	 * @param value
	 * @param limit
	 * @return
	 * @throws IOException
	 * @throws CorruptIndexException
	 */
	private List<TaxonConcept> searchTaxonConceptIndexBy(String columnName, String value, int limit)
			throws Exception {
		Query query = new TermQuery(new Term(columnName, value));
		TopDocs topDocs = getTcIdxSearcher().search(query, limit);
		List<TaxonConcept> tcs = new ArrayList<TaxonConcept>();
		for(ScoreDoc scoreDoc: topDocs.scoreDocs){
			Document doc = getTcIdxSearcher().doc(scoreDoc.doc);
			tcs.add(createTaxonConceptFromIndex(doc));
		}	
		return tcs;
	}
        private List<TaxonConcept> searchCoLIndexBy(String colName, String value, int limit) throws Exception{
            Query query = new TermQuery(new Term(colName, value));
		TopDocs topDocs = getCoLIdxSearcher().search(query, limit);
		List<TaxonConcept> tcs = new ArrayList<TaxonConcept>();
		for(ScoreDoc scoreDoc: topDocs.scoreDocs){
			Document doc = getCoLIdxSearcher().doc(scoreDoc.doc);
			tcs.add(createTaxonConceptFromColIndex(doc));
		}
		return tcs;
        }
        private List<TaxonName> searchTaxonNameIndexBy(String[] columnName, String[] values, String[] fuzzyFields, String[] fuzzyValues, int[] prefixes, int limit) throws Exception {
            List<TaxonName> tns = new ArrayList<TaxonName>();
            if (columnName.length == values.length) {

                BooleanQuery boolQuery = new BooleanQuery();

                for (int i = 0; i < columnName.length; i++) {
                      String value = values[i] == null ? "NULL":values[i];
                        Query q = new TermQuery(new Term(columnName[i], value));
                        boolQuery.add(q, Occur.MUST);
                   
                    
                }
                if(fuzzyFields != null && fuzzyFields.length == fuzzyValues.length){
                    //val query = new FuzzyQuery(new Term(lookupKey, lookupValue), 0.80f, 3)
                    for(int i =0;i<fuzzyFields.length; i++){
                        String value = fuzzyValues[i] == null ? "NULL": fuzzyValues[i];
                        Query fq = new FuzzyQuery(new Term(fuzzyFields[i], value), 0.90f, prefixes[i]);
                        boolQuery.add(fq, Occur.MUST);
                    }
                }
                TopDocs topDocs = getTnIdxSearcher().search(boolQuery, limit);
                for(ScoreDoc scoreDoc: topDocs.scoreDocs){
			Document doc = getTnIdxSearcher().doc(scoreDoc.doc);
			tns.add(createTaxonNameFromIndex(doc));
		}
            }
            return tns;
        }

        private TaxonName createTaxonNameFromIndex(Document doc){
            TaxonName tn = new TaxonName();
            tn.setGuid(doc.get("guid"));
            tn.setNameComplete(getDocValue(doc,"scientificName"));
            tn.setAuthorship(getDocValue(doc,"authorship"));
            //set the rank string to the RankType
            String rank = getDocValue(doc,"rank");
            if(rank != null){
                RankType rt = RankType.getForStrRank(rank);
                if(rt != null)
                    rank = rt.getRank();
                else
                    logger.warn("Unknown rank type : " + rank + " for " + tn.getGuid());
            }
            
            tn.setRankString(rank);
            tn.setUninomial(getDocValue(doc,"uninomial"));
            tn.setGenus(getDocValue(doc,"genus"));
            tn.setSpecificEpithet(getDocValue(doc,"specificEpithet"));
            tn.setInfraspecificEpithet(getDocValue(doc,"infraspecificEpithet"));

            return tn;
        }
        public String getDocValue(Document doc, String field){
            String value = doc.get(field);
            return value == null || value.equals("NULL")? null : value;
        }

	/**
	 * Populate a TaxonConcept from the data in the lucene index.
	 * 
	 * @param doc
	 * @return
	 */
	private TaxonConcept createTaxonConceptFromIndex(Document doc) {
		TaxonConcept taxonConcept = new TaxonConcept();
		taxonConcept.setGuid(doc.get("guid"));
		taxonConcept.setParentGuid(doc.get("http://rs.tdwg.org/ontology/voc/TaxonConcept#IsChildTaxonOf"));
		taxonConcept.setNameString(doc.get(LuceneUtils.SCI_NAME_RAW));
                taxonConcept.setNameGuid(doc.get("nameGuid"));
		taxonConcept.setPublishedIn(doc.get("publishedIn"));
		taxonConcept.setPublishedInCitation(doc.get("publishedInCitation"));
                String children = doc.get("children");
                if(children != null && !children.equals(""))
                    taxonConcept.getChildrenGuid().addAll(CollectionUtils.arrayToList(children.split(",")));
                String guid = taxonConcept.getGuid();
                String internalId = guid.substring(guid.lastIndexOf(":")+1);
                if(guid.contains("afd.taxon"))
                    taxonConcept.setInfoSourceURL("http://www.environment.gov.au/biodiversity/abrs/online-resources/fauna/afd/taxa/"+internalId);
                else if(guid.contains("apni.taxon"))
                    taxonConcept.setInfoSourceURL("http://biodiversity.org.au/apni.taxon/"+internalId);
		return taxonConcept;
	}
        private TaxonConcept createTaxonConceptFromColIndex(Document doc){
            
            TaxonConcept taxonConcept = new TaxonConcept();
            taxonConcept.setId(Integer.parseInt(doc.get("id")));
            taxonConcept.setGuid(getDocValue(doc, "guid"));
            //if the guid is nul; set it to the id
            if(taxonConcept.getGuid()== null)
                taxonConcept.setGuid(Integer.toString(taxonConcept.getId()));
            taxonConcept.setNameString(getDocValue(doc,"nameShow"));
            taxonConcept.setParentId(getDocValue(doc,"parent"));
            
            return taxonConcept;
        }
        private TaxonName createTaxonNameFromColIndex(Document doc){
            TaxonName tn = new TaxonName();
            tn.authorship = getDocValue(doc,"author");
            tn.genus = doc.get("genusShow");
            tn.infraspecificEpithet = doc.get("infraspecificShow");
            tn.specificEpithet = doc.get("speciesShow");
            tn.nameComplete = doc.get("nameShow");
            String rank = getDocValue(doc, "rank");
            String marker = getDocValue(doc,"infraMarker" );
            if(rank != null){
                RankType rt = RankType.getForStrRank(rank);
                if(rt != null && rt == RankType.INFRASPECIFICNAME && marker != null){

                    rt = RankType.getForStrRank(marker);
                }
                rank = rt != null ? rt.getRank():rank;
                tn.rankString = rank;
            }
            return tn;
        }
//        /**
//         * Returns all the relationships for the supplied guid
//         * @param guid
//         * @return
//         */
//        public java.util.Map<String, List<String>> getRelationshipsForConcept(String guid){
//            //toTaxon
//            TopDocs topDocs = 
//
//            return null;
//        }
	
	/**
	 * Is this concept a vernacular concept.
	 * 
	 * @param guid
	 * @return
	 * @throws Exception
	 */
	public boolean isVernacularConcept(String guid) throws Exception {
		TermQuery toTaxonQuery = new TermQuery(new Term("toTaxon", guid));
		TermQuery relQuery = new TermQuery(new Term("relationship", "http://rs.tdwg.org/ontology/voc/TaxonConcept#HasVernacular"));
		BooleanQuery query = new BooleanQuery();
		query.add(toTaxonQuery, Occur.MUST);
		query.add(relQuery, Occur.MUST);
		
		TopDocs topDocs = getRelIdxSearcher().search(query, 1);
		return topDocs.scoreDocs.length>0;
	}
	
	/**
	 * Retrieve the taxon concepts for which the supplied taxon concept guid is a vernacular concept.
	 * 
	 * @param vernacularGuid
	 * @return
	 * @throws Exception
	 */
	public List<String> getIsVernacularConceptFor(String vernacularGuid) throws Exception {

		TermQuery toTaxonQuery = new TermQuery(new Term("toTaxon", vernacularGuid));
		TermQuery relQuery = new TermQuery(new Term("relationship", "http://rs.tdwg.org/ontology/voc/TaxonConcept#HasVernacular"));
		BooleanQuery query = new BooleanQuery();
		query.add(toTaxonQuery, Occur.MUST);
		query.add(relQuery, Occur.MUST);
		
		TopDocs topDocs = getRelIdxSearcher().search(query, 20);
		List<String> guids = new ArrayList<String>();
		for(ScoreDoc scoreDoc: topDocs.scoreDocs){
			Document doc = relIdxSearcher.doc(scoreDoc.doc);
			guids.add(doc.get("fromTaxon"));
		}
		return guids;
	}

	private Searcher getRelIdxSearcher() throws Exception {
		//FIXME move to dependency injection
		if(this.relIdxSearcher==null){
			this.relIdxSearcher = new IndexSearcher(REL_INDEX_DIR);
		}
		return this.relIdxSearcher;
	}
	
	private Searcher getAccIdxSearcher() throws Exception {
		if(this.accIdxSearcher==null){
			this.accIdxSearcher = new IndexSearcher(ACC_INDEX_DIR);
		}
		return this.accIdxSearcher;
	}	

	private Searcher getTcIdxSearcher() throws Exception {
		if(this.tcIdxSearcher==null){
			this.tcIdxSearcher = new IndexSearcher(TC_INDEX_DIR);
		}
		return this.tcIdxSearcher;
	}
	
	private Searcher getPubIdxSearcher() throws Exception {
		if(this.pubIdxSearcher==null){
			this.pubIdxSearcher = new IndexSearcher(PUB_INDEX_DIR);
		}
		return this.pubIdxSearcher;
	}

        private Searcher getTnIdxSearcher() throws Exception {
            if(this.tnIdxSearcher == null)
                this.tnIdxSearcher = new IndexSearcher(TN_INDEX_DIR);
            return this.tnIdxSearcher;
        }
        private Searcher getCoLIdxSearcher() throws Exception {
            if(this.colIdxSearcher == null)
                this.colIdxSearcher = new IndexSearcher(COL_INDEX_DIR);
            return this.colIdxSearcher;
        }
        public TaxonConcept getCoLByID(String id) throws Exception{
            List<TaxonConcept> results  = searchCoLIndexBy("id", id, 1);
            if(!results.isEmpty())
                return results.get(0);
            return null;
        }
        public TaxonName getCoLName(String id) throws Exception{
            Query query = new TermQuery(new Term("id", id));
            TopDocs topDocs = getCoLIdxSearcher().search(query, 1);
            if(topDocs.totalHits>0)
                return createTaxonNameFromColIndex(getCoLIdxSearcher().doc(topDocs.scoreDocs[0].doc));
            return null;
        }
        /**
         * Attempts to locate the CoL TaxonConcept that is similar to supplied tc
         * @param tc
         * @return
         */
        public TaxonConcept getCoL(TaxonName tn) throws Exception{
            TaxonConcept colTc = null;
            BooleanQuery boolQuery = new BooleanQuery();


            RankType rank = RankType.getForName(tn.getRankString());
            if(rank.getId() <= RankType.GENUS.getId()){

                if(tn.getUninomial() != null){
                  boolQuery.add(new TermQuery(new Term("name", tn.getUninomial().toLowerCase())), Occur.MUST);
                }
                if(tn.genus != null && tn.uninomial==null){
                    boolQuery.add(new TermQuery(new Term("name", tn.genus.toLowerCase())), Occur.MUST);
                }
            }
            else{
                //If the rank is definitely above a species we want to ensure that we are matching correctly
                //Corrects issue with Melaleuca bracteata 'Revolution Green' being mapped to Melaleuca bracteata
                if(rank.getId()>RankType.SPECIES.getId())
                    boolQuery.add(new TermQuery(new Term("rank", "Infraspecies")), Occur.MUST);
                //if(tn.genus != null){
                    boolQuery.add(new TermQuery(new Term("genus", colSearchTerm(tn.genus))), Occur.MUST);
                //}
                //if(tn.specificEpithet != null){
                    boolQuery.add(new TermQuery(new Term("species", colSearchTerm(tn.specificEpithet))), Occur.MUST);
                //}
                //if(tn.infraspecificEpithet != null){
                    boolQuery.add(new TermQuery(new Term("infraspecific", colSearchTerm(tn.infraspecificEpithet))), Occur.MUST);
                //}
            }
            //System.out.println("boolQuert:" + boolQuery);

            TopDocs topDocs = getCoLIdxSearcher().search(boolQuery, 10);
		List<TaxonConcept> tcs = new ArrayList<TaxonConcept>();
		if(topDocs.scoreDocs.length>=1)
                    colTc = createTaxonConceptFromColIndex(getCoLIdxSearcher().doc(topDocs.scoreDocs[0].doc));
            
            return colTc;
        }
        private String colSearchTerm(String value){
            if(value == null)
                return "NULL";
            else
                return value.toLowerCase();
        }

        public TaxonConcept getAcceptedConcept(List<TaxonConcept> tcs) throws Exception{
            if(tcs != null && tcs.size()>0){
                TaxonConcept tc = null;
                for(TaxonConcept t : tcs){
                    String auth = isAcceptedConcept(t.getGuid());
                    if(auth != null){
                        t.setAcceptedConceptGuid(t.getGuid());
                        return t;
                    }
                    if(tc == null){
                        //only want to add the first concept that has a parent
                        String parent = getParent(t.getGuid());
                        if(parent != null)
                            tc = t;
                    }
                }
                //if there are no tc with parents return the first
                return tc != null?tc: tcs.get(0);
            }
            return null;
        }
        public String getParent(String guid) throws Exception{
            //first attempt to get it with is child of relationship
            List<String> parent = getRelationshipValues("fromTaxon", guid, "toTaxon", "http://rs.tdwg.org/ontology/voc/TaxonConcept#IsChildTaxonOf", 1);
            if(!parent.isEmpty())
                return parent.get(0);
            //now try the parent of relationship
            parent = getRelationshipValues("toTaxon", guid, "fromTaxon", "http://rs.tdwg.org/ontology/voc/TaxonConcept#IsParentTaxonOf", 1);
            if(!parent.isEmpty()){
                //ensure that the supplied parent guid actually exists in our dump file...
                if(getByGuid(parent.get(0), 1) != null)
                    return parent.get(0);
            }
            return null;

        }
        public Set<String> getChildren(String guid) throws Exception{
            Set<String> children = new HashSet<String>();
            children.addAll(getRelationshipValues("toTaxon",  guid, "fromTaxon", "http://rs.tdwg.org/ontology/voc/TaxonConcept#IsChildTaxonOf", getRelIdxSearcher().maxDoc()));
            return children;
            //I think that both of these are populated for each taxon so I don't need to add them twice.
            //children.addAll(getRelationshipValues("fromTaxon", guid, "toTaxon", "http://rs.tdwg.org/ontology/voc/TaxonConcept#IsParentTaxonOf", getRelIdxSearcher().maxDoc()));
            //return children;
            //return getRelationshipValues("toTaxon", guid, "fromTaxon", "http://rs.tdwg.org/ontology/voc/TaxonConcept#IsChildTaxonOf", getRelIdxSearcher().maxDoc());
        }
        public List<String> getRelationshipValues(String field,  String value, String field2Get, String relationship, int limit) throws Exception{
            BooleanQuery boolQuery = new BooleanQuery();
            boolQuery.add(new TermQuery(new Term(field, value)), Occur.MUST);
            boolQuery.add(new TermQuery(new Term("relationship", relationship)), Occur.MUST);
            boolQuery.add(new TermQuery(new Term(field2Get, value)), Occur.MUST_NOT);

            TopDocs topDocs = getRelIdxSearcher().search(boolQuery, limit);
            List<String> rels = new ArrayList<String>();
            for(ScoreDoc scoreDoc: topDocs.scoreDocs){
			Document doc = getRelIdxSearcher().doc(scoreDoc.doc);
			rels.add(doc.get(field2Get));
		}
		return rels;
//            if(topDocs.scoreDocs.length>0){
//                Document doc = relIdxSearcher.doc(topDocs.scoreDocs[0].doc);
//                return doc.get(field2Get);
//            }
            
        }
	
	/**
	 * Is this concept an accepted concept? If so return the authority that
	 * that says so. e.g. "AFD", "APC"
	 * 
	 * @param guid
	 * @return
	 * @throws Exception
	 */
	public String isAcceptedConcept(String guid) throws Exception {
		TermQuery guidQuery = new TermQuery(new Term("guid", guid));
//		TermQuery relQuery = new TermQuery(new Term("relationship", "http://rs.tdwg.org/ontology/voc/TaxonConcept#IsCongruentTo"));
//		BooleanQuery query = new BooleanQuery();
//		query.add(toTaxonQuery, Occur.MUST);
//		query.add(relQuery, Occur.MUST);
		
		TopDocs topDocs = getAccIdxSearcher().search(guidQuery, 1);
		if(topDocs.scoreDocs.length>0){
			Document doc = accIdxSearcher.doc(topDocs.scoreDocs[0].doc);
			return doc.get("authority");
		} else {
			return null;
		}
//		
//		return topDocs.scoreDocs[0];
	}

	/**
	 * Is this concept congruent to another and the "toTaxon" in the relationship
	 * as supplied by ANBG?
	 * 
	 * @param guid
	 * @return
	 * @throws Exception
	 */
	public String isCongruentConcept(String guid) throws Exception {
		TermQuery toTaxonQuery = new TermQuery(new Term("toTaxon", guid));
		TermQuery relQuery = new TermQuery(new Term("relationship", "http://rs.tdwg.org/ontology/voc/TaxonConcept#IsCongruentTo"));
		BooleanQuery query = new BooleanQuery();
		query.add(toTaxonQuery, Occur.MUST);
		query.add(relQuery, Occur.MUST);
                //don't report concepts that are congruent to themsleves.
                query.add(new TermQuery(new Term("fromTaxon", guid)), Occur.MUST_NOT);
		
		TopDocs topDocs = getRelIdxSearcher().search(query, 1);
                if(topDocs.scoreDocs.length>0){
                    return relIdxSearcher.doc(topDocs.scoreDocs[0].doc).get("fromTaxon");
                }
                return null;
		//return topDocs.scoreDocs.length>0;
	}

        public String isIncluded(String guid) throws Exception {
            //toTaxon include
            TermQuery toTaxon = new TermQuery(new Term("toTaxon", guid));
            TermQuery fromTaxon = new TermQuery(new Term("fromTaxon",guid));
            BooleanQuery query = new BooleanQuery();
            query.add(toTaxon, Occur.MUST);
            query.add(fromTaxon, Occur.MUST_NOT);
            query.add(new TermQuery(new Term("relationship","http://rs.tdwg.org/ontology/voc/TaxonConcept#Includes")), Occur.MUST);
            TopDocs topDocs = getRelIdxSearcher().search(query, 1);
                if(topDocs.scoreDocs.length>0){
                    return relIdxSearcher.doc(topDocs.scoreDocs[0].doc).get("fromTaxon");
                }
            //fromTaxon isincludein
            query = new BooleanQuery();
            query.add(toTaxon, Occur.MUST_NOT);
            query.add(fromTaxon, Occur.MUST);
            query.add(new TermQuery(new Term("relationship","http://rs.tdwg.org/ontology/voc/TaxonConcept#IsIncludedIn")), Occur.MUST);
            topDocs = getRelIdxSearcher().search(query, 1);
                if(topDocs.scoreDocs.length>0){
                    return relIdxSearcher.doc(topDocs.scoreDocs[0].doc).get("fromTaxon");
                }
            return null;

        }
	

	public String isSynonymFor(String guid) throws Exception {
		TermQuery toTaxonQuery = new TermQuery(new Term("toTaxon", guid));
		TermQuery relQuery = new TermQuery(new Term("relationship", "http://rs.tdwg.org/ontology/voc/TaxonConcept#HasSynonym"));
		BooleanQuery query = new BooleanQuery();
		query.add(toTaxonQuery, Occur.MUST);
		query.add(relQuery, Occur.MUST);
		
		TopDocs topDocs = getRelIdxSearcher().search(query, 1);
                
                if(topDocs.scoreDocs.length>0){
                    return relIdxSearcher.doc(topDocs.scoreDocs[0].doc).get("fromTaxon");
                }
                return null;
		//return topDocs.scoreDocs.length>0;
	}
	
	/**
	 * Load the accepted concepts 
	 * 
	 * @throws Exception
	 */
	public void loadAccepted() throws Exception {

		File file = new File(ACC_INDEX_DIR);
    	if(file.exists()){
    		FileUtils.forceDelete(file);
    	}
    	FileUtils.forceMkdir(file);
    	int i=0;
		KeywordAnalyzer analyzer = new KeywordAnalyzer();
    	IndexWriter iw = new IndexWriter(file, analyzer, MaxFieldLength.UNLIMITED);
		try {
	    	long start = System.currentTimeMillis();
	    	//add the relationships
	    	TabReader tr = new TabReader(BIE_STAGING_DIR+"anbg/acceptedConcepts.txt");
	    	String[] keyValue = null;
	    	
	    	while((keyValue=tr.readNext())!=null){
	    		if(keyValue.length==2){
	    			i++;
			    	Document doc = new Document();
			    	doc.add(new Field("guid", keyValue[0], Store.YES, Index.ANALYZED));
			    	doc.add(new Field("authority", keyValue[1], Store.YES, Index.ANALYZED));
			    	iw.addDocument(doc);
	    		}
			}
	    	tr.close();
			long finish = System.currentTimeMillis();
	    	logger.info(i+" loaded accepted guids, Time taken "+(((finish-start)/1000)/60)+" minutes, "+(((finish-start)/1000) % 60)+" seconds.");
		} finally {
			iw.close();
		}
	}

	/**
	 * Create the relationships index.
	 * 
	 * @throws Exception
	 */
	public void loadRelationships() throws Exception {

		File file = new File(REL_INDEX_DIR);
    	if(file.exists()){
    		FileUtils.forceDelete(file);
    	}
    	FileUtils.forceMkdir(file);
    	int i=0;
		KeywordAnalyzer analyzer = new KeywordAnalyzer();
    	IndexWriter iw = new IndexWriter(file, analyzer, MaxFieldLength.UNLIMITED);
		try {
	    	long start = System.currentTimeMillis();
	    	//add the relationships
	    	TabReader tr = new TabReader(BIE_STAGING_DIR+"anbg/relationships.txt");
	    	String[] keyValue = null;
	    	
	    	while((keyValue=tr.readNext())!=null){
	    		if(keyValue.length==3){
	    			i++;
			    	Document doc = new Document();
			    	doc.add(new Field("fromTaxon", keyValue[0], Store.YES, Index.ANALYZED));
			    	doc.add(new Field("toTaxon", keyValue[1], Store.YES, Index.ANALYZED));
			    	doc.add(new Field("relationship", keyValue[2], Store.YES, Index.ANALYZED));
			    	iw.addDocument(doc);
	    		}
			}
	    	tr.close();
			long finish = System.currentTimeMillis();
	    	logger.info(i+" loaded relationships, Time taken "+(((finish-start)/1000)/60)+" minutes, "+(((finish-start)/1000) % 60)+" seconds.");
		} finally {
			iw.close();
		}
	}

     public void loadCoLNames() throws Exception{
         long start = System.currentTimeMillis();
         //create a name index
        File file = new File(COL_INDEX_DIR);
        if (file.exists()) {
            FileUtils.forceDelete(file);
        }
        FileUtils.forceMkdir(file);
        //Analyzer analyzer = new KeywordAnalyzer(); - works for exact matches
        KeywordAnalyzer analyzer = new KeywordAnalyzer();
        //initialise lucene
        IndexWriter iw = new IndexWriter(file, analyzer, MaxFieldLength.UNLIMITED);
        int i = 0;

        TabReader tr = new TabReader("/data/checklistbank/rawdata/col2010/taxa.txt", false);
        String[] keyValue = null;

    	while((keyValue=tr.readNext())!=null){
            //id,lsid,name,parentid,rank,acceptedtaxonid, accepted name, scientific name authorship, infraspecific epithet
            i++;
            if(keyValue.length>=5){
                Document doc = new Document();
                doc.add(new Field("id", keyValue[0], Store.YES, Index.ANALYZED));
                doc.add(new Field("guid",replaceNull(keyValue[1],true), Store.YES, Index.ANALYZED));
                //TO DO may need to add the scientific name in its parsed form...
                doc.add(new Field("name", replaceNull(keyValue[2],true), Store.YES, Index.ANALYZED));
                if(keyValue[2] != null)
                    doc.add(new Field("nameShow", keyValue[2], Store.YES, Index.NO));
                doc.add(new Field("parent", replaceNull(keyValue[3],true), Store.YES, Index.ANALYZED));
                doc.add(new Field("rank", replaceNull(keyValue[4]),Store.YES, Index.ANALYZED));
                String accepted = keyValue.length>5?keyValue[5]: null;
                doc.add(new Field("acceptedId", replaceNull(accepted,true), Store.YES, Index.ANALYZED));
                String acceptName = keyValue.length>6?keyValue[6]:null;
                doc.add(new Field("acceptName", replaceNull(acceptName), Store.YES, Index.ANALYZED));
                String author = keyValue.length>7?keyValue[7]:null;
                doc.add(new Field("author",replaceNull(author), Store.YES, Index.ANALYZED));
                String genus = keyValue.length>8?keyValue[8]:null;
                doc.add(new Field("genus", replaceNull(genus,true), Store.YES, Index.ANALYZED));
                if(genus != null)
                    doc.add(new Field("genusShow", genus, Store.YES, Index.NO));
                String species = keyValue.length>9?keyValue[9]:null;
                doc.add(new Field("species", replaceNull(species,true), Store.YES, Index.ANALYZED));
                if(species!=null)
                    doc.add(new Field("speciesShow", species, Store.YES, Index.NO));
                String infrasp = keyValue.length>10?keyValue[10]:null;
                doc.add(new Field("infraspecific", replaceNull(infrasp,true), Store.YES, Index.ANALYZED));
                if(infrasp != null)
                    doc.add(new Field("infraspecificShow",infrasp, Store.YES, Index.NO));
                String infraMark = keyValue.length >11 ?keyValue[11]:null;
                doc.add(new Field("infraMarker", replaceNull(infraMark), Store.YES,Index.ANALYZED));
                 iw.addDocument(doc, analyzer);
            }
        }
         tr.close();
    	iw.close();
        long finish = System.currentTimeMillis();
    	logger.info(i+" indexed CoL names in: "+(((finish-start)/1000)/60)+" minutes, "+(((finish-start)/1000) % 60)+" seconds.");

     }
     private String replaceRank(String rank){
         if(rank == null || rank.equals(""))
             return "NULL";

         return"";
     }

     /**
      * Gets a taxon name from ANBG based on the supplied values. This is used to match a CoL taxon Concept to ANBG
      * @param uninomial
      * @param genus
      * @param species
      * @param infra
      * @param rank
      * @return
      * @throws Exception
      */
     public TaxonName getTaxonName(String uninomial, String genus, String species, String infra, RankType rank)throws Exception{
         TaxonName tn = null;
         BooleanQuery bquery = new BooleanQuery();
         bquery.add(new TermQuery(new Term("lowUninomial",  colSearchTerm(uninomial))), Occur.MUST);
         bquery.add(new TermQuery(new Term("lowGenus",  colSearchTerm(genus))), Occur.MUST);
         bquery.add(new TermQuery(new Term("lowSpecificEpithet",  colSearchTerm(species))), Occur.MUST);
         bquery.add(new TermQuery(new Term("lowInfraspecificEpithet",  colSearchTerm(infra))), Occur.MUST);
         if(rank != null && rank == RankType.SPECIES){
             //prevents Agonis flexuosa from being matched to Agonis flexuosa 'Pied Piper'
             bquery.add(new TermQuery(new Term("rank","http://rs.tdwg.org/ontology/voc/TaxonRank#Species")), Occur.MUST);
         }
         TopDocs topDocs = getTnIdxSearcher().search(bquery, 10);
         if(topDocs.totalHits>0)
             tn = createTaxonNameFromIndex(getTnIdxSearcher().doc(topDocs.scoreDocs[0].doc));
         return tn;
     }

     public void loadTaxonNames() throws Exception {
        long start = System.currentTimeMillis();
        //create a name index
        File file = new File(TN_INDEX_DIR);
        if (file.exists()) {
            FileUtils.forceDelete(file);
        }
        FileUtils.forceMkdir(file);

        //Analyzer analyzer = new KeywordAnalyzer(); - works for exact matches
        KeywordAnalyzer analyzer = new KeywordAnalyzer();
        //initialise lucene
        IndexWriter iw = new IndexWriter(file, analyzer, MaxFieldLength.UNLIMITED);

        int i = 0;

        TabReader tr = new TabReader(BIE_STAGING_DIR+"anbg/taxonNames.txt");
    	String[] keyValue = null;

    	while((keyValue=tr.readNext())!=null){
            /*
             *     writeField(writer,lsid, false) //0
	writeField(writer,scientificName, false) //1
	writeField(writer,(taxonName \ "nameComplete").text, false) //2
	writeField(writer,(taxonName \ "authorship").text, false) //3
        writeField(writer, (taxonName\"uninomial").text, false) //4
        writeField(writer, (taxonName\"genusPart").text, false)//5
        writeField(writer, (taxonName\"specificEpithet").text, false)//6
        writeField(writer, (taxonName\"infraspecificEpithet").text, false)//7
	writeField(writer,rank, false) //8
	writeField(writer,publishedIn, false) //9
	writeField(writer,nomenclaturalCode, false) //10
	writeField(writer,typString, false) //11
        writeField(writer, acceptedConcept, true)//12
             */
            i++;
            if(keyValue.length>=13){
                Document doc = new Document();
                doc.add(new Field("guid", keyValue[0], Store.YES, Index.ANALYZED));
                if(keyValue[1] != null)
                    doc.add(new Field("scientificName", keyValue[1], Store.YES, Index.ANALYZED));
                else
                    System.out.println("Line " + i + " has a null scientific name ");
                //TO DO may need to index various forms of the autor to allow better matching
                //if(keyValue[3] != null)
                 doc.add(new Field("authorship", replaceNull(keyValue[3]), Store.YES, Index.ANALYZED));
                //if(keyValue[4] != null)
                    doc.add(new Field("uninomial", replaceNull(keyValue[4]), Store.YES, Index.ANALYZED));
                    doc.add(new Field("lowUninomial", replaceNull(keyValue[4],true), Store.NO, Index.ANALYZED));
                //if(keyValue[5] != null)
                    doc.add(new Field("genus", replaceNull(keyValue[5]), Store.YES, Index.ANALYZED));
                    doc.add(new Field("lowGenus", replaceNull(keyValue[5],true), Store.NO, Index.ANALYZED));
                //if(keyValue[6] != null)
                    doc.add(new Field("specificEpithet", replaceNull(keyValue[6]), Store.YES, Index.ANALYZED));
                    doc.add(new Field("lowSpecificEpithet", replaceNull(keyValue[6], true), Store.NO, Index.ANALYZED));
                //if(keyValue[7] != null)
                    doc.add(new Field("infraspecificEpithet", replaceNull(keyValue[7]), Store.YES, Index.ANALYZED));
                    doc.add(new Field("lowInfraspecificEpithet", replaceNull(keyValue[7],true), Store.NO, Index.ANALYZED));
                //TODO may need to standardise the ranks before indexing...
                //if(keyValue[8] != null)
                    doc.add(new Field("rank", replaceNull(keyValue[8]), Store.YES, Index.ANALYZED));
                //if(keyValue[12] != null)
                    doc.add(new Field("acceptedLsid" , replaceNull(keyValue[12]), Store.YES, Index.ANALYZED));
                    //give a boost to the documents that have accepted concepts.  This will allow for the most appropriate name match to be applied...
                    if(keyValue[12] != null)
                        doc.setBoost(2.0f);

                iw.addDocument(doc, analyzer);
            }
            
        }
        tr.close();
    	iw.close();
        long finish = System.currentTimeMillis();
    	logger.info(i+" indexed taxon names in: "+(((finish-start)/1000)/60)+" minutes, "+(((finish-start)/1000) % 60)+" seconds.");

    }
     private String replaceNull(String value, boolean lower){
         if(value == null)
             return "NULL";
         else if(lower)
             return value.toLowerCase();
         return value;
     }
     private String replaceNull(String value){
         return replaceNull(value, false);
     }

	/**
	 * Create the taxon concept index.
	 * 
	 * @throws Exception
	 */
	public void loadTaxonConcepts() throws Exception {
		long start = System.currentTimeMillis();
		
		//create a name index
    	File file = new File(TC_INDEX_DIR);
    	if(file.exists()){
    		FileUtils.forceDelete(file);
    	}
    	FileUtils.forceMkdir(file);
    	
    	//Analyzer analyzer = new KeywordAnalyzer(); - works for exact matches
    	KeywordAnalyzer analyzer = new KeywordAnalyzer();
        //initialise lucene
    	IndexWriter iw = new IndexWriter(file, analyzer, MaxFieldLength.UNLIMITED);
//    	IndexSearcher is = new IndexSearcher(REL_INDEX_DIR);
    	
    	int i = 0;
    	
    	//names files to index
    	TabReader tr = new TabReader(BIE_STAGING_DIR+"anbg/taxonConcepts.txt");
    	String[] keyValue = null;
        /*
         * writeField(tcOutput,lsid,false) //0
	      writeField(tcOutput,nameLsid,false) //1
	      writeField(tcOutput,scientificName,false) //2
	      writeField(tcOutput,author,false) //3
	      writeField(tcOutput,authorYear,false) //4
	      writeField(tcOutput,publishedInCitation,false) //5
	      writeField(tcOutput,publishedIn,false) //6
	      writeField(tcOutput,isChildTaxonOf,false) //7
              writeField(tcOutput,accepted, true)//8
         */
    	while((keyValue=tr.readNext())!=null){
    		i++;
    		if(keyValue.length>3){

    			Document doc = new Document();
		    	doc.add(new Field("guid", keyValue[0], Store.YES, Index.ANALYZED));
                        if(keyValue[1] != null)
                            doc.add(new Field("nameGuid", keyValue[1], Store.YES, Index.ANALYZED));
                        else
                            System.out.println(keyValue[0] + " does not have a namelsid line " + i);
		    	if(keyValue[5]!=null){
		    		doc.add(new Field("publishedInCitation", keyValue[5], Store.YES, Index.ANALYZED));
		    	}
		    	if(keyValue[6]!=null){
		    		doc.add(new Field("publishedIn", keyValue[6], Store.YES, Index.NO));
		    	}
                        if(keyValue[8]!= null){
                            doc.add(new Field("children", keyValue[8], Store.YES, Index.NO));
                        }
		    	LuceneUtils.addScientificNameToIndex(doc, keyValue[2], null);
                        //doc.add(new Field("scientificName", keyValue[2], Store.YES, Index.ANALYZED));


		    	
		    	//add relationships between concepts
//		    	addRels(is,keyValue[0], doc);
		    	
		    	//add to index
		    	iw.addDocument(doc, analyzer);
			}
	    	
		}
    	
    	//close taxonConcept stream
    	tr.close();
    	iw.close();
    	
    	long finish = System.currentTimeMillis();
    	logger.info(i+" indexed taxon concepts in: "+(((finish-start)/1000)/60)+" minutes, "+(((finish-start)/1000) % 60)+" seconds.");
	}

	/**
	 * @throws Exception
	 */
	public void loadPublications() throws Exception {
		
		File file = new File(PUB_INDEX_DIR);
    	if(file.exists()){
    		FileUtils.forceDelete(file);
    	}
    	FileUtils.forceMkdir(file);
    	int i=0;
		KeywordAnalyzer analyzer = new KeywordAnalyzer();
    	IndexWriter iw = new IndexWriter(file, analyzer, MaxFieldLength.UNLIMITED);
		try {
	    	long start = System.currentTimeMillis();
	    	//add the relationships
	    	TabReader tr = new TabReader(BIE_STAGING_DIR+"anbg/publications.txt");
	    	String[] keyValue = null;
	    	
	    	while((keyValue=tr.readNext())!=null){
	    		if(keyValue.length==5){
	    			i++;
			    	Document doc = new Document();
			    	doc.add(new Field("guid", keyValue[0], Store.YES, Index.ANALYZED));
			    	if(keyValue[1]!=null) doc.add(new Field("title", keyValue[1], Store.YES, Index.NO));
			    	if(keyValue[2]!=null) doc.add(new Field("authorship", keyValue[2], Store.YES, Index.NO));
			    	if(keyValue[3]!=null) doc.add(new Field("datePublished", keyValue[3], Store.YES, Index.NO));
			    	if(keyValue[4]!=null) doc.add(new Field("publicationType", keyValue[4], Store.YES, Index.NO));
			    	iw.addDocument(doc);
	    		}
			}
	    	tr.close();
			long finish = System.currentTimeMillis();
	    	logger.info(i+" loaded relationships, Time taken "+(((finish-start)/1000)/60)+" minutes, "+(((finish-start)/1000) % 60)+" seconds.");
		} finally {
			iw.close();
		}
	}
}
