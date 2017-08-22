package au.org.ala.downloads

import grails.validation.Validateable


class DownloadCommand implements Validateable{

    def loggerService

    Long id
    Integer reasonTypeId
    String comment

    static constraints = {
        id blank: false
        reasonTypeId nullable: false, blank: false, validator: { reason, cmd ->
            reason in cmd.loggerService.reasons*.id
        }
        comment nullable: true, blank: true
    }

}
