package org.ala.hbase;

import au.org.ala.checklist.lucene.SearchResultException;
import au.org.ala.data.model.LinnaeanRankClassification;
import au.org.ala.data.util.RankType;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.ala.dao.InfoSourceDAO;
import org.ala.dao.TaxonConceptDao;
import org.ala.dao.TaxonConceptSHDaoImpl;
import org.ala.lucene.CreateLoadingIndex;
import org.ala.model.Classification;
import org.ala.model.Publication;
import org.ala.model.TaxonConcept;
import org.ala.model.TaxonName;
import org.ala.util.LoadUtils;
import org.ala.util.SpringUtils;
import org.ala.util.TabReader;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 *
 * Creates a ALA classification NUB by:
 *
 * <ol>
 *  <li>Start with the NSL TaxonNames</li>
 *      <ol>
 *          <li>Obtain a list of TaxonConcepts for the name</li>
 *          <li>Select the "Accepted" concept either by obtaining either the NSL "accepted" concept or locating a concept that has a parent</li>
 *          <li>Mark all other concepts as "Associated" concepts for this one</li>
 *          <li>Check to see if the concept is a synonym or congruent to one of the other concept</li>
 *          <li>Depending on the outcome of the above checks insert it as a concept OR synonym/congruent to an existing concept</li>
 *          <li>Concepts that are missing parents are noted for possible CoL completion</li>
 *      </ol>
 *  <li>Use CoL to fill missing terms</li>
 *  <li>Generate the classification and left right values</li>
 * </ol>
 *
 * @author Natasha Carter
 */
@Component("anbgNameConstructor")
public class ANBGNameConstructor {

    protected static Logger logger = Logger.getLogger(ANBGNameConstructor.class);
    @Inject
    protected InfoSourceDAO infoSourceDAO;
    @Inject
    protected TaxonConceptDao taxonConceptDao;
    public static final String animaliaLsid = "urn:lsid:biodiversity.org.au:afd.taxon:4647863b-760d-4b59-aaa1-502c8cdf8d3c";
    
    private static final String TAXON_CONCEPTS = "/data/bie-staging/anbg/taxonConcepts.txt";
    public static final String TAXON_NAMES = "/data/bie-staging/anbg/taxonNames.txt";//-small.txt";
    private static final String RELATIONSHIPS = "/data/bie-staging/anbg/relationships.txt";
    private static final String PUBLICATIONS = "/data/bie-staging/anbg/publications.txt";
    private static final String SIMILAR_NAME = "/data/tmp/similarName.txt";
    private static final String MISSING_PARENT = "/data/tmp/missingParent.txt";
    private static final String CLASS_START = "/data/tmp/classificationStart.txt";
    private static final String COL_NAMES ="/data/checklistbank/rawdata/col2010/taxa.txt";
    LoadUtils loadUtils ;
    //caches the CoL concepts for reuse...
    Map<String, TaxonConcept> colConcepts;
    List<TaxonConcept> topLevel;//CoL concepts without parents
    

