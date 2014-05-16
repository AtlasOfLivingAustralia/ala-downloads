package au.org.ala.downloads

import grails.converters.JSON
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class LoggerService {

    def httpWebService, downloadService, grailsApplication

    def addProxiedDownloadEvent(String dataUri, String metadataUri, String userIP, String userEmail, String comment, Integer reasonTypeId) {
        final results = downloadService.getRecordCountsFromUrl(metadataUri)

        log.error "recordCountMap = $results"
        final event = new LogEvent()
        event.userEmail = userEmail
        event.comment = comment?:""
        event.userIP = userIP
        event.reasonTypeId = reasonTypeId
        event.recordCounts = results
        event.sourceUrl = dataUri

        if (!event.save(flush: true)) {
            log.error event.errors.allErrors.join("|")
        }
    }

    /**
     * Save logEvent to DB
     *
     * @param download
     * @param params
     */
    def addDownloadEvent(Download download, GrailsParameterMap params) {
        Map recordCountMap = [:]
        //recordCountMap.put("dr111", 200)
        download.recordCount.each {
            recordCountMap.put(it.code, it.records.toString())
        }
        log.error "recordCountMap = $recordCountMap"
        def logEvent = new LogEvent(
                userEmail: params.userEmail,
                comment: params.comment?:"",
                userIP: params.userIP,
                reasonTypeId: params.reasonTypeId,
                recordCounts: recordCountMap,
                sourceUrl: "http://macropus.ala.org.au/archives/" + download.fileUri.replaceAll(grailsApplication.config.app.downloads.dir,"")
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
            def resp
            log.debug "json = ${jsonBody.toString()}"
            if (grailsApplication.config.app.logger.enabled) {
                final server = grailsApplication.config.app.logger.server ?: 'http://logger.ala.org.au'
                final port = grailsApplication.config.app.logger.port ?: '80'
                final path = grailsApplication.config.app.logger.path ?: 'service/logger/'

                resp = httpWebService.doJsonPost(server, path, port, jsonBody.toString())
                log.debug "resp = $resp"
            } else {
                resp = [:]
                log.warn "ALA Logger NOT enabled"
            }

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
