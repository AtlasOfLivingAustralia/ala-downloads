package au.org.ala.downloads

import grails.rest.RestfulController
import grails.transaction.Transactional
import org.hibernate.annotations.FetchMode

class ProjectResourceController extends RestfulController<Project> {

    static responseFormats = ['json', 'xml']

    ProjectResourceController() {
        super(Project, true)
    }

//    def show(Project project) {
//        if (project) {
//            project.artifacts.each {}
//        }
        //project.links.find { it.rel == 'self' }.href == g.createLink(controller: "projectResource", params:[projectId: project.id])
        //project.link rel:'self', href: g.createLink(resource: 'projectResource', action: 'show', absolute: true, id: project.id), contentType: 'application/hal+json'
//        respond project
//    }

    @Override
    protected Project queryForResource(Serializable id) {
        // need to pre fetch artifacts because the hibernate session is gone after control is transferred into the
        // project json renderer
        def projects = Project.withCriteria {
            eq 'id', Long.parseLong(id)
            fetchMode 'artifacts', org.hibernate.FetchMode.SELECT
        }

        projects.empty ? null : projects.get(0)
    }
}
