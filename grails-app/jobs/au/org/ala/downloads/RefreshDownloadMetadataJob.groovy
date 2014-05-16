package au.org.ala.downloads

class RefreshDownloadMetadataJob {
    static triggers = {
      simple repeatInterval: 300000l // execute job once in 5 minutes
    }

    def downloadService

    def execute() {
        log.debug("Running RefreshDownloadMetadataJob")
        downloadService.updateMetadataForDownloads()
        log.debug("Completed RefreshDownloadMetadataJob")
    }
}