    public static void main(String[] args) throws Exception {
       
        logger.info("Starting ANBG load....");
        long start = System.currentTimeMillis();
        ApplicationContext context = SpringUtils.getContext();
        ANBGNameConstructor loader = (ANBGNameConstructor) context.getBean(ANBGNameConstructor.class);
        loader.init();
        loader.construct();
       //CoL index query: name:equisetopsida OR id:2251389 OR id:2242856
      
        //loader.generateEquivalentCoLList();
        loader.fillMissingClassification();
        loader.generateClassification();
        //create index
        loader.taxonConceptDao.createIndex();
        long finish = System.currentTimeMillis();
        logger.info("Data loaded in: " + ((finish - start) / 60000) + " minutes.");
        
        System.exit(1);
    }
    public void init() throws Exception{
         loadUtils = new LoadUtils();
         colConcepts = new HashMap<String,TaxonConcept>();
         topLevel = new ArrayList<TaxonConcept>();
    }
    /**
     * Processes a single supplied record. If the supplied name has associated taxon concepts
     * this method will insert either an "accepted", synonym or congruent taxon concept.
     * @param record
     * @param pout
     * @param sout
     * @throws Exception
     */
    public void processRecord(String[] record, Writer pout, Writer sout) throws Exception{
        if(loadUtils == null)
            loadUtils = new LoadUtils();
        if(record.length >=10){
                String namesGuid = record[0];
                //get all the taxon concepts for this name...
                List<TaxonConcept> tcs = loadUtils.getByNameGuid(namesGuid, 10);
                //search for the accepted concept
                TaxonConcept accepted = loadUtils.getAcceptedConcept(tcs);
                if(accepted != null){
                    String guid = accepted.getGuid();
                    TaxonName tn = loadUtils.getNameByGuid(namesGuid);


                    //check to see if this concept should be marked as congruent to or a synonym of another...
                    String cguid = loadUtils.isCongruentConcept(guid);
                    if(cguid != null && !cguid.equals(guid)){
                        taxonConceptDao.addIsCongruentTo(cguid, accepted);
                        //TODO work out what to do with remaining TC if there are more than 1 associated with this name
                    }
                    else{
                        //check see if synonym
                        String sguid = loadUtils.isSynonymFor(guid);
                        if(sguid != null && !sguid.equals(guid)){
                            taxonConceptDao.addSynonym(sguid, accepted);
                            //TODO work out what to do with remaining TC if there are more than 1 associated with this name
                        } else {
                            //check to see if it is included in another taxon concept
                            String iguid = loadUtils.isIncluded(guid);
                            if(iguid !=null){
                                taxonConceptDao.addIncluded(guid, accepted);
                            }
                            else{
                                //Hack for the Plantae TC
                                if(guid.equals("urn:lsid:biodiversity.org.au:apni.taxon:0")){
                                    accepted.setNameString("Plantae " + accepted.getNameString());
                                    tn.setNameComplete("Plantae");
                                }
                                //we have a real taxon concept!!
                                //set the rank
                                RankType rank = RankType.getForName(tn.getRankString());
                                if(rank != null){
                                    accepted.setRankID(rank.getId());
                                    accepted.setRankString(rank.getRank());
                                }
                                //set the parent for the accepted concept
                                accepted.setParentGuid(loadUtils.getParent(guid));
                                //set the children for the concept...
                                //This is the most time consuming step
                                //accepted.setChildrenGuid(loadUtils.getChildren(guid));

                                //add all the other taxon concept with the same taxonName as "associated concepts"
                                tcs.remove(accepted);
                                if(!tcs.isEmpty()){
                                    taxonConceptDao.addAssociatedTaxa(guid, tcs);
                                }
                                for(TaxonConcept atc : tcs){
                                    //add all the children for the associated concepts - also time consuming...
                                    //accepted.getChildrenGuid().addAll(loadUtils.getChildren(atc.getGuid()));
                                    accepted.getChildrenGuid().addAll(atc.getChildrenGuid());
                                }
                                
                                //add it
                                taxonConceptDao.create(accepted);


                                //add the name...
                                taxonConceptDao.addTaxonName(guid, tn);



                                //if the concept does not have a parent write it to file...
                                if (accepted.getParentGuid() == null) {
                                    pout.write("\""+guid + "\"\t\"" + accepted.getNameString() + "\"\t\"" + tn.getRankString() + "\"\n");
                                }


                                //report similar names...
                                List<TaxonName> similarNames = loadUtils.getSimilarNames(tn, 10);
                                if(similarNames!= null && !similarNames.isEmpty()){
                                    //report these names and in the future detemine whether or not TC for these names need to be merged with the current TC...
                                    for(TaxonName name : similarNames){
                                        writeName(sout,tn, false);
                                        writeName(sout, name, true);
                                    }
                                    sout.flush();
                                }

                                
                            }
                        }
                    }
                }
                else{
                    logger.warn("No Taxon Concepts for " + namesGuid +"("+record[1]+ ")");
                }

            }


    }
    /**
     * First step in constructing the
     * @throws Exception
     */
    public void construct() throws Exception{
        logger.info("Constructing....");

        
        TabReader tr = new TabReader(TAXON_NAMES);
        String[] record = null;
        long start = System.currentTimeMillis();
        Writer out = new OutputStreamWriter(new FileOutputStream(SIMILAR_NAME), "UTF-8");
        Writer pout = new OutputStreamWriter(new FileOutputStream(MISSING_PARENT), "UTF-8");
        out.write("USED NAME LSID\tUSED SCIENTIFIC NAME\tUSED AUTHOR\tUSED UNINOMIAL\tUSED GENUS\tUSED SPECIFIC EPITHET\tUSED INFRASPECIFIC EPITHET\tNAME LSID\tSCIENTIFIC NAME\tAUTHOR\tUNINOMIAL\tGENUS\tSPECIFIC EPITHET\tINFRASPECIFIC EPITHET\n");
        //use the names file as a basis for getting the Taxon Concepts  to be constructed
        int i =0;
        int numberAdded = 0;
        while ((record = tr.readNext()) != null) {
            i++;
            processRecord(record,pout,out);

        }
        out.flush();
        out.close();
        pout.flush();
        pout.close();
            	long finish = System.currentTimeMillis();
	    	logger.info(i+" loaded taxon concepts in: "+(((finish-start)/1000)/60)+" minutes.");
    }

