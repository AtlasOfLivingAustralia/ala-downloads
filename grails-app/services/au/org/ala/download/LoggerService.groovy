package au.org.ala.download

import au.org.ala.downloads.Download
import au.org.ala.downloads.LogEvent
import grails.converters.JSON
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class LoggerService {
    def httpWebService
    /**
     * Save logEvent to DB
     *
     * @param download
     * @param params
     */
    def addDownloadEvent(Download download, GrailsParameterMap params) {
        def logEvent = new LogEvent(
                userEmail: params.userEmail,
                comment: params.comment?:"",
                userIP: params.userIP,
                reasonTypeId: params.reasonTypeId,
                sourceUrl: "http://macropus.ala.org.au/archives/" + download.filePath.replaceAll("/data/archives/","")
        )
        if (!logEvent.save(flush: true)) {
            log.error logEvent.errors.allErrors.join("|")
        }
    }

    /**
     * Log any events in the DB to the ALA Logger web service.
     * @see <a href="http://code.google.com/p/ala-bie/wiki/ALALoggerServices">ALALoggerServices wiki page</a>
     *
     * @return
     */
    def flushDownloadEvents() {
        def events = LogEvent.listOrderByDateCreated()
        events.each { event ->
            log.info "${(System.currentTimeMillis() / 1000L)} event = ${event as JSON}"
            def jsonBody = event as JSON
            def resp = httpWebService.doJsonPost("http://logger.ala.org.au/", "service/logger/", "80", jsonBody.toString())
            log.debug "resp = $resp"
            if (resp.error) {
                // got an error
                log.error "${resp.error}"
            } else {
                // it worked
                try {
                    event.delete(flush:true)
                } catch (Exception ex) {
                    log.error ex.localizedMessage, ex
                }
            }
        }
    }
}
