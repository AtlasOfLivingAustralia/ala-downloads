package au.org.ala.downloads

class FeedController {

    def projects() {
        render(feedType:"rss", feedVersion:"2.0") {
            title = "Projects"
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
            render(feedType:"rss", feedVersion:"2.0") {
                title = "Downloads for ${project.name}"
                link = "${createLink(action:"artifacts", absolute: true)}"
                description = "A list of the artifacts that can be downloaded for the ${project.name} project."
                project.artifacts?.each { artifact ->
                    if (!artifact.deprecated || includeDeprecated) {
                        entry(artifact.name) {
                            link = "${createLink(controller: 'project', action: 'downloadArtifact', id: artifact.id)}"
                            artifact.description
                        }
                    }
                }
            }
        }
    }
}
