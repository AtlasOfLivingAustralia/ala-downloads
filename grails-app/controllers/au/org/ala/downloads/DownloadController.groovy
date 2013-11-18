package au.org.ala.downloads

import org.springframework.dao.DataIntegrityViolationException

class DownloadController {
    def fileListingService, authService
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def beforeInterceptor = [action:this.&auth]

    private auth() {
        if (!authService.userInRole("ROLE_ADMIN")) {
            flash.message = "You are not authorised to access this page."
            redirect(controller:"home", action: "index")
            false
        } else {
            true
        }
    }

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [downloadInstanceList: Download.list(params), downloadInstanceTotal: Download.count()]
    }

    def create() {
        [downloadInstance: new Download(params), fileListing: fileListingService.getListing()]
    }

    def save() {
        def downloadInstance = new Download(params)
        if (!downloadInstance.save(flush: true)) {
            render(view: "create", model: [downloadInstance: downloadInstance])
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

        [downloadInstance: downloadInstance, fileListing: fileListingService.getListing()]
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
}
