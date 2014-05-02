package au.org.ala.downloads

import com.google.gson.stream.JsonWriter
import grails.rest.Link
import grails.rest.render.ContainerRenderer
import grails.rest.render.RenderContext
import org.codehaus.groovy.grails.web.mime.MimeType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod

/**
 * Created by bea18c on 15/04/2014.
 */
class ProjectListRenderer extends AbstractJsonRenderer<List<Project>> implements ContainerRenderer<List<Project>, Project> {

    @Autowired
    ProjectJsonRenderer jsonRenderer;

    Class<List> getTargetType() { List }
    Class<Project> getComponentType() { Project }
    MimeType[] getMimeTypes() { [MimeType.HAL_JSON, MimeType.JSON, MimeType.TEXT_JSON] as MimeType[] }

    ProjectListRenderer() {
        super(List, MimeType.HAL_JSON, MimeType.JSON, MimeType.TEXT_JSON)
    }

    void renderInternal(List<Project> projects, RenderContext context) {
        JsonWriter jsonWriter = new JsonWriter(context.writer)

        jsonWriter.beginObject().name(LINKS_ATTRIBUTE).beginObject()
        writeLink(
                Link.createLink(
                        rel: RELATIONSHIP_SELF,
                        href: grailsLinkGenerator.link(resource: 'projectResource', action: 'index', method: HttpMethod.GET.toString(), absolute: absoluteLinks),
                        contentType: 'application/hal+json'),
                context.locale,
                jsonWriter)
        jsonWriter.name('project').beginArray()
        for (def project : projects) {
            writeArrayLink(
                    Link.createLink(
                            rel: 'project',
                            href: grailsLinkGenerator.link(resource: 'projectResource', action: 'show', absolute: absoluteLinks, id: project.id),
                            title: project.name,
                            contentType: 'application/hal+json'),
                    context.locale,
                    jsonWriter
            )
        }
        jsonWriter.endArray() // projects

        jsonWriter.endObject() // _links

        writeDomainProperty(projects.size(), 'size', jsonWriter)

        jsonWriter.name(EMBEDDED_ATTRIBUTE).beginObject()
        jsonWriter.name('project').beginArray()
        for (def project : projects) {
            jsonRenderer.renderProject(project, context.locale, jsonWriter, true)
        }
        jsonWriter.endArray()
        jsonWriter.endObject() // _embedded
        jsonWriter.endObject() // }

        jsonWriter.flush()
    }


}
