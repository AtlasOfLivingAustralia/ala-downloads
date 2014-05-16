databaseChangeLog = {

	changeSet(author: "bea18c (generated)", id: "1400033571377-1") {
		addColumn(tableName: "download") {
			column(defaultValue: "", name: "data_etag", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}
}
