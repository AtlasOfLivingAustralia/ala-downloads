package au.org.ala.downloads

class HomeController {
    def loggerService

    def index(Integer max) {
        //redirect(action: "list", params: params)
        params.max = Math.min(max ?: 10, 100)
        render(view: "/index", model: [downloadInstanceList: Download.list(params), downloadInstanceTotal: Download.count(), projects: Project.list(), reasons: loggerService.reasons])
    }
}
