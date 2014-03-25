package au.org.ala.downloads

import org.apache.commons.io.FileUtils

class ArtifactStorageService {

    def grailsApplication

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
