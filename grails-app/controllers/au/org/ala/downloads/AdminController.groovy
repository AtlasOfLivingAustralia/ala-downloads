package au.org.ala.downloads

class AdminController {

    def projectService

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
        def artifacts = ProjectArtifact.findAllByProject(project)
        [projectInstance: project, artifacts: artifacts]
    }
}
