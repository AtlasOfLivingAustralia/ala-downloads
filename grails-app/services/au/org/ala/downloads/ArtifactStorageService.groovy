package au.org.ala.downloads

import org.apache.commons.io.FileUtils

class ArtifactStorageService {

    def grailsApplication

    /**
     * Stores the artifact from an input stream and calculates the sha and md5 hashes while it's storing the file.
     * The return value is a map that contains the md5 hash, sha hash, file length and the UUID that the file
     * is stored under
     *
     * @param artifact The artifact to store the input stream to
     * @param inputStream The input stream to store
     * @return A map with the keys md5, sha, length, guid which correspond to the MD5 hash, SHA hash (both as hex
     * strings), the input stream length as a long and the GUID used to store this file as a String.
     */
    def storeArtifactFile(ProjectArtifact artifact, InputStream inputStream) {
        def artifactId = UUID.randomUUID().toString()
        def filePath = getFilePathForArtifact(artifact.project.id, artifactId)

        def file = new File(filePath)
        if (file.exists()) {
            throw new RuntimeException("File path already exists!: ${filePath}")
        }

        // ensure the directory hierarchy exists...
        file.parentFile.mkdirs()
        FileUtil.copyInputStreamToFileWithSha1AndMd5Digest(file, inputStream) << [guid:artifactId]
    }

    def storeArtifactFile(ProjectArtifact artifact, byte[] bytes) {

        def artifactId = UUID.randomUUID().toString()
        def filePath = getFilePathForArtifact(artifact.project.id, artifactId)

        def file = new File(filePath)
        if (file.exists()) {
            throw new RuntimeException("File path already exists!: ${filePath}")
        }

        // ensure the directory hierarchy exists...
        file.parentFile.mkdirs()
        FileUtils.writeByteArrayToFile(file, bytes)
        return artifactId
    }

    private String getFilePathForArtifact(long projectId, String guid) {
        return "${grailsApplication.config.app.artifacts.dir}/${projectId}/${guid}"
    }

    def streamArtifactBytes(ProjectArtifact artifact, OutputStream stream) {
        def filePath = getFilePathForArtifact(artifact.project.id, artifact.artifactGuid)
        def file = new File(filePath)
        if (!file) {
            throw new RuntimeException("File does not exist for artifact ${artifact.id}")
        }

        stream << file.newInputStream()
    }

    def deleteArtifact(ProjectArtifact artifact) {
        def filePath = getFilePathForArtifact(artifact.project.id, artifact.artifactGuid)
        def file = new File(filePath)
        if (!file) {
            throw new RuntimeException("File does not exist for artifact ${artifact.id}")
        }

        // Try and delete the file
        if (!FileUtils.deleteQuietly(file)) {
            // if that fails, schedule it for deletion later!
            file.deleteOnExit()
        }
    }
}