    /**
     * Attempts to match the COL name to an AFD or APNI name
     *
     * TODO consider the homonyms during the matching process...
     * @throws Exception
     */
    public void generateEquivalentCoLList() throws Exception{
        logger.info("Starting to generate a list of equivalent COL to ANBG lists ...");
        TabReader tr = new TabReader(COL_NAMES,false);
        String[] record = null;
        Writer out = new OutputStreamWriter(new FileOutputStream("/data/tmp/col_anbg_mapping.txt"), "UTF-8");
        long start = System.currentTimeMillis();
        int i =0;
        while ((record = tr.readNext()) != null) {
            i++;
            String uninomial =null,genus=null,species=null,infra=null;
            if(record.length>=5){
                String srank = record[4];
                RankType rank = RankType.getForStrRank(srank);
                if(rank == null)
                    System.out.println("Can't find rank : "+srank);
                if(rank.getId()<=RankType.GENUS.getId()){
                    //we are only interested in populating the uninomial
                    uninomial = record[2];
                }
                else{
                    genus = record.length>8?record[8]:null;
                    species = record.length>9?record[9]:null;
                    infra = record.length>10?record[10]:null;
                }
                TaxonName tn = loadUtils.getTaxonName(uninomial, genus, species, infra, rank);
                if(tn !=null){
                    //System.out.println(record[0]+"\t"+record[1] +"\t" +record[2] +"\t" +record[4] + "\t"+tn.getGuid()+"\t"+tn.getNameComplete());
                    //get the accepted taxon concept for the name that was returned
                    TaxonConcept tc = loadUtils.getAcceptedConcept(loadUtils.getByNameGuid(tn.getGuid(), 100));
                    if(tc != null)
                        out.write(record[0]+"\t"+record[1] +"\t" +record[2] +"\t" +record[4] + "\t"+tn.getGuid()+"\t"+tn.getNameComplete()+"\t"+tn.getRankString()+"\t"+tc.getGuid()+"\t"+tc.getNameString());
                    else{
                        System.out.println(record[0]+"\t"+record[1] +"\t" +record[2] +"\t" +record[4] + "\t"+tn.getGuid()+"\t"+tn.getNameComplete()+"\t"+tn.getRankString());
                    }

                }
            }
        }
        out.flush();
        out.close();
    }
    /**
     * Attempts to fill in the missing classification with values from CoL
     *
     * When a genus has no parents and it is a possible homonym it is difficult to resolve
     * these homonyms because CoL does not include authority for Genus.
     *
     * @throws Exception
     */
    public void fillMissingClassification() throws Exception{
        //init file that will be used to write the classification start points
        Writer out =new OutputStreamWriter(new FileOutputStream(CLASS_START), "UTF-8");
        //attempt to add missing parts of the classification using the CoL
                TaxonConceptSHDaoImpl tcDAOImpl = null;
               
        if(taxonConceptDao instanceof TaxonConceptSHDaoImpl)
            tcDAOImpl= (TaxonConceptSHDaoImpl)taxonConceptDao;
        //As a first step use the missing parents list to identify area's of missing classification.
        logger.info("Starting to fill in missing information using CoL");
        TabReader tr = new TabReader(MISSING_PARENT);
        String[] record = null;
        long start = System.currentTimeMillis();
     
        int i =0,badname=0,badrank=0,matched=0,supra=0,homo=0;
         while ((record = tr.readNext()) != null) {
            i++;
            boolean checkHomo =false;
             boolean locatedName = false;
             String reason="";
            if(record.length>=3){

                //ignore bad names
                String name = record[1];
                if(name.startsWith("??") || name.startsWith("Unplaced")){
                    //logger.info("Ignoring bad name: "+ name);
                    badname++;
                    reason ="Bad name";
                }
                else{
                    RankType rank = RankType.getForName(record[2]);
                    if(rank == null){
                        //attempt to get the rank type based on the raw
                        rank = RankType.getForStrRank(record[2]);
                    }

                    if(rank != null){
                        switch(rank){
//                            case SUPRAGENERICNAME:
//                                //don't process
//                                supra++;
//                                break;
//                            case GENUS:FAMILY:
//                                break;
//                            case SPECIES:
//                                break;
                            case GENUS:
                                checkHomo =true;
                            default:
                                //search for the name
                                
                                TaxonConcept tc = loadUtils.getByGuid(record[0], 1);
                               
                                TaxonName tn = loadUtils.getNameByGuid(tc.getNameGuid());
                                if(tn != null){
                                    TaxonConcept colTc = loadUtils.getCoL(tn);
                                    if(colTc != null){
                                        //System.out.println("Located match : " + colTc);
                                        if(checkHomo){
                                            try{

                                                tcDAOImpl.cbIdxSearcher.resolveIRMNGHomonym(new LinnaeanRankClassification(null, colTc.getNameString()), RankType.GENUS);
                                            }
                                            catch(SearchResultException e){
                                                //stop the processing
                                                homo++;
                                                reason = "Homonym Detected";
                                                break;
                                            }
                                        }
                                        matched++;
                                       
                                        //System.out.println("Matched " + name + " to CoL: " + colTc.getId() + " : " + colTc.getGuid() + " " + colTc.getNameString());
                                        if(colTc.getParentId()!=null){
                                            boolean stop = false;
                                            TaxonConcept anbgTc = tc;
                                            while(!stop){
                                                colTc = processCOLName(colTc, anbgTc);
                                                anbgTc = null;
                                                stop = colTc ==null || colTc.getParentId() == null || colTc.getGuid().startsWith("urn:lsid:biodiversity.org.au");
                                            }
                                            
                                            

//                                            TaxonConcept parentToAddTo =processCOLName(colTc);
//                                            if(parentToAddTo != null){
//                                                //if the parent was an ANBG term stop here
//                                                System.out.println("Added : " + name + "("+record[0]+") to parent " + parentToAddTo.getNameString() + " (" + parentToAddTo.getGuid()+")");
//
//                                            }
                                            locatedName=true;
                                        }
//                                        else{
//                                            //add it to the list that must be used to generate left and rights...
//                                        }
                                    }
                                    else{
                                        //if the rank is species or below check to see if we can match on species etc
                                        if(rank.getId()>RankType.GENUS.getId() ||(rank == RankType.SUPRAGENERICNAME && tn.getGenus() != null && tn.getSpecificEpithet() != null)){
                                            String genus = tn.getGenus();
                                            String species = tn.getInfraspecificEpithet() != null ?tn.getSpecificEpithet():null;
                                            RankType r = RankType.SPECIES;
                                            if(species == null){
                                                //check for homonym
                                                r = RankType.GENUS;
                                                try{

                                                tcDAOImpl.cbIdxSearcher.resolveIRMNGHomonym(new LinnaeanRankClassification(null, genus), RankType.GENUS);
                                            }
                                            catch(SearchResultException e){
                                                //stop the processing
                                                homo++;
                                                reason = "Homonym detected in broken down name";
                                                break;
                                            }
                                            }
                                            //now search in the ANBG index
                                            TaxonName parentName =loadUtils.getTaxonName(null, genus, species, null, r);
                                            if(parentName != null){
                                                //found our new parent
                                                //TaxonConcept parentConcept = loadUtils.getAcceptedConcept(loadUtils.getByNameGuid(parentName.getGuid(), 100));
                                                //get the stored version so exiting children are not modified...
                                                TaxonConcept parentConcept = getStoredConceptForName(parentName);
                                                if(parentConcept != null){
                                                    parentConcept.getChildrenGuid().add(tc.getGuid());
                                                    taxonConceptDao.update(tc);
                                                    locatedName=true;
                                                }
                                                else
                                                    reason = "Unable to find concept in storage";
                                            }
                                            else {
                                                //serach for the CoL term
                                                TaxonName tempTn = new TaxonName();
                                                tempTn.genus = genus;
                                                tempTn.specificEpithet = species;
                                                tempTn.rankString = r.getRank();
                                                TaxonConcept parentConcept = loadUtils.getCoL(tempTn);
                                                if (parentConcept != null) {
                                                    tempTn = loadUtils.getCoLName(Integer.toString(parentConcept.getId()));
                                                    parentConcept.getChildrenGuid().add(tc.getGuid());
                                                    
                                                    if(parentConcept.getParentId() != null){
                                                        TaxonConcept pptc = loadUtils.getCoLByID(parentConcept.getParentId());
                                                        parentConcept.setParentGuid(pptc.getGuid());
                                                    }
                                                    taxonConceptDao.create(parentConcept);
                                                    taxonConceptDao.addTaxonName(parentConcept.getGuid(), tempTn);
                                                    colConcepts.put(Integer.toString(parentConcept.getId()), parentConcept);
                                                    boolean stop = false;
                                                    TaxonConcept anbgTc = tc;
                                                    while (!stop) {
                                                        parentConcept = processCOLName(parentConcept, anbgTc);
                                                        anbgTc = null;
                                                        stop = colTc == null || colTc.getParentId() == null || colTc.getGuid().startsWith("urn:lsid:biodiversity.org.au");
                                                    }
                                                    locatedName=true;
                                                }
                                                else
                                                    reason = "Unable to find disected parent";
                                            }
                                        }
                                        else{
                                            reason = "Unable to locate a matching term in CoL";
                                        }
                                    }
                                }
                                break;

                        }
                    }
                    else{
                        badrank++;
                        reason = "No Rank";
                    }
                        //logger.info("Unable to process unknown rank: " + record[0] + "\t" + record[1] + "\t" +record[2]);
                }
            }
             //If AFD name add it to "Animalia"
             //Unfortunately there is no one default kingdom for APNI
             if (! locatedName && record[0].startsWith("urn:lsid:biodiversity.org.au:afd.taxon") && !record[0].equals(animaliaLsid)) {
                 TaxonConcept animaliaConcept = taxonConceptDao.getByGuid(animaliaLsid);
                 animaliaConcept.getChildrenGuid().add(record[0]);
                 taxonConceptDao.update(animaliaConcept);
                 logger.warn("Adding " + record[0] + " - " + record[1] + " to default kingdom Animalia. " + reason);
                 locatedName = true;
             }
             if (!locatedName)
                 out.write(StringUtils.join(record,"\t")+"\t"+reason + "\n");
        }
        //add all the toplevel terms to the file
        for(TaxonConcept ttc : topLevel)
            out.write(ttc.getGuid() + "\t" + ttc.getNameString() + "\t\n");
        //update all the concepts in the colConcepts map
        for(TaxonConcept ctc: colConcepts.values())
            taxonConceptDao.update(ctc);


        out.write(colConcepts.toString()+"\n");
        out.flush();
        out.close();
        System.out.println("Processed " + i + " missing parents. " + badname + " - bad names. " + badrank + " - badrank." + matched + " - matched COL. " + supra + " suprageneric rank. " + homo + " homonyms..." );

        //ignore the Kingdom rank and blacklisted names and "null" rank

        //if the rank is a species attempt to parse out genus (check with IRMNG to see if the names is a

        //Work out what to do with the additional species etc not covered by AFD and APNI....
    }
    private TaxonConcept processCOLName(TaxonConcept existingName, TaxonConcept anbgConcept) throws Exception{
        //attempt to get the parent of the existing name
        if(existingName.getParentId() != null){
            //attempt to locate the parent in the colConcepts
            //if it is there it means that the concept has already been added and can be used...
            TaxonConcept parent = colConcepts.get(existingName.getParentId());
            if(parent == null){
                parent = loadUtils.getCoLByID(existingName.getParentId());
                if(parent == null)
                    System.out.println("Unable to find parent: " + existingName.getParentId());
                TaxonName ctn = loadUtils.getCoLName(Integer.toString(parent.getId()));
                if(ctn != null){
                    //attempt to locate a ANBG equivalent name
                    String uninomial = ctn.getGenus() == null ? ctn.getNameComplete():null;
                    RankType rank = RankType.getForStrRank(ctn.getRankString());
                    TaxonName tn =loadUtils.getTaxonName(uninomial, ctn.genus, ctn.specificEpithet, ctn.infraspecificEpithet,rank);
                    //get the stored version so that all the children exist
                    TaxonConcept  accepted = getStoredConceptForName(tn);//taxonConceptDao.getByGuid(accepted.getGuid())
                    if(accepted != null){
                        //located an equivalent ANBG name
                        System.out.println("COL: " + parent.getNameString() + " ANBG: " + tn.getNameComplete());
                        
                        if(accepted != null){
                        if(anbgConcept != null)
                            accepted.getChildrenGuid().add(anbgConcept.getGuid());
                        else
                            accepted.getChildrenGuid().add(existingName.getGuid());
                        taxonConceptDao.update(accepted);
                        return accepted;
                        }
                        else
                            System.out.println("Matched to name without concept " + tn);
                    }
                    else{
                        //we need to add this as a CoL term
                        colConcepts.put(existingName.getParentId(),parent);
                        if(anbgConcept != null)
                            parent.getChildrenGuid().add(anbgConcept.getGuid());
                        else
                            parent.getChildrenGuid().add(existingName.getGuid());
                        
                        if(parent.getParentId() != null){
                            TaxonConcept pptc = loadUtils.getCoLByID(parent.getParentId());
                            parent.setParentGuid(pptc.getGuid());
                        }
                        taxonConceptDao.create(parent);
                        taxonConceptDao.addTaxonName(parent.getGuid(), ctn);
                        if(parent.getParentId() == null)
                            topLevel.add(parent);
                    }
                }
            }
            else{
                //the name has already been added place the supplied concept as a child
                parent.getChildrenGuid().add(existingName.getGuid());
                return parent;
            }
        }
        return null;

    }
    private TaxonConcept getStoredConceptForName(TaxonName tn) throws Exception{
        TaxonConcept tc = null;
        if(tn != null){
            List<TaxonConcept> concepts = loadUtils.getByNameGuid(tn.getGuid(), 100);
            for(TaxonConcept concept: concepts){
                tc = taxonConceptDao.getByGuid(concept.getGuid());
                if(tc != null)
                    return tc;
            }
        }
        return tc;
    }

