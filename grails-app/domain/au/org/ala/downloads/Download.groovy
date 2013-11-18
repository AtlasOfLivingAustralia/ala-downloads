package au.org.ala.downloads

class Download {
    String name
    String description
    String filePath
    String mimeType
    Long fileSize
    Date dateCreated
    Date lastUpdated

    static constraints = {
        name blank: false
        description blank: true, widget: 'textarea'
        filePath blank: false
        mimeType blank: true
        fileSize min: 1L
        dateCreated display: false
        lastUpdated display: false
    }
}
