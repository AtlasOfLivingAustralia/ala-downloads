package au.org.ala.downloads

class ProjectController {

    def artifactStorageService

    def index() {
        redirect(action:'list')
    }

    def list() {
        def projects = Project.listOrderByName()
        [projects: projects]
    }

    def artifactDetailsByName(String projectName, String file) {
        render(view: 'artifactDetails', model: internalArtifactDetails(artifactByName(projectName, file)))
    }

    def artifactDetails() {
        def artifact = ProjectArtifact.get(params.int("id"))
        if (!artifact) {
            artifact = ProjectArtifact.get(params.int("artifactId"))
        }

        internalArtifactDetails(artifact)
    }

    private def internalArtifactDetails(ProjectArtifact artifact) {
        if (!artifact) {
            response.sendError(404, "The artifact id is missing or invalid")
            return
        }

        [artifact: artifact]
    }

    def downloadArtifactByName(String projectName, String file) {
        internalDownloadArtifact(artifactByName(projectName, file))
    }

    def downloadArtifact() {
        def artifact = ProjectArtifact.get(params.int("id"))
        if (!artifact) {
            artifact = ProjectArtifact.get(params.int("artifactId"))
        }
        internalDownloadArtifact(artifact)
    }

    private def internalDownloadArtifact(ProjectArtifact artifact) {

        if (!artifact) {
            response.sendError(404, "The artifact is missing or invalid")
            return
        }

        try {
            response.setHeader "Content-disposition", "attachment; filename=${artifact.name}"
            response.contentType = "${artifact.mimeType}"
            response.contentLength = artifact.fileSize
            artifactStorageService.streamArtifactBytes(artifact, response.outputStream)
            response.outputStream.flush()
            artifact.downloadCount = (artifact.downloadCount ?: 0) + 1
        } catch (Exception ex) {
            response.sendError(500, ex.message)
        }
    }

    def artifactList() {
        internalArtifactList(Project.get(params.int("id")))
    }

    def artifactListByName() {
        render(view: 'artifactList', model: internalArtifactList(Project.caseInsensitiveName(params.projectName).get()))
    }

    private def internalArtifactList(Project project) {
        if (!project) {
            flash.errorMessage = "Missing or invalid project id!"
            redirect(action:'list')
            return
        }
        boolean includeDeprecated = params.boolean("includeDeprecated") ?: false

        def artifacts = ProjectArtifact.byDeprecated(project, includeDeprecated).list(params)

        [projectInstance: project, artifacts: artifacts]
    }

    private ProjectArtifact artifactByName(String projectName, String file) {
        ProjectArtifact.byName(projectName, file).get()
    }

}
