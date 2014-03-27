package au.org.ala.downloads

import org.codehaus.groovy.grails.plugins.codecs.MD5Codec
import org.codehaus.groovy.grails.plugins.codecs.SHA1Codec
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest

class AdminController {

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

        redirect(action:'projectList')
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

        def artifacts = ProjectArtifact.findAllByProject(project, params)
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

            artifact = new ProjectArtifact(name: file.originalFilename, description: params.description)
            artifact.project = project
            def bytes = file.bytes
            artifact.md5hash = MD5Codec.encode(bytes)
            artifact.sha1hash = SHA1Codec.encode(bytes)
            artifact.originalFilename = file.originalFilename
            artifact.uploadedBy = authService.userId
            artifact.mimeType = file.contentType
            artifact.fileSize = file.size
            artifact.artifactGuid = artifactStorageService.storeArtifactFile(artifact, bytes)

            artifact.save()
        } else {
            flash.errorMessage = "You must select a file to upload!"
            redirect(action:'uploadProjectArtifact', params:[projectId: project.id])
            return
        }

        redirect(action:'projectArtifacts', params:[projectId: project.id])
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
