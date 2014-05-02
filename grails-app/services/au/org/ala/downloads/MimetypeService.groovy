package au.org.ala.downloads

import com.google.common.base.Charsets
import com.google.common.io.Resources

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
        def mimetypeList = JSON.parse(Resources.toString(Resources.getResource("mimetypes.json"), Charsets.UTF_8))
        mimetypeList.each { it ->
            mimetypeMap.putAll(it)
        }
    }

    def getMimetypeMap() {
        mimetypeMap
    }
}
