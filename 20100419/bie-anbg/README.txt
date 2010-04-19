bie-anbg
--------

This project utilities for generating a Darwin Core Archive from AFD and APC/APNI data.

For generating a Darwin Core Archive
-------------------------------------

1) Run the scala script AnbgParser - produces 4 tab files, taxonConcepts.txt. taxonNames.txt, relationships.txt and publications.txt
2) Copy to /data
3) Create DB using anbg.sql
4) Import the tab files using load.sql
5) Create the archive using dwc.sql
 