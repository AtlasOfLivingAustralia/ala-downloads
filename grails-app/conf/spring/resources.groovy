import au.org.ala.downloads.ProjectArtifactJsonRenderer
import au.org.ala.downloads.ProjectArtifactListRenderer
import au.org.ala.downloads.ProjectJsonRenderer
import au.org.ala.downloads.ProjectListRenderer

// Place your Spring DSL code here

beans = {
    projectRenderer(ProjectJsonRenderer)
    projectCollectionRenderer(ProjectListRenderer)
    projectArtifactRenderer(ProjectArtifactJsonRenderer)
    projectArtifactCollectionRenderer(ProjectArtifactListRenderer)
}
