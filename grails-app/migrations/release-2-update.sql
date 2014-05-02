-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: changelog.groovy
-- Ran at: 10/04/14 5:58 PM
-- Against: postgres@jdbc:postgresql://localhost/downloads-dev
-- Liquibase version: 2.0.5
-- *********************************************************************

-- This script contains all the liquibase generated SQL that is required to go from a releaes 1 DB to a release 2 DB

-- Changeset release-2-1-lower_name.xml::1397109340541-1::bea18c::(Checksum: 3:5e9e363a4a65ae26aef4e7583346a33a)
ALTER TABLE project ADD lower_name VARCHAR(255) UNIQUE;

UPDATE project SET lower_name = lower(name);

ALTER TABLE project ALTER COLUMN  lower_name SET NOT NULL;

-- Changeset release-2-project.groovy::1397109340541-2::bea18c (generated)::(Checksum: 3:22fc7ef8493395f2e57e54b8f43ce186)
ALTER TABLE project ADD summary VARCHAR(255);

UPDATE project SET summary = description;

ALTER TABLE project ALTER COLUMN  summary SET NOT NULL;

-- Changeset release-2-project.groovy::1397109340541-3::bea18c (generated)::(Checksum: 3:779673fbf5aff695966766ecf2e12cc8)
-- Not needed because adding the lower_name column above with a UNIQUE constraint will create an implicit index.
--CREATE UNIQUE INDEX lower_name_uniq_1397109339970 ON project(lower_name);

-- Changeset release-2-project.groovy::1397109340541-4::bea18c::(Checksum: 3:c272209b73ebd0afc5fee3a88e8c7c53)
ALTER TABLE project ALTER COLUMN description TYPE VARCHAR(1000);

-- Changeset release-2-project-artifact.groovy::1397176775770-1::bea18c (generated)::(Checksum: 3:c2360d159a0c036c537d38471bf7db0b)
CREATE INDEX project_artifact_name_idx ON project_artifact(name);
