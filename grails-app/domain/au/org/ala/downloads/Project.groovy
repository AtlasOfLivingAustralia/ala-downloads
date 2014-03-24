package au.org.ala.downloads

class Project {

    String name
    String description
    String guid

    static hasMany = [artifacts: ProjectArtifact]

    static constraints = {
        name nullable: false
        guid nullable: true
    }

    def beforeInsert() {
        if (!guid) {
            guid = UUID.randomUUID().toString()
        }
    }
}
