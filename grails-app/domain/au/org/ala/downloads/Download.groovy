package au.org.ala.downloads

class Download {
    String name
    String description
    String fileUri
    String mimeType
    Long fileSize

    Date dateCreated
    Date lastUpdated

    static hasMany = [recordCount: RecordCount]

    static constraints = {
        name blank: false
        description blank: true, widget: 'textarea'
        fileUri blank: false, unique: true
        mimeType blank: true
        fileSize min: 1L
        dateCreated display: false
        lastUpdated display: false
    }
}
