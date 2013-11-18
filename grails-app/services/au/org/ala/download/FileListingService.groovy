package au.org.ala.download

import groovy.io.FileType

class FileListingService {
    def grailsApplication, mimetypeService

    def getListing() {
        def list = []
        def dir = new File(grailsApplication.config.app.downloads.dir)
        def mimetypeMap = mimetypeService.mimetypeMap

        try {
            dir.eachFileRecurse (FileType.FILES) { file ->
                def fileMap = [:]
                fileMap.path = file.absolutePath
                fileMap.size = file.length()
                fileMap.date = file.lastModified()
                def ext = file.name.tokenize('.').last()
                fileMap.mimetype = mimetypeMap.getAt("." + ext)
                list.add(fileMap)
            }
        } catch (FileNotFoundException e) {
            log.error e.localizedMessage, e
        }

        log.debug "list = $list"
        list
    }
}
