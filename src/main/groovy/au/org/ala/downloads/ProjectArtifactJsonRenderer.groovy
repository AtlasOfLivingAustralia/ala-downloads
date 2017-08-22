package au.org.ala.downloads

import com.google.gson.stream.JsonWriter
import grails.rest.Link
import grails.rest.render.RenderContext
import grails.web.mime.MimeType
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by bea18c on 15/04/2014.
 */
class ProjectArtifactJsonRenderer extends AbstractJsonRenderer<ProjectArtifact> {

    Class<Project> getTargetType() { ProjectArtifact }
    MimeType[] getMimeTypes() { [MimeType.HAL_JSON, MimeType.JSON, MimeType.TEXT_JSON] as MimeType[] }

    @Autowired
    ProjectJsonRenderer projectJsonRenderer

    ProjectArtifactJsonRenderer() {
        super(ProjectArtifact, MimeType.HAL_JSON, MimeType.JSON, MimeType.TEXT_JSON)
    }

    @Override
    void renderInternal(ProjectArtifact artifact, RenderContext context) {
        JsonWriter jsonWriter = new JsonWriter(context.writer)
        renderArtifact(artifact, context.locale, jsonWriter, false)
        jsonWriter.flush()
    }

    void renderArtifact(ProjectArtifact artifact, Locale locale, JsonWriter writer, boolean embedded) {
        writer.beginObject().name(LINKS_ATTRIBUTE).beginObject()
        writeLink(
                Link.createLink(
                        rel: RELATIONSHIP_SELF,
                        href: grailsLinkGenerator.link(resource: 'projectResource/projectArtifactResource', action: 'show', id: artifact.id, projectResourceId: artifact.project.id, absolute: absoluteLinks),
                        contentType: MimeType.HAL_JSON.name),
                locale,
                writer)
        writeLink(
                Link.createLink(
                        rel: 'project',
                        href: grailsLinkGenerator.link(resource: 'projectResource', action: 'show', id: artifact.project.id, absolute: absoluteLinks),
                        contentType: MimeType.HAL_JSON.name),
                locale,
                writer)
        writer.name('alternate').beginArray()
        writeArrayLink(
                Link.createLink(
                        rel: 'alternate',
                        href: grailsLinkGenerator.link(mapping: 'artifactDetailsByName', params: [projectName: artifact.project.name, file: artifact.name], absolute: absoluteLinks),
                        contentType: MimeType.HTML.name
                ),
                locale,
                writer)
        writeArrayLink(
                Link.createLink(
                        rel: 'alternate',
                        href: grailsLinkGenerator.link(mapping: 'downloadByFile', params: [projectName: artifact.project.name, file: artifact.name], absolute: absoluteLinks),
                        contentType: artifact.mimeType
                ),
                locale,
                writer)
        writer.endArray()
        writer.endObject() // _links

        writeDomainProperty(artifact.name, 'name', writer)
        writeDomainProperty(artifact.fileSize, 'fileSize', writer)
        writeDomainProperty(artifact.mimeType, 'contentType', writer)
        writeDomainProperty(artifact.dateCreated, 'uploadedDate', writer)
        writeDomainProperty(artifact.md5hash, 'md5', writer)
        writeDomainProperty(artifact.sha1hash, 'sha1', writer)
        writeDomainProperty(artifact.summary, 'summary', writer)
        writeDomainProperty(artifact.description, 'description', writer)
        writeDomainProperty(artifact.downloadCount, 'downloadCount', writer)
        writeDomainProperty(artifact.deprecated, 'deprecated', writer)

        if (!embedded) {
            writer.name(EMBEDDED_ATTRIBUTE).beginObject().name("project")

            projectJsonRenderer.renderProject(artifact.project, locale, writer, true)

            writer.endObject() // _embedded
        }

        writer.endObject() // }
    }
}
