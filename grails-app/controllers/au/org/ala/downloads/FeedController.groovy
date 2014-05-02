package au.org.ala.downloads

import com.sun.syndication.feed.synd.SyndContentImpl

class FeedController {

    def markdownService

    def projects() {
        render(feedType:"atom", feedVersion:"1.0") {
            title = "Projects hosted on the Atlas of Living Australia's download service"
            link = "${createLink(action:"projects", absolute: true)}"
            description = "A list of the projects that have artifacts hosted on the ALA downloads service"
            Project.listOrderByName(order: "desc").each { project ->
                entry(project.name) {
                    link = "${createLink(mapping: "projectByName", params: [projectName: project.name], absolute: true)}"
                    def descriptionNode = new SyndContentImpl()
                    descriptionNode.type = 'text'
                    descriptionNode.value = project.summary
                    description = descriptionNode
                    return content(type: 'html') {
                        markdownService.markdown(project.description)
                    }
                }
            }
        }
    }

    def artifacts() {
        def project = Project.caseInsensitiveName(params.projectName).get()
        def includeDeprecated = params.boolean("includeDeprecated") ?: false
        if (project) {
            render(feedType:"atom", feedVersion:"1.0") {
                title = "Downloads for project '${project.name}'"
                link = "${createLink(action:"artifacts", absolute: true)}"
                description = "A list of the artifacts that can be downloaded for the ${project.name} project from the Atlas Of Living Australia's download service."
                ProjectArtifact.findAllByProject(project, [sort: 'lastUpdated', order: 'desc']).each { artifact ->
                    if (!artifact.deprecated || includeDeprecated) {
                        entry(artifact.name) {
                            link = "${createLink(mapping: "artifactDetailsByName", params:[projectName: params.projectName, file:artifact.name], absolute: true)}"
                            title = artifact.name
                            publishedDate = artifact.dateCreated
                            updatedDate = artifact.lastUpdated
                            def descriptionNode = new SyndContentImpl()
                            descriptionNode.type = 'text'
                            descriptionNode.value = artifact.summary
                            description = descriptionNode
                            content(type: 'html') {
                                markdownService.markdown(artifact.description)
                            }
                        }
                    }
                }
            }
        }
    }
}
