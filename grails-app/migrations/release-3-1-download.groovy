databaseChangeLog = {

	changeSet(author: "bea18c (generated)", id: "1399605615878-1") {
		addColumn(tableName: "download") {
			column(name: "metadata_uri", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}
}
