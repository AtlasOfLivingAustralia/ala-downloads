-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: changelog.groovy
-- Ran at: 9/05/14 2:09 PM
-- Against: postgres@jdbc:postgresql://localhost/downloads-dev
-- Liquibase version: 2.0.5
-- *********************************************************************

-- Lock Database
-- Changeset release-3-1-download.groovy::1399605615878-1::bea18c (generated)::(Checksum: 3:d5072bdccacf61a76e1c3a5ad50a0327)
ALTER TABLE download ADD metadata_uri VARCHAR(255) NOT NULL;

--INSERT INTO databasechangelog (AUTHOR, COMMENTS, DATEEXECUTED, DESCRIPTION, EXECTYPE, FILENAME, ID, LIQUIBASE, MD5SUM, ORDEREXECUTED) VALUES ('bea18c (generated)', '', NOW(), 'Add Column', 'EXECUTED', 'release-3-1-download.groovy', '1399605615878-1', '2.0.5', '3:d5072bdccacf61a76e1c3a5ad50a0327', 20);

-- Changeset release-3-2-download.groovy::1399965256726-1::bea18c (generated)::(Checksum: 3:3cde3a421f53c5d89eaebd782fd8780e)
ALTER TABLE download ADD data_last_modified TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT 'Thu Jan 01 10:00:00 EST 1970';

--INSERT INTO databasechangelog (AUTHOR, COMMENTS, DATEEXECUTED, DESCRIPTION, EXECTYPE, FILENAME, ID, LIQUIBASE, MD5SUM, ORDEREXECUTED) VALUES ('bea18c (generated)', '', NOW(), 'Add Column', 'EXECUTED', 'release-3-2-download.groovy', '1399965256726-1', '2.0.5', '3:3cde3a421f53c5d89eaebd782fd8780e', 21);

-- Changeset release-3-3-download.groovy::1400033571377-1::bea18c (generated)::(Checksum: 3:94bd95d65a9ff13a9b76675e01600638)
ALTER TABLE download ADD data_etag VARCHAR(255) NOT NULL DEFAULT '';

--INSERT INTO databasechangelog (AUTHOR, COMMENTS, DATEEXECUTED, DESCRIPTION, EXECTYPE, FILENAME, ID, LIQUIBASE, MD5SUM, ORDEREXECUTED) VALUES ('bea18c (generated)', '', NOW(), 'Add Column', 'EXECUTED', 'release-3-3-download.groovy', '1400033571377-1', '2.0.5', '3:94bd95d65a9ff13a9b76675e01600638', 22);

-- Release Database Lock
