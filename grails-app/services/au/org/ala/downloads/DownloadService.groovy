package au.org.ala.downloads

import grails.transaction.Transactional
import org.apache.commons.lang.time.DateUtils

@Transactional
class DownloadService {

    def proxyService, httpWebService

    def updateMetadataForDownloads() {
        Download.all.each { updateMetadataForDownload(it) }
    }

    def updateMetadataForDownload(Download download) {
        try {
            final newVals = proxyService.headRequest(download.fileUri.toURL(), download.dataEtag, download.dataLastModified)
            if (newVals.statusCode == 304) {
                // The response indicated the resource was unchanged since the last
                log.debug("${download} data resource is unchanged since last update")
            } else if (newVals.statusCode >= 300) {
                // Don't update the download if there was a non successful HTTP response
                log.warn("HTTP Request (${newVals}) while refreshing metadata for ${download} was non successful!")
            } else if (download.mimeType != newVals.contentType ||
                !DateUtils.truncatedEquals(download.dataLastModified, newVals.lastModified, Calendar.SECOND) ||
                download.fileSize != newVals.contentLength ||
                download.dataEtag != newVals.etag) {

                // Only update the download if some of the data has changed
                download.mimeType = newVals.contentType
                download.fileSize = newVals.contentLength

                download.dataLastModified = newVals.lastModified
                download.dataEtag = newVals.etag

                log.info("Updating ${download}")
                if (!download.save(validate: true)) {
                    log.error("Tried to save ${download} but it was invalid!")
                }
            }
        } catch (MalformedURLException | IOException e) {
            log.error("Error updating metadata for ${download}", e)
        }
    }

    def getRecordCountsFromUrl(String url) {
        getFacetMap(httpWebService.getJson(url))
    }

    def getRecordCountsFromUrlAsArray(String url) {
        getRecordCountsFromUrl(url).collect {k,v -> [id:k, count:v]}
    }

    private def getFacetMap(json) {
        json.facetResults
                .findAll { ["institution_uid", "collection_uid", "data_resource_uid"].contains(it.fieldName) }
                .collectEntries { it.fieldResult.collectEntries { [ (it.fq.split(':')[1].replaceAll('"', '')) : it.count.toString() ] } }
    }
}
