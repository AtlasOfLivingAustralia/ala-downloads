DROP TABLE taxon_concept;
DROP TABLE taxon_name;
DROP TABLE common_name;
DROP TABLE relationship;
DROP TABLE publication;

CREATE TABLE taxon_concept (
  guid char(100) NOT NULL,
  taxon_name_guid varchar(80) DEFAULT NULL,
  name TEXT DEFAULT NULL,
  author varchar(255) DEFAULT NULL,
  author_year varchar(255) DEFAULT NULL,
  is_child_of varchar(255) DEFAULT NULL,  
  is_synonym_for varchar(255) DEFAULT NULL,
  is_vernacular_for varchar(255) DEFAULT NULL,
  PRIMARY KEY (guid)
) ENGINE=MyISAM DEFAULT CHARSET=UTF8;
alter table taxon_concept add index ix_name_guid(taxon_name_guid);

CREATE TABLE common_name (
  guid char(100) NOT NULL,
  common_name varchar(255) DEFAULT NULL,
  rank_string varchar(255) DEFAULT NULL,
  PRIMARY KEY (guid)
) ENGINE=MyISAM DEFAULT CHARSET=UTF8;

CREATE TABLE taxon_name (
  guid char(100) NOT NULL,
  scientific_name varchar(255) DEFAULT NULL,
  canonical varchar(255) DEFAULT NULL,
  authorship varchar(255) DEFAULT NULL,
  rank_string varchar(255) DEFAULT NULL,
  publication_guid varchar(255) DEFAULT NULL,  
  nomenclaturalCode varchar(255) DEFAULT NULL,
  typificationString TEXT DEFAULT NULL,
  PRIMARY KEY (guid)
) ENGINE=MyISAM DEFAULT CHARSET=UTF8;

CREATE TABLE relationship (
  id int(11) NOT NULL auto_increment,
  from_taxon char(100) NOT NULL,
  to_taxon char(100) NOT NULL,
  relationship_type char(100) NOT NULL,  
  PRIMARY KEY (id)
) ENGINE=MyISAM DEFAULT CHARSET=UTF8;

CREATE TABLE publication (
  guid char(100) NOT NULL,
  title TEXT DEFAULT NULL,
  authorship TEXT DEFAULT NULL,  
  date_published varchar(255) DEFAULT NULL,  
  publication_type varchar(255) DEFAULT NULL,    
  PRIMARY KEY (guid)
) ENGINE=MyISAM DEFAULT CHARSET=UTF8;

load data infile '/data/publications.txt' into table publication fields OPTIONALLY ENCLOSED BY '"';
load data infile '/data/taxonConcepts.txt' into table taxon_concept fields OPTIONALLY ENCLOSED BY '"';
load data infile '/data/taxonNames.txt' into table taxon_name fields OPTIONALLY ENCLOSED BY '"';
load data infile '/data/commonNames.txt' into table common_name fields OPTIONALLY ENCLOSED BY '"';
load data infile '/data/relationships.txt' into table relationship fields OPTIONALLY ENCLOSED BY '"' (to_taxon, from_taxon, relationship_type);

alter table taxon_concept add is_vernacular BOOLEAN default false;
alter table taxon_concept add accepted_guid char(100) default null;
alter table taxon_concept add parent_guid char(100) default null;
alter table taxon_concept add rank_string char(100) default null;

-- vernacular concept (from_taxon) to scientific (to_taxon)

-- synonym concept (from_taxon) to accepted (to_taxon)
-- sets a accepted guid on a synonym
alter table relationship add index idx_from (from_taxon);
alter table taxon_concept add index idx_tc_guid (guid);

-- update the parent concept - note the *child* is the toTaxon in the relationship table
update taxon_concept tc set tc.parent_guid = 
(select from_taxon from relationship where to_taxon=tc.guid and relationship_type='http://rs.tdwg.org/ontology/voc/TaxonConcept#IsChildTaxonOf');

-- update the accepted concept
update taxon_concept tc set tc.rank_string = (select tn.rankString from taxon_name tn where tn.guid=tc.taxon_name_guid);

