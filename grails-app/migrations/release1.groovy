databaseChangeLog = {

	changeSet(author: "bea18c (generated)", id: "1397108656517-1") {
		createTable(tableName: "download") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "download_pkey")
			}

			column(name: "version", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "TIMESTAMP WITH TIME ZONE") {
				constraints(nullable: "false")
			}

			column(name: "description", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "file_size", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "file_uri", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "last_updated", type: "TIMESTAMP WITH TIME ZONE") {
				constraints(nullable: "false")
			}

			column(name: "mime_type", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "bea18c (generated)", id: "1397108656517-2") {
		createTable(tableName: "download_record_count") {
			column(name: "download_record_count_id", type: "int8")

			column(name: "record_count_id", type: "int8")
		}
	}

	changeSet(author: "bea18c (generated)", id: "1397108656517-3") {
		createTable(tableName: "log_event") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "log_event_pkey")
			}

			column(name: "version", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "comment", type: "VARCHAR(255)")

			column(name: "date_created", type: "TIMESTAMP WITH TIME ZONE") {
				constraints(nullable: "false")
			}

			column(name: "event_type_id", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "last_updated", type: "TIMESTAMP WITH TIME ZONE") {
				constraints(nullable: "false")
			}

			column(name: "month", type: "VARCHAR(255)")

			column(name: "reason_type_id", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "source_type_id", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "source_url", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "user_email", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "userip", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "bea18c (generated)", id: "1397108656517-4") {
		createTable(tableName: "log_event_record_counts") {
			column(name: "record_counts", type: "int8")

			column(name: "record_counts_idx", type: "VARCHAR(255)")

			column(name: "record_counts_elt", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "bea18c (generated)", id: "1397108656517-5") {
		createTable(tableName: "project") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "project_pkey")
			}

			column(name: "version", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "description", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "guid", type: "VARCHAR(255)")

			column(name: "name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "bea18c (generated)", id: "1397108656517-6") {
		createTable(tableName: "project_artifact") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "project_artifact_pkey")
			}

			column(name: "version", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "artifact_guid", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "TIMESTAMP WITH TIME ZONE") {
				constraints(nullable: "false")
			}

			column(name: "deprecated", type: "bool")

			column(name: "description", type: "VARCHAR(1000)") {
				constraints(nullable: "false")
			}

			column(name: "download_count", type: "int8")

			column(name: "file_size", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "last_updated", type: "TIMESTAMP WITH TIME ZONE") {
				constraints(nullable: "false")
			}

			column(name: "md5hash", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "mime_type", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "original_filename", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "project_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "sha1hash", type: "VARCHAR(255)")

			column(name: "uploaded_by", type: "VARCHAR(255)")

			column(name: "summary", type: "VARCHAR(100)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "bea18c (generated)", id: "1397108656517-7") {
		createTable(tableName: "project_artifact_tags") {
			column(name: "project_artifact_id", type: "int8")

			column(name: "tags_string", type: "VARCHAR(255)")

			column(name: "tags_idx", type: "int4")
		}
	}

	changeSet(author: "bea18c (generated)", id: "1397108656517-8") {
		createTable(tableName: "record_count") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "record_count_pkey")
			}

			column(name: "version", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "code", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "records", type: "int4") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "bea18c (generated)", id: "1397108656517-9") {
		addUniqueConstraint(columnNames: "file_uri", constraintName: "download_file_uri_key", deferrable: "false", disabled: "false", initiallyDeferred: "false", tableName: "download")
	}

	changeSet(author: "bea18c (generated)", id: "1397108656517-10") {
		addUniqueConstraint(columnNames: "artifact_guid", constraintName: "project_artifact_artifact_guid_key", deferrable: "false", disabled: "false", initiallyDeferred: "false", tableName: "project_artifact")
	}

	changeSet(author: "bea18c (generated)", id: "1397108656517-14") {
		createSequence(schemaName: "public", sequenceName: "hibernate_sequence")
	}

	changeSet(author: "bea18c (generated)", id: "1397108656517-11") {
		addForeignKeyConstraint(baseColumnNames: "download_record_count_id", baseTableName: "download_record_count", baseTableSchemaName: "public", constraintName: "fk8c7e9df8a85a82a9", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "download", referencedTableSchemaName: "public", referencesUniqueColumn: "false")
	}

	changeSet(author: "bea18c (generated)", id: "1397108656517-12") {
		addForeignKeyConstraint(baseColumnNames: "record_count_id", baseTableName: "download_record_count", baseTableSchemaName: "public", constraintName: "fk8c7e9df89f2fa7f8", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "record_count", referencedTableSchemaName: "public", referencesUniqueColumn: "false")
	}

	changeSet(author: "bea18c (generated)", id: "1397108656517-13") {
		addForeignKeyConstraint(baseColumnNames: "project_id", baseTableName: "project_artifact", baseTableSchemaName: "public", constraintName: "fk6fe7583824097ddb", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "project", referencedTableSchemaName: "public", referencesUniqueColumn: "false")
	}
}
