package au.org.ala.downloads

import org.springframework.dao.DataIntegrityViolationException

class RecordCountController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [recordCountInstanceList: RecordCount.list(params), recordCountInstanceTotal: RecordCount.count()]
    }

    def create() {
        [recordCountInstance: new RecordCount(params)]
    }

    def save() {
        def recordCountInstance = new RecordCount(params)
        if (!recordCountInstance.save(flush: true)) {
            render(view: "create", model: [recordCountInstance: recordCountInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'recordCount.label', default: 'RecordCount'), recordCountInstance.id])
        redirect(action: "show", id: recordCountInstance.id)
    }

    def show(Long id) {
        def recordCountInstance = RecordCount.get(id)
        if (!recordCountInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'recordCount.label', default: 'RecordCount'), id])
            redirect(action: "list")
            return
        }

        [recordCountInstance: recordCountInstance]
    }

    def edit(Long id) {
        def recordCountInstance = RecordCount.get(id)
        if (!recordCountInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'recordCount.label', default: 'RecordCount'), id])
            redirect(action: "list")
            return
        }

        [recordCountInstance: recordCountInstance]
    }

    def update(Long id, Long version) {
        def recordCountInstance = RecordCount.get(id)
        if (!recordCountInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'recordCount.label', default: 'RecordCount'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (recordCountInstance.version > version) {
                recordCountInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'recordCount.label', default: 'RecordCount')] as Object[],
                        "Another user has updated this RecordCount while you were editing")
                render(view: "edit", model: [recordCountInstance: recordCountInstance])
                return
            }
        }

        recordCountInstance.properties = params

        if (!recordCountInstance.save(flush: true)) {
            render(view: "edit", model: [recordCountInstance: recordCountInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'recordCount.label', default: 'RecordCount'), recordCountInstance.id])
        redirect(action: "show", id: recordCountInstance.id)
    }

    def delete(Long id) {
        def recordCountInstance = RecordCount.get(id)
        if (!recordCountInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'recordCount.label', default: 'RecordCount'), id])
            redirect(action: "list")
            return
        }

        try {
            recordCountInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'recordCount.label', default: 'RecordCount'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'recordCount.label', default: 'RecordCount'), id])
            redirect(action: "show", id: id)
        }
    }
}
