package au.org.ala.downloads

import au.org.ala.web.AlaSecured
import grails.converters.JSON
import org.springframework.dao.DataIntegrityViolationException

@AlaSecured(value=["ROLE_ADMIN"], redirectController = "home", message = "You are not authorised to access this page")
class DownloadController {
    static allowedMethods = [save: "POST", update: "POST", delete: "POST", checkHead: "GET", download: ["HEAD", "GET"]]

    def proxyService, downloadService

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [downloadInstanceList: Download.list(params), downloadInstanceTotal: Download.count()]
    }

    def create() {
        [downloadInstance: new Download(params), metadataRecords: []]
    }

    def save() {
        def downloadInstance = new Download(params)

        setPropertiesFromDataUri(downloadInstance)

        if (downloadInstance.fileSize == -1) downloadInstance.fileSize = null
        if (!downloadInstance.dataLastModified) downloadInstance.dataLastModified = new Date()

        if (!downloadInstance.save(flush: true)) {
            render(view: "create", model: [downloadInstance: downloadInstance, metadataRecords: downloadService.getRecordCountsFromUrlAsArray(downloadInstance.metadataUri)])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'download.label', default: 'Download'), downloadInstance.id])
        redirect(action: "show", id: downloadInstance.id)
    }

    def show(Long id) {
        def downloadInstance = Download.get(id)
        if (!downloadInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'download.label', default: 'Download'), id])
            redirect(action: "list")
            return
        }

        [downloadInstance: downloadInstance]
    }

    def edit(Long id) {
        def downloadInstance = Download.get(id)
        if (!downloadInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'download.label', default: 'Download'), id])
            redirect(action: "list")
            return
        }

        [downloadInstance: downloadInstance, metadataRecords: downloadService.getRecordCountsFromUrlAsArray(downloadInstance.metadataUri)]
    }

    def update(Long id, Long version) {
        def downloadInstance = Download.get(id)
        if (!downloadInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'download.label', default: 'Download'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (downloadInstance.version > version) {
                downloadInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'download.label', default: 'Download')] as Object[],
                          "Another user has updated this Download while you were editing")
                render(view: "edit", model: [downloadInstance: downloadInstance])
                return
            }
        }

        downloadInstance.properties = params

        setPropertiesFromDataUri(downloadInstance)

        if (!downloadInstance.save(flush: true)) {
            render(view: "edit", model: [downloadInstance: downloadInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'download.label', default: 'Download'), downloadInstance.id])
        redirect(action: "show", id: downloadInstance.id)
    }

    def delete(Long id) {
        def downloadInstance = Download.get(id)
        if (!downloadInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'download.label', default: 'Download'), id])
            redirect(action: "list")
            return
        }

        try {
            downloadInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'download.label', default: 'Download'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'download.label', default: 'Download'), id])
            redirect(action: "show", id: id)
        }
    }

    def checkHead(String uri) {
        log.debug("checkHead ${uri}")
        try {
            render proxyService.headRequest(new URL(uri)) as JSON
        } catch (MalformedURLException e) {
            log.debug("MalformedURLException contacting ${uri}", e)
            response.sendError(400, "The URL is invalid")
        } catch (IOException e) {
            log.debug("IOException contacting ${uri}", e)
            response.sendError(500)
        }
        return null
    }

    def checkMetadataUri(String uri) {
        log.debug("checkMetadataUri ${uri}")
        try {
            render downloadService.getRecordCountsFromUrlAsArray(uri) as JSON
        } catch (MalformedURLException e) {
            log.debug("MalformedURLException contacting ${uri}", e)
            response.sendError(400, "The URL is invalid")
        } catch (IOException e) {
            log.debug("IOException contacting ${uri}", e)
            response.sendError(500, "Network error contacting service")
        }
        return null
    }

    private def setPropertiesFromDataUri(Download downloadInstance) {
        final data = proxyService.headRequest(downloadInstance.fileUri.toURL())
        downloadInstance.dataEtag = data.etag
        downloadInstance.dataLastModified = data.lastModified
        downloadInstance.fileSize = data.contentLength
        downloadInstance.mimeType = data.contentType
        downloadInstance.dataMd5 = data.contentMd5
    }
}