    public void generateClassification() throws Exception{
        //starting at kigdom rank queyr for all taxon concept at each rank
        //assign the left value and construct the classificatin to be added at each level

        logger.info("Generating....");

        TabReader tr = new TabReader(CLASS_START, false);
        String[] record = null;
        long start = System.currentTimeMillis();
        int i =0;
        int previous =0,before=0;
        while ((record = tr.readNext()) != null) {
            i++;

            String guid = record[0];
            
            Classification cl = new Classification();
            before = previous;
            previous=processLevel(guid,null, previous+1, cl);
            System.out.println(">>>>>guid: " + guid+ " left " + before + " right " + (previous));

        }
        long finish = System.currentTimeMillis();
	    	logger.info("Finished generating the classification in: "+(((finish-start)/1000)/60)+" minutes.");
        
    }

    public int processLevel(String guid, String parentguid, int previous, Classification cl) throws Exception{
        //get this concept
        TaxonConcept tc = taxonConceptDao.getByGuid(guid);
        if(tc != null){
//            System.out.println("Processing : " + tc);
        tc.setLeft(previous);
        TaxonName tn = taxonConceptDao.getTaxonNameFor(guid);
        cl.setRank(tn.getRankString(), tc.getNameString(), guid);
        //set the classification
        taxonConceptDao.addClassification(guid, cl);
        Classification cl2 = initClassification(cl);
        //get the children of this concept from the index...
        Set<String> children =tc.getChildrenGuid();//loadUtils.getChildren(guid);
        //remove self as child
        children.remove(tc.getGuid());
        for(String child : children){
            //System.out.println("parent: "+guid + " previous " + previous);
            previous = processLevel(child, guid, previous+1,cl2);
        }
        //add the right value
        tc.setRight(previous+1);
        if(parentguid != null)
            tc.setParentGuid(parentguid);
        //update the taxon concept
        taxonConceptDao.update(tc);
        System.out.println(guid + "- LEFT: " + tc.getLeft() + " RIGHT: " + tc.getRight());
        return previous+1;
        }
        return previous-1;

    }

