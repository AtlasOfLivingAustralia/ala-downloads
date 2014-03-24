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
    String guid
    List<String> tags
    String md5hash
    String uploadedBy

    Date dateCreated
    Date lastUpdated

    static belongsTo = [project: Project]
    static hasMany = [tags: String]

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
        md5hash nullable: true
        uploadedBy nullable: true
    }

    def beforeInsert() {
        if (!guid) {
            guid = UUID.randomUUID().toString()
        }
    }
}
