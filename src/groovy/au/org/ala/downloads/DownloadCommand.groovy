package au.org.ala.downloads

import grails.validation.Validateable

@Validateable
class DownloadCommand {

    Long id
    Integer reasonTypeId
    String comment

    static constraints = {
        id blank: false
        reasonTypeId blank: false, range: 0..10
        comment nullable: true, blank: true
    }

}