    private Classification initClassification(Classification cl){
        Classification clnew = new Classification();
        clnew.setKingdom(cl.getKingdom());
        clnew.setKingdomGuid(cl.getKingdomGuid());
        clnew.setPhylum(cl.getPhylum());
        clnew.setPhylumGuid(cl.getPhylumGuid());
        clnew.setClazz(cl.getClazz());
        clnew.setClazzGuid(cl.getClazzGuid());
        clnew.setOrder(cl.getOrder());
        clnew.setOrderGuid(cl.getOrderGuid());
        clnew.setSuperfamily(cl.getSuperfamily());
        clnew.setSuperfamilyGuid(cl.getSuperfamilyGuid());
        clnew.setFamily(cl.getFamily());
        clnew.setFamilyGuid(cl.getFamilyGuid());
        clnew.setSubfamily(cl.getSubfamily());
        clnew.setSubfamilyGuid(cl.getSubfamilyGuid());
        clnew.setGenus(cl.getGenus());
        clnew.setSpecies(cl.getSpecies());
        clnew.setSpeciesGuid(cl.getSpeciesGuid());
        clnew.setSubspecies(cl.getSubspecies());
        clnew.setSubspeciesGuid(cl.getSubspeciesGuid());
        return clnew;
    }


    private void writeName(Writer out, TaxonName tn, boolean newline) throws Exception{
        out.write(tn.guid + "\t");
        out.write(StringUtils.defaultString(tn.nameComplete) + "\t");
        out.write(StringUtils.defaultString(tn.authorship) + "\t");
        out.write(StringUtils.defaultString(tn.uninomial) + "\t");
        out.write(StringUtils.defaultString(tn.genus) + "\t");
        out.write(StringUtils.defaultString(tn.specificEpithet) + "\t");
        out.write(StringUtils.defaultString(tn.infraspecificEpithet));
        if(newline)
            out.write("\n");
        else
            out.write('\t');
    }



