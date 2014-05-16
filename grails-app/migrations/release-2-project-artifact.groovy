databaseChangeLog = {

	changeSet(author: "bea18c (generated)", id: "1397176775770-1") {
		createIndex(indexName: "project_artifact_name_idx", tableName: "project_artifact") {
			column(name: "name")
		}
	}
}
