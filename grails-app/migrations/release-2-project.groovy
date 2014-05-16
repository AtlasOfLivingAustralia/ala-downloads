databaseChangeLog = {

    // use XML because the column.value* attributes in the Groovy DSL are broken and I need to update
    // existing records before applying a not null constraint
    include file: 'release-2-1-lower_name.xml'

    // use XML because the column.value* attributes in the Groovy DSL are broken and I need to update
    // existing records before applying a not null constraint
    include file: 'release-2-2-summary.xml'

	changeSet(author: "bea18c (generated)", id: "1397109340541-3") {
        createIndex(indexName: "lower_name_uniq_1397109339970", tableName: "project", unique: "true") {
            column(name: "lower_name")
        }
    }

    changeSet(author: "bea18c", id: "1397109340541-4") {
        modifyDataType(tableName: 'project', columnName: 'description', newDataType: 'varchar(1000)')
    }
}
