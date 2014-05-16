databaseChangeLog = {

	changeSet(author: "bea18c (generated)", id: "1399965256726-1") {
		addColumn(tableName: "download") {
			column(defaultValue: "Thu Jan 01 10:00:00 EST 1970", name: "data_last_modified", type: "timestamp") {
				constraints(nullable: "false")
			}
		}
	}
}