    public void eval() throws Exception {
        //First up test how many TC have multiple possible TN based on the uninomial, genus, specificEpithet and infraspecificEpithet
        //Then create a test load for the constructing the hierarchy
        //Keep track of which taxon concept have no parent these ones may need their hierarchy padded with CoL concepts...
        //Then we need to work out where the missing species etc are...


        logger.info("Starting load of taxon concepts...");

        LoadUtils loadUtils = new LoadUtils();
        TabReader tr = new TabReader(TAXON_CONCEPTS);
        String[] record = null;
        long start = System.currentTimeMillis();
        int i = 0;
        int j = 0;

        while ((record = tr.readNext()) != null) {
            i++;
            //get the name guid and do some evaluation...
            if (record.length >= 3) {
                String nameGuid = record[1];
                if (nameGuid != null && nameGuid.length() > 4) {
                    TaxonName tn = loadUtils.getNameByGuid(nameGuid);
                    List<TaxonName> tns = loadUtils.getSimilarNames(tn, 10);
                    if(!tns.isEmpty()){
                        System.out.println(">>>>>>>>>>> Possible repeated name: " + tns + "\t" + tn + "\t" +i);
                    }

                } else {
                    System.out.println("------ No Name GUID for: " + record[0]);
                }

            }
        }
    }
}
