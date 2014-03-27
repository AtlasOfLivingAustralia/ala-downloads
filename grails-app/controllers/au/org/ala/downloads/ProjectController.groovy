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

    def artifactDetails() {
        def artifact = ProjectArtifact.get(params.int("id"))
        if (!artifact) {
            artifact = ProjectArtifact.get(params.int("artifactId"))
        }

        if (!artifact) {
            response.sendError(404, "The artifact id is missing or invalid")
            return
        }

        [artifact: artifact]
    }

    def downloadArtifact() {
        def artifact = ProjectArtifact.get(params.int("id"))
        if (!artifact) {
            artifact = ProjectArtifact.get(params.int("artifactId"))
        }

        if (!artifact) {
            response.sendError(404, "The artifact id is missing or invalid")
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

        def project = Project.get(params.int("id"))

        if (!project) {
            flash.errorMessage = "Missing or invalid project id!"
            redirect(action:'list')
            return
        }
        boolean includeDeprecated = params.boolean("includeDeprecated") ?: false

        def artifacts
        if (!includeDeprecated) {
            def c = ProjectArtifact.createCriteria()
            artifacts = c.list(params) {
                and {
                    eq("project", project)
                    or {
                        isNull("deprecated")
                        eq("deprecated", false)
                    }
                }

            }
        } else {
            artifacts = ProjectArtifact.findAllByProject(project, params)
        }

        [projectInstance: project, artifacts: artifacts]
    }

    def findByName() {
        println params.name

        def project = Project.findByNameIlike(params.name)
        if (project) {
            params.id = project.id
            render(view: 'artifactList', model:artifactList())
        } else {
            flash.errorMessage = "No such project '${params.name}"
            redirect(action:'list')
        }
    }

}
