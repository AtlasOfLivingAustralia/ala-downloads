package au.org.ala.downloads

import com.google.common.hash.Hashing
import com.google.common.hash.HashingOutputStream
import com.google.common.io.BaseEncoding
import com.google.common.io.ByteStreams
import com.google.common.io.Closer
import org.apache.commons.fileupload.FileUpload
import org.apache.commons.io.output.NullOutputStream

class ProxyService {

    static final String USER_AGENT = "ALA Downloads Service"

    static def transactional = false

    def headRequest(URL uri, String etag = '', Date lastModified = new Date(0)) throws IOException {

        def retVal = doRequest(uri, "HEAD", etag, lastModified)
        // app servers might not support head requests so try a get request instead.
        if (retVal.statusCode == 405) retVal = doRequest(uri, "GET", etag, lastModified)
        return retVal

    }

    private def doRequest(URL url, String method, String etag, Date lastModified) throws IOException {
        def c = url.openConnection().asType(HttpURLConnection)
        try {
            c.requestMethod = method
            c.setRequestProperty("User-Agent", USER_AGENT)
            if (etag) c.setRequestProperty("If-None-Match", etag)
            else if (lastModified && lastModified.time != 0) c.setIfModifiedSince(lastModified.time)
            c.connect()
            def rc = c.responseCode
            final stream
            final closer = Closer.create()

            def contentLength = c.contentLengthLong
            def contentMd5 = c.getHeaderField("Content-MD5")
            def contentEtag = c.getHeaderField("ETag")
            def contentLastMod = c.lastModified != 0 ? new Date(c.lastModified) : null

            try {
                if ((200..299).contains(rc)) {
                    stream = closer.register(c.inputStream)
                } else {
                    stream = closer.register(c.errorStream)
                }

                // Calculate content-length and md5 ourself if one of them was not sent by server and we have a response body
                if (!"HEAD".equalsIgnoreCase(method) &&
                    rc != 304 &&
                    (
                        (!contentMd5 && !contentLastMod && !contentEtag) || // We only care about md5 if there is no last modified and no etag
                         contentLength == -1)
                    ) {
                    def out = new HashingOutputStream(Hashing.md5(), NullOutputStream.NULL_OUTPUT_STREAM)
                    def written = ByteStreams.copy(stream, out)
                    contentLength = written
                    out.flush()
                    contentMd5 = BaseEncoding.base64().encode(out.hash().asBytes())
                }
            } catch (IOException e) {
                throw closer.rethrow(e)
            } finally {
                closer.close()
            }

            final filename = getFilenameForProxiedDownload(url.getPath(), c.getHeaderField(FileUpload.CONTENT_DISPOSITION))
            [statusCode: rc, contentLength: contentLength, contentType: c.contentType, lastModified: contentLastMod, filename: filename, etag: contentEtag, contentMd5: contentMd5]
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
