package au.org.ala.downloads


class ProxyController {

    def loggerService

    def readFile (Long id) {
        def downloadInstance = Download.get(id)
        if (!downloadInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'download.label', default: 'Download'), id])
            redirect(controller: "download")
            return
        }

        try {
            def fileObj = new File(downloadInstance.filePath);
            def inputStream = fileObj.newInputStream()

            // log to ala-logger
            //LogEventVO vo_reason = new LogEventVO(1002, params.reasonTypeId?:0, 0, params.email?:"", params.comment?:"", request.getRemoteAddr(), null);
            //log (RestLevel.REMOTE, vo_reason);
            params.userIP = request.getRemoteAddr()
            loggerService.addDownloadEvent(downloadInstance, params)  // queue

            response.setHeader "Content-disposition", "attachment; filename=${fileObj.name}"
            response.contentType = "${downloadInstance.mimeType}"
            response.contentLength = fileObj.length() //downloadInstance.fileSize.toInteger()
            response.outputStream << inputStream
            response.outputStream.flush()
        } catch (FileNotFoundException fe) {
            log.error fe.localizedMessage, fe
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'download.label', default: 'Download file'), id])
            redirect(controller: "download")
            return
        }

    }
}
