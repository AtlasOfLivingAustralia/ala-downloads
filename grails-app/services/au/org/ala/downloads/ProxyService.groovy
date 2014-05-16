package au.org.ala.downloads

import org.apache.commons.fileupload.FileUpload

class ProxyService {

    static final String USER_AGENT = "ALA Downloads Service"

    static def transactional = false

    def headRequest(URL uri, String etag = '', Date lastModified = new Date(0)) throws IOException {

        def c = uri.openConnection().asType(HttpURLConnection)
        try {
            c.requestMethod = "HEAD"
            c.setRequestProperty("User-Agent", USER_AGENT)
            if (etag) c.setRequestProperty("If-None-Match", etag)
            else if (lastModified && lastModified.time != 0) c.setIfModifiedSince(lastModified.time)
            c.connect()
            final filename = getFilenameForProxiedDownload(uri.getPath(), c.getHeaderField(FileUpload.CONTENT_DISPOSITION))
            [statusCode: c.responseCode, contentLength: c.contentLengthLong, contentType: c.contentType, lastModified: new Date(c.lastModified), filename: filename, etag: c.getHeaderField("ETag")]
        } finally {
            c.disconnect()
        }
    }

    /**
     * Hack using Apache Commons FileUpload to parse Content Disposition header value or if that value is null use
     * a File object to extract the filename from the path component of a URL
     *
     * @param path The path component of a URL
     * @param contentDisposition The content disposition
     * @return the best guess at a filename
     */
    def getFilenameForProxiedDownload(String path, String contentDisposition) {
        new FileUpload().getFileName([(FileUpload.CONTENT_DISPOSITION) : contentDisposition]) ?: new File(path).getName()
    }
}
