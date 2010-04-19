load data infile '/data/taxonConcepts.txt' into table taxon_concept fields OPTIONALLY ENCLOSED BY '"' (guid,taxon_name_guid,name,author,author_year,published_citation,published_in,is_child_of,is_synonym_for);
load data infile '/data/commonNames.txt' into table common_name fields OPTIONALLY ENCLOSED BY '"';
load data infile '/data/taxonNames.txt' into table taxon_name fields OPTIONALLY ENCLOSED BY '"';
load data infile '/data/publications.txt' into table publication fields OPTIONALLY ENCLOSED BY '"';
load data infile '/data/relationships.txt' into table relationship fields OPTIONALLY ENCLOSED BY '"' (to_taxon, from_taxon, relationship_type);