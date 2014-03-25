<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="adminLayout"/>
        <title>ALA Downloads - Administration - Projects</title>
        <r:script>
            $(document).ready(function() {

                $(".btnDelete").click(function(e) {
                    e.preventDefault();
                    var projectId = $(this).closest("[projectId]").attr("projectId");
                    if (projectId) {
                        window.location = "${createLink(action: "deleteProject")}?projectId=" + projectId;
                    }
                });

                $(".btnEdit").click(function(e) {
                    e.preventDefault();
                    var projectId = $(this).closest("[projectId]").attr("projectId");
                    if (projectId) {
                        window.location = "${createLink(action: "editProject")}?projectId=" + projectId;
                    }
                });

                $(".btnArtifacts").click(function(e) {
                    e.preventDefault();
                    var projectId = $(this).closest("[projectId]").attr("projectId");
                    if (projectId) {
                        window.location = "${createLink(action: "projectArtifacts")}?projectId=" + projectId;
                    }
                });

            });
        </r:script>
    </head>
    <body class="content">
        <content tag="pageTitle">Projects</content>
        <content tag="adminButtonBar">
            <a href="${createLink(controller:'admin', action:'newProject')}" class="btn btn-small btn-primary"><i class="icon-plus icon-white"></i>&nbsp;Create New Project</a>
        </content>

        <div>
            <h4>Project list</h4>
            <table class="table table-bordered table-striped">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Description</th>
                    <th>Artifacts</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                    <g:each in="${projects}" var="project">
                        <tr projectId="${project.id}">
                            <td><g:link action="projectArtifacts" params="${[projectId: project.id]}">${project.name}</g:link></td>
                            <td><small>${project.description}</small></td>
                            <td>${project.artifacts.size()}</td>
                            <td>
                                <button class="btnDelete btn btn-danger btn-mini"><i class="icon-remove icon-white"></i></button>
                                <button class="btnEdit btn btn-mini"><i class="icon-edit"></i></button>
                                <button class="btnArtifacts btn btn-mini" title="Project artifacts"><i class="icon-list"></i></button>
                            </td>
                        </tr>
                    </g:each>
                </tbody>
            </table>
        </div>
    </body>
</html>