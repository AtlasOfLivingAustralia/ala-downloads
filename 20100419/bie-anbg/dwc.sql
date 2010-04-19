select guid, taxon_name_guid, name, is_child_of, rank_string, author, is_synonym_for 
from taxon_concept into outfile '/tmp/DarwinCore.txt' CHARACTER SET UTF8;