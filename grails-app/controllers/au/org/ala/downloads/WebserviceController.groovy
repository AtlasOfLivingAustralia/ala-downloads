package au.org.ala.downloads

import grails.converters.JSON
import groovy.io.FileType

class WebserviceController {
    def mimetypeService, fileListingService

    def getDownloadFileListing() {
        render fileListingService.getListing() as JSON
    }
}
