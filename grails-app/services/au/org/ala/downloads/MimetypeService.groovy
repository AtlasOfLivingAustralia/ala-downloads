package au.org.ala.downloads

import javax.annotation.PostConstruct
import grails.converters.*

/**
 * Service to provide list/map of Mimetypes.
 * Original source: http://www.stdicon.com/mimetypes
 */
class MimetypeService {

    def grailsApplication
    def mimetypeMap = [:]
    /**
     * Init method called on object construction
     */
    @PostConstruct
    def init() {
        def filename = "${System.properties['base.dir']}/grails-app/conf/mimetypes.json"
        def mimetypeList = JSON.parse(new File(filename).text)
        mimetypeList.each { it ->
            mimetypeMap.putAll(it)
        }
    }

    def getMimetypeMap() {
        mimetypeMap
    }
}
