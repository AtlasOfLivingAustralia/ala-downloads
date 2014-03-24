package au.org.ala.downloads

class ProjectController {

    def index() {
        redirect(action:'list')
    }

    def list() {
        def projects = Project.listOrderByName()
        [projects: projects]
    }

}
