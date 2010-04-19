CREATE DATABASE anbg;
use anbg;

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
  published_citation TEXT DEFAULT NULL,
  published_in TEXT DEFAULT NULL,
  is_child_of varchar(255) DEFAULT NULL,
  is_synonym_for varchar(255) DEFAULT NULL,
  is_vernacular BOOLEAN DEFAULT FALSE,
  rank_string varchar(255) DEFAULT NULL,
  PRIMARY KEY (guid)
) ENGINE=MyISAM DEFAULT CHARSET=UTF8;
alter table taxon_concept add index ix_name_guid(taxon_name_guid);

CREATE TABLE common_name (
  guid char(100) NOT NULL,
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
alter table relationship add index idx_from (from_taxon);

CREATE TABLE publication (
  guid char(100) NOT NULL,
  title TEXT DEFAULT NULL,
  authorship TEXT DEFAULT NULL,  
  date_published varchar(255) DEFAULT NULL,  
  publication_type varchar(255) DEFAULT NULL,    
  PRIMARY KEY (guid)
) ENGINE=MyISAM DEFAULT CHARSET=UTF8;
