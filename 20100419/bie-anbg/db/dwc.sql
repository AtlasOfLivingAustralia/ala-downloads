-- update the concept, adding the rank string
update taxon_concept tc set tc.rank_string = (select tn.rank_string from taxon_name tn where tn.guid=tc.taxon_name_guid);

-- mark the vernacular concepts
update taxon_concept set is_vernacular=true where guid IN (select guid from common_name);

-- export file
select guid, taxon_name_guid, name, is_child_of, rank_string, author, is_synonym_for 
from taxon_concept where is_vernacular = false into outfile '/tmp/DarwinCore.txt' CHARACTER SET UTF8;
