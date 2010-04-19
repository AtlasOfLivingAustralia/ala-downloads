package ala.infosource;

/**
 * Column headers to be used in tab file generation. These should tie
 * in with Dublin Core or Darwin Core terms. 
 * 
 * See: http://rs.tdwg.org/dwc/terms/index.htm
 * See: http://dublincore.org/documents/dcmi-terms/
 * 
 * @author Dave Martin (David.Martin@csiro.au)
 */
public interface ColumnHeaders {
	public final String URI = "URI";
	public final String KINGDOM = "kingdom";
	public final String PHYLUM = "phylum";
	public final String CLASS = "class";
	public final String ORDER = "order";
	public final String FAMILY = "family";
	public final String SUPERFAMILY = "superfamily";
	public final String SUBFAMILY = "subfamily";
	public final String GENUS = "genus";
	public final String SUBGENUS = "subgenus";
	public final String SPECIES = "species";
	public final String SUBSPECIES = "subspecies";
	public final String SPECIFIC_EPITHET = "specificEpithet";	
	public final String SCIENTIFIC_NAME = "scientificName";
	public final String COMMON_NAME = "commonName";
	public final String AUTHOR = "scientificNameAuthorship";
	public final String TAXON_RANK = "taxonRank";
	public final String PEST_STATUS = "pestStatus";
	public final String CONSERVATION_STATUS = "conservationStatus";
}