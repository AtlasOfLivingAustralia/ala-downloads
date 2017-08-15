<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="adminLayout"/>
        <title>ALA Downloads - Administration - Projects</title>
        <asset:script type="text/javascript">
        jQuery(function($) {
            $('.btnDelete').click(function(e) {
                e.preventDefault();
                var form = $(e.target).closest('form');
                var name = $(e.target).closest('[data-project-name]').data('project-name');
                $('#confirmationModalSave').text("Delete " + name).on('click', function(e) {
                    form.submit();
                });
                $('#confirmModelText').text("Are you sure you want to delete " + name + "?");
                $('#confirmationModal').on('hide', function () {
                    $('#confirmationModalSave').off('click');
                });

                $('#confirmationModal').modal('show');
            });
        });
        var brs = $.browser
        </asset:script>
    </head>
    <body class="content">
        <content tag="pageTitle">Projects</content>
        <content tag="adminButtonBar">
            <a href="${createLink(controller:'admin', action:'newProject')}" class="btn btn-sm btn-primary"><i class="glyphicon glyphicon-plus glyphicon glyphicon-white"></i>&nbsp;Create New Project</a>
        </content>

        <div>
            <table class="table table-bordered table-striped">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Description</th>
                    <th>Artifacts</th>
                    <th>Downloads</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                    <g:each in="${projects}" var="project">
                        <tr data-project-id="${project.id}" data-project-name="${project.name}">
                            <td><g:link action="projectArtifacts" params="${[projectId: project.id]}">${project.name}</g:link></td>
                            <td><small>${project.summary}</small></td>
                            <td>${project.artifacts.size()}</td>
                            <td>${project.artifacts*.downloadCount.sum() ?: 0}</td>
                            <td>
                                <g:form controller="admin" action="deleteProject" method="delete" name="delete-${project.id}" style="display:inline-block;" class="form-inline"><g:hiddenField name="projectId" value="${project.id}"/><button type="submit" id="submit-delete-${project.id}" class="btnDelete btn btn-danger btn-xs"><i class="glyphicon glyphicon-remove glyphicon glyphicon-white"></i></button></g:form>
                                <g:link controller="admin" action="editProject" params="[projectId: project.id]" role="button" class="btnEdit btn btn-default btn-xs"><i class="glyphicon glyphicon-edit"></i></g:link>
                                <g:link controller="admin" action="projectArtifacts" params="[projectId: project.id]" role="button" class="btnArtifacts btn btn-default btn-xs" title="Project artifacts"><i class="glyphicon glyphicon-list"></i></g:link>
                            </td>
                        </tr>
                    </g:each>
                </tbody>
            </table>
        </div>

        <!-- Confirm Modal -->
        <div id="confirmationModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="confirmationModel" aria-hidden="true">
            <div class="modal-dialog">

            <!-- Modal content-->
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                        <h3 id="confirmModalLabel">Delete project…</h3>
                    </div>
                    <div class="modal-body">
                        <p id="confirmModelText">Are you sure you want to delete this project?</p>
                    </div>
                    <div class="modal-footer">
                        <button id="confirmationModalClose" class="btn btn-default" data-dismiss="modal" aria-hidden="true">Close</button>
                        <button id="confirmationModalSave" class="btn btn-danger">Delete Project</button>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>