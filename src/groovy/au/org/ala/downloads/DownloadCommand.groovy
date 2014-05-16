package au.org.ala.downloads

import grails.validation.Validateable

@Validateable
class DownloadCommand {

    Long id
    Integer reasonTypeId
    String userEmail
    String comment

    static constraints = {
        id blank: false
        reasonTypeId blank: false, range: 0..10
        userEmail blank: false, email: true
        comment nullable: true, blank: true
    }

}
