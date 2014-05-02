package au.org.ala.downloads

import grails.rest.RestfulController

class ProjectArtifactResourceController extends RestfulController<ProjectArtifact> {

    static responseFormats = ['json', 'xml']

    ProjectArtifactResourceController() {
        super(ProjectArtifact, true)
    }

//    def show(ProjectArtifact projectArtifact) {
//        if (!projectArtifact) {
//            respond projectArtifact
//            return
//        }
//        //projectArtifact.link rel:'self', href: g.createLink(resource: 'projectResource/projectArtifactResource', action: 'show', absolute: true, id: projectArtifact.id, projectResourceId: projectArtifact.project.id), contentType: 'application/hal+json'
//        respond projectArtifact
//    }

    @Override
    protected ProjectArtifact queryForResource(Serializable id) {
        def projectId = params.projectResourceId
        ProjectArtifact.where {
            id == id && project.id == projectId
        }.find()
    }
}
