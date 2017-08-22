package au.org.ala.downloads

import com.google.gson.stream.JsonWriter
import grails.rest.Link
import grails.rest.render.ContainerRenderer
import grails.rest.render.RenderContext
import grails.web.mime.MimeType
import org.grails.plugins.web.rest.render.ServletRenderContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod

/**
 * Created by bea18c on 15/04/2014.
 */
class ProjectArtifactListRenderer extends AbstractJsonRenderer<List<ProjectArtifact>> implements ContainerRenderer<List<ProjectArtifact>, ProjectArtifact> {

    @Autowired
    ProjectArtifactJsonRenderer projectArtifactJsonRenderer;
    @Autowired
    ProjectJsonRenderer projectJsonRenderer

    Class<List> getTargetType() { List }
    Class<Project> getComponentType() { ProjectArtifact }
    MimeType[] getMimeTypes() { [MimeType.HAL_JSON, MimeType.JSON, MimeType.TEXT_JSON] as MimeType[] }

    ProjectArtifactListRenderer() {
        super(List, MimeType.HAL_JSON, MimeType.JSON, MimeType.TEXT_JSON)
    }

    void renderInternal(List<ProjectArtifact> artifacts, RenderContext context) {
        JsonWriter jsonWriter = new JsonWriter(context.writer)
        def locale = context.locale

        Long projectId = 0
        if (context instanceof ServletRenderContext) {
            def src = context as ServletRenderContext
            projectId = src.webRequest.params.getLong('projectResourceId')
        }

        renderProjectArtifactList(Project.get(projectId), artifacts, locale, jsonWriter, false)
        jsonWriter.flush()
    }

    def renderProjectArtifactList(Project project, Collection<ProjectArtifact> artifacts, Locale locale, JsonWriter jsonWriter, boolean embedded) {
        //URI projectUri = URI.create(context.resourcePath).resolve("..")

        jsonWriter.beginObject().name(LINKS_ATTRIBUTE).beginObject()
        writeLink(
                Link.createLink(
                        rel: RELATIONSHIP_SELF,
                        href: grailsLinkGenerator.link(resource: 'projectResource/projectArtifactResource', action: 'index', projectResourceId: project.id, method: HttpMethod.GET.toString(), absolute: absoluteLinks),
                        contentType: 'application/hal+json'),
                locale,
                jsonWriter)
        writeLink(
                Link.createLink(
                        rel: 'project',
                        href: grailsLinkGenerator.link(resource: 'projectResource', action: 'show', id: project.id, method: HttpMethod.GET.toString(), absolute: absoluteLinks),
                        contentType: 'application/hal+json'),
                locale,
                jsonWriter)
        jsonWriter.name('artifact').beginArray()
        for (def artifact : artifacts) {
            writeArrayLink(
                    Link.createLink(
                            rel: 'artifact',
                            href: grailsLinkGenerator.link(resource: 'projectResource/projectArtifactResource', action: 'show', id: artifact.id, projectResourceId: project.id, absolute: absoluteLinks),
                            title: artifact.name,
                            contentType: 'application/hal+json',
                            deprecated: artifact.deprecated),
                    locale,
                    jsonWriter)
        }
        jsonWriter.endArray() // artifact
        jsonWriter.endObject() // _links

        writeDomainProperty(artifacts.size(), 'size', jsonWriter)

        if (!embedded) {
            jsonWriter.name(EMBEDDED_ATTRIBUTE).beginObject()

            jsonWriter.name('project')
            projectJsonRenderer.renderProject(project, locale, jsonWriter, true)

            jsonWriter.name('artifact').beginArray()
            for (def artifact : artifacts) {
                projectArtifactJsonRenderer.renderArtifact(artifact, locale, jsonWriter, true)
            }
            jsonWriter.endArray() // artifact
            jsonWriter.endObject() // _embedded
        }

        jsonWriter.endObject() // }

    }

}

