package au.org.ala.downloads

import au.org.ala.web.AlaSecured
import au.org.ala.web.CASRoles
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest

@AlaSecured(value = [CASRoles.ROLE_ADMIN], redirectController = "home")
class AdminController {

    static allowedMethods = [uploadArtifact: ['POST'],
                             updateProjectArtifact: ['PUT', 'POST'],
                             deleteProjectArtifact: ['DELETE', 'POST'],
                             saveProject: ['POST'],
                             deleteProject: ['DELETE', 'POST']]


    def projectService
    def authService
    def artifactStorageService

    def index() {
    }

    def projectList() {
        def projects = Project.listOrderByName()
        [projects: projects]
    }

    def newProject() {
        render(view: 'editProject')
    }

    def editProject() {
        def project = Project.get(params.int("projectId"))
        render(view:'editProject', model:[projectInstance: project])
    }

    def saveProject() {

        def project = Project.get(params.int("projectId"))

        if (project) {
            // modifying an existing project
            project.properties = params
        } else {
            project = new Project(params)
            project.save(failOnError: true, flush: true)
        }

        redirect(action:'projectArtifacts', params: [projectId: project.id] )
    }

    def deleteProject() {
        def project = Project.get(params.int("projectId"))
        if (project) {
            projectService.deleteProject(project)
            flash.message = "Project '${project.name}' deleted."
        }

        redirect(action:'projectList')
    }

    def projectArtifacts() {
        def project = Project.get(params.int("projectId"))
        if (!project) {
            flash.errorMessage = "No project found for id ${params.projectId}"
            redirect(action:'projectList')
            return
        }
        params.sort = params.sort ?: "dateCreated"
        params.order = params.order ?: "asc"

        def includeDeprecated = params.boolean("includeDeprecated", false)

        def artifacts = ProjectArtifact.byDeprecated(project, includeDeprecated).list(params)
        [projectInstance: project, artifacts: artifacts]
    }

    def uploadProjectArtifact() {
        def project = Project.get(params.int("projectId"))
        if (!project) {
            flash.errorMessage = "No project found for id ${params.projectId}"
            redirect(action:'projectList')
            return
        }
        [projectInstance: project]
    }

    def uploadArtifact() {
        def project = Project.get(params.int("projectId"))
        if (!project) {
            flash.errorMessage = "No project found for id ${params.projectId}"
            redirect(action:'projectList')
            return
        }

        ProjectArtifact artifact
        def fileList = []
        def mprequest = request as MultipartHttpServletRequest
        for (String paramName : mprequest.getMultiFileMap().keySet()) {
            List<MultipartFile> files = mprequest.getMultiFileMap().get(paramName);
            files.each { file ->
                if (file.size > 0) {
                    fileList << file
                }
            }
        }

        if (fileList) {
            MultipartFile file = fileList[0]
            def existing = ProjectArtifact.findAllByProjectAndName(project, file.originalFilename)
            if (existing) {
                flash.errorMessage = "There already exists in this project an artifact with the name '${existing.name}'"
                return
            }

            artifact = new ProjectArtifact(name: file.originalFilename, summary: params.summary, description: params.description)
            artifact.project = project
            artifact.originalFilename = file.originalFilename
            artifact.uploadedBy = authService.userId
            artifact.mimeType = file.contentType
            artifact.fileSize = file.size

            final start = System.currentTimeMillis()
            final result
            try {
                result = artifactStorageService.storeArtifactFile(artifact, file.inputStream)
            } catch (IOException e) {
                log.error("Couldn't write ${artifact} from ${file}", e)
                render(status: 500)
                return
            }
            if (artifact.fileSize != result.length) {
                log.warn("Reported size of ${file.originalFilename} in project ${project.name} differs from copied size")
            }
            artifact.md5hash = result.md5
            artifact.sha1hash = result.sha
            artifact.artifactGuid = result.guid

            final end = System.currentTimeMillis()
            log.debug("Copy and hash took ${end - start}ms")

            artifact.save()
        } else {
            flash.errorMessage = "You must select a file to upload!"
            redirect(action:'uploadProjectArtifact', params:[projectId: project.id])
            return
        }

        redirect(action:'projectArtifacts', params:[projectId: project.id])
    }

    def editProjectArtifact(int artifactId) {
        def artifact = ProjectArtifact.get(artifactId)
        if (!artifact) {
            flash.errorMessage = "Missing or invalid artifact id!"
            redirect(action: 'projectList')
            return
        }
        [artifactInstance: artifact]
    }

    def updateProjectArtifact(Long artifactId, Long version) {
        def artifact = ProjectArtifact.get(artifactId)
        if (!artifact) {
            flash.errorMessage = "Missing or invalid artifact id!"
            redirect(action: 'projectList')
            return
        }

        if (version != null) {
            if (artifact.version > version) {
                artifact.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'projectArtifact.label', default: 'Project Artifact')] as Object[],
                        "Another user has updated this Project Artifact while you were editing")
                render(view: "editProjectArtifact", model: [artifactInstance: artifact])
                return
            }
        }

        artifact.properties['description','summary','deprecated'] = params

        if (!artifact.save(flush: true)) {
            render(view: "editProjectArtifact", model: [artifactInstance: artifact])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'projectArtifact.label', default: 'Project Artifact'), artifact.id])
        redirect(action: "projectArtifacts", params:[projectId: artifact.project.id])
    }

    def deleteProjectArtifact() {
        def artifact = ProjectArtifact.get(params.int("artifactId"))
        if (!artifact) {
            flash.errorMessage = "Missing or invalid artifact id!"
            redirect(action: 'projectList')
            return
        }
        def project = artifact.project
        projectService.deleteProjectArtifact(artifact)
        redirect(action:"projectArtifacts", params:[projectId:project.id])
    }
}
