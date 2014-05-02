package au.org.ala.downloads

import grails.rest.Resource

@Resource(uri="/ws/projects", readOnly = true)
class Project {

    String name
    String lowerName // contains the Java .toLowerCase version of the name to ensure unique case insensitive project names
    String summary
    String description
    String guid

    static hasMany = [artifacts: ProjectArtifact]

    static namedQueries = {
        caseInsensitiveName { otherName ->
            def lowerOtherName = otherName.toLowerCase()
            eq 'lowerName', lowerOtherName
        }
    }

    static constraints = {
        name nullable: false
        lowerName nullable: false, unique: true
        summary nullable: false, maxSize: 255
        description maxSize: 1000
        guid nullable: true
    }

    static mapping = {
        lowerName defaultValue: ""
        summary defaultValue: ""
        description defaultValue: ""
        sort 'name'
    }

    def beforeValidate() {
        // before validate can be called often, so only set lowerName if it's null or
        // empty since it will be forced to the correct value on insert and update
        if (!lowerName)
            lowerName = name?.toLowerCase()
    }

    def beforeInsert() {
        if (!guid) {
            guid = UUID.randomUUID().toString()
        }
        lowerName = name?.toLowerCase()
    }

    def beforeUpdate() {
        lowerName = name?.toLowerCase()
    }
}
