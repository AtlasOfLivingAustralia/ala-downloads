package au.org.ala.downloads


class ProjectService {

    static transactional = true

    def deleteProject(Project project) {

        if (!project) {
            return
        }

        // Make sure to delete all dependencies

        project.delete()
    }
}
