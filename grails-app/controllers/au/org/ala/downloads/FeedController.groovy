package au.org.ala.downloads

class FeedController {

    def projects() {
        render(feedType:"atom", feedVersion:"1.0") {
            title = "Projects hosted on the Atlas of Living Australia's download service"
            link = "${createLink(action:"projects", absolute: true)}"
            description = "A list of the projects that have artifacts hosted on the ALA downloads service"
            Project.list().each { project ->
                entry(project.name) {
                    link = "${createLink(controller:'project', action:'show', id: project.id)}"
                    project.description
                }
            }
        }
    }

    def artifacts() {
        def project = Project.findByNameIlike(params.projectName)
        def includeDeprecated = params.boolean("includeDeprecated") ?: false
        if (project) {
            render(feedType:"atom", feedVersion:"1.0") {
                title = "Downloads for project '${project.name}'"
                link = "${createLink(action:"artifacts", absolute: true)}"
                description = "A list of the artifacts that can be downloaded for the ${project.name} project from the Atlas Of Living Australia's download service."
                project.artifacts?.each { artifact ->
                    if (!artifact.deprecated || includeDeprecated) {
                        entry(artifact.name) {
                            link = "${createLink(controller: 'project', action: 'downloadArtifact', id: artifact.id, params:[name:artifact.name], absolute: true)}"
                            artifact.description
                        }
                    }
                }
            }
        }
    }
}
