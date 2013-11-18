package au.org.ala.downloads

class FlushDownloadEventJob {
    def loggerService

    static triggers = {
      simple repeatInterval: 60000l // execute job once in 60 seconds
    }

    def execute() {
        loggerService.flushDownloadEvents()
    }
}
