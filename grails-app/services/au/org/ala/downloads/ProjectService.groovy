package au.org.ala.downloads


class ProjectService {

    static transactional = true

    def artifactStorageService

    def deleteProject(Project project) {

        if (!project) {
            return
        }

        // Make sure to delete all dependencies
        def artifacts = ProjectArtifact.findAllByProject(project)
        artifacts.each { artifact ->
            deleteProjectArtifact(artifact)
        }

        project.delete()
    }

    def deleteProjectArtifact(ProjectArtifact artifact) {
        if (!artifact) {
            return
        }

        artifactStorageService.deleteArtifact(artifact)
        artifact.delete(flush: true, failOnError: true)
    }

}
