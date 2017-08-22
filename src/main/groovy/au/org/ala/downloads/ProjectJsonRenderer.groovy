package au.org.ala.downloads

import com.google.gson.stream.JsonWriter
import grails.rest.Link
import grails.rest.render.*
import grails.web.mime.MimeType
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by bea18c on 15/04/2014.
 */
class ProjectJsonRenderer extends AbstractJsonRenderer<Project> {

    Class<Project> getTargetType() { Project }
    MimeType[] getMimeTypes() { [MimeType.HAL_JSON, MimeType.JSON, MimeType.TEXT_JSON] as MimeType[] }

    @Autowired
    ProjectArtifactListRenderer projectArtifactListRenderer

    ProjectJsonRenderer() {
        super(Project, MimeType.HAL_JSON, MimeType.JSON, MimeType.TEXT_JSON)
    }

    void renderInternal(Project project, RenderContext context) {
        JsonWriter jsonWriter = new JsonWriter(context.writer)
        renderProject(project, context.locale, jsonWriter, false)
        jsonWriter.flush()
    }

    void renderProject(Project project, Locale locale, JsonWriter jsonWriter, boolean embedded) {
        jsonWriter.beginObject().name(LINKS_ATTRIBUTE).beginObject()
        writeLink(
                Link.createLink(
                        rel: RELATIONSHIP_SELF,
                        href: grailsLinkGenerator.link(resource: 'projectResource', action: 'show', id: project.id, absolute: absoluteLinks),
                        contentType: 'application/hal+json'),
                locale,
                jsonWriter)

        writeLink(
                Link.createLink(
                        rel: 'artifacts',
                        href: grailsLinkGenerator.link(resource: 'projectResource/projectArtifactResource', action: 'index', projectResourceId: project.id, absolute: absoluteLinks),
                        contentType: 'application/hal+json',
                ),
                locale,
                jsonWriter)

        jsonWriter.endObject() // _links

        writeDomainProperty(project.id, 'id', jsonWriter)
        writeDomainProperty(project.name, 'name', jsonWriter)
        writeDomainProperty(project.summary, 'summary', jsonWriter)
        writeDomainProperty(project.description, 'description', jsonWriter)

        if (!embedded) {
            jsonWriter.name(EMBEDDED_ATTRIBUTE).beginObject().name('artifacts')
            projectArtifactListRenderer.renderProjectArtifactList(project, project.artifacts, locale, jsonWriter, true)
            jsonWriter.endObject() // _embedded
        }

        jsonWriter.endObject() // }
    }

}
