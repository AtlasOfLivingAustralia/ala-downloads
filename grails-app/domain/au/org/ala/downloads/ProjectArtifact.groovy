package au.org.ala.downloads

class ProjectArtifact {

    Project project
    String name
    String description
    String fileUri
    String mimeType
    Long fileSize
    Long downloadCount
    Boolean deprecated

    Date dateCreated
    Date lastUpdated

    static belongsTo = [project: Project]

    static constraints = {
        name blank: false
        description blank: true, widget: 'textarea'
        fileUri blank: false, unique: true
        mimeType blank: true
        fileSize min: 1L
        dateCreated display: false
        lastUpdated display: false
        downloadCount nullable: true
        deprecated nullable: true
    }
}
