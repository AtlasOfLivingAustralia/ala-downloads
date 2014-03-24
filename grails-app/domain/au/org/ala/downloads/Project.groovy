package au.org.ala.downloads

class Project {

    String name
    String description

    static hasMany = [artifacts: ProjectArtifact]

    static constraints = {
        name nullable: false
    }
}
