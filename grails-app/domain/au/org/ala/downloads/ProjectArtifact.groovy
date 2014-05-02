package au.org.ala.downloads

import grails.rest.Resource

@Resource(readOnly = true)
class ProjectArtifact {

    private static final DEFAULT_DOWNLOAD_COUNT = 0L
    private static final DEFAULT_DEPRECATED = false

    Project project
    String name
    String summary
    String description
    String originalFilename
    String artifactGuid
    String mimeType
    Long fileSize
    Long downloadCount = DEFAULT_DOWNLOAD_COUNT
    Boolean deprecated = DEFAULT_DEPRECATED
    List<String> tags
    String md5hash
    String sha1hash
    String uploadedBy

    Date dateCreated
    Date lastUpdated

    static belongsTo = [project: Project]
    static hasMany = [tags: String]

    static namedQueries = {
        /**
         * Find a file by name.  Requires two params, the first is the project name (it should be lower cased already)
         * and then second is the file name, case sensitive
         */
        byName { projectName, filename ->
            project {
                caseInsensitiveName(projectName)
            }
            eq 'name', filename
        }
        byDeprecated { project, deprecated ->
            eq "project", project
            if (deprecated) {
                or {
                    isNotNull "deprecated"
                    eq "deprecated", true
                }
            } else {
                or {
                    isNull "deprecated"
                    eq "deprecated", false
                }
            }

        }
    }

    static constraints = {
        name blank: false
        summary blank: false, maxSize: 255
        description blank: true, maxSize: 1000, widget: 'textarea'
        artifactGuid blank: false, unique: true
        mimeType blank: true
        fileSize min: 0L
        dateCreated display: false
        lastUpdated display: false
        downloadCount nullable: true
        deprecated nullable: true
        md5hash nullable: true
        uploadedBy nullable: true
        md5hash nullable: false
        sha1hash nullable: true
    }

    static mapping = {
        deprecated defaultValue: "${DEFAULT_DEPRECATED}"
        downloadCount defaultValue: "${DEFAULT_DOWNLOAD_COUNT}"
        sort downloadCount:"desc", dateCreated: "desc"
        name index: 'project_artifact_name_idx'
    }

}
