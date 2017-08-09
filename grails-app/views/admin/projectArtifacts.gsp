<!DOCTYPE html>
<html>
<g:set var="includeDeprecated" value="${params.boolean('includeDeprecated')}" />
%{-- Calculate params for the show/hide deprecated and sort table links --}%
<g:set var="hideDeprecatedParams" value="[projectId: projectInstance.id]" />
<g:set var="showDeprecatedParams" value="[projectId: projectInstance.id, includeDeprecated: true]" />
<g:if test="${params.sort}">
    <g:set var="sortParams" value="[sort:params.sort, order: params.order]" />
</g:if>
<g:else>
    <g:set var="sortParams" value="[:]" />
</g:else>
<g:if test="${includeDeprecated}">
    <g:set var="showHideParams" value="${hideDeprecatedParams << sortParams}" />
    <g:set var="linkparams" value="${showDeprecatedParams}" />
</g:if>
<g:else>
    <g:set var="showHideParams" value="${showDeprecatedParams << sortParams}" />
    <g:set var="linkparams" value="${hideDeprecatedParams}" />
</g:else>
<head>
    <meta name="layout" content="adminLayout"/>
    <title>ALA Downloads - Administration - Project Downloads</title>
    <r:script disposition="head">
        jQuery(function($) {
            $('.btnDelete').click(function(e) {
                e.preventDefault();
                var form = $(e.target).closest('form');
                var name = $(e.target).closest('[data-artifact-name]').data('artifact-name');
                $('#confirmationModalSave').text("Delete " + name).on('click', function() {
                    form.submit();
                });
                $('#confirmModelText').text("Are you sure you want to delete " + name + "?");
                $('#confirmationModal').on('hide', function () {
                    $('#confirmationModalSave').off('click');
                })

                $('#confirmationModal').modal('show');
            });
        });
        var brs = $.browser
    </r:script>
</head>
<body class="content">
    <content tag="pageTitle">${projectInstance.name} artifacts</content>
    <content tag="adminButtonBar">
        <a href="${createLink(controller:'admin', action:'projectList')}" class="btn btn-default btn-sm"><i class="glyphicon glyphicon-chevron-left"></i>&nbsp;Back to project list</a>
        <link:projectByName projectName="${projectInstance.name}" attrs="[class:'btn btn-default btn-sm']"><i class="glyphicon glyphicon-circle-arrow-up"></i>&nbsp;Public project page</link:projectByName>
        <g:link controller="admin" action="editProject" params="[projectId:projectInstance.id]" class="btn btn-default btn-sm"><i class="glyphicon glyphicon-edit"></i>&nbsp;Edit project</g:link>
        <a href="${createLink(controller:'admin', action:'uploadProjectArtifact', params:[projectId: projectInstance.id])}" class="btn btn-default btn-sm btn-primary"><i class="glyphicon glyphicon-plus glyphicon glyphicon-white"></i>&nbsp;Upload a new artifact</a>
    </content>

    <div>
        <h4>${projectInstance.name} Artifacts</h4>
        <div class="well well-sm">
            <markdown:renderHtml text="${projectInstance.description}" />
        </div>

        <div>
            <g:link action="${actionName}" params="${showHideParams}"><g:if test="${includeDeprecated}">Hide</g:if><g:else>Show</g:else> deprecated downloads</g:link>
        </div>
        <table class="table table-bordered table-striped">
            <thead>
            <tr>
                <g:sortableColumn action="projectArtifacts" params="${linkparams}" property="name" title="Filename" />
                <g:sortableColumn action="projectArtifacts" params="${linkparams}" property="fileSize" title="Size" />
                <g:sortableColumn action="projectArtifacts" params="${linkparams}" property="summary" title="Summary" />
                <g:sortableColumn action="projectArtifacts" params="${linkparams}" property="dateCreated" title="Date uploaded" />
                <g:sortableColumn action="projectArtifacts" params="${linkparams}" property="downloadCount" title="Downloads" />
                <g:sortableColumn action="projectArtifacts" params="${linkparams}" property="deprecated" title="Deprecated" />
                <th style="width: 124px"></th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${artifacts}" var="artifact">
                <tr data-artifact-id="${artifact.id}" data-artifact-name="${artifact.name}">
                    <td><g:link controller="admin" action="editProjectArtifact" params="[artifactId: artifact.id]">${artifact.name}</g:link></td>
                    <td><dl:sizeInBytes size="${artifact.fileSize}" /></td>
                    <td><small>${artifact.summary}</small></td>
                    <td><g:formatDate date="${artifact.dateCreated}" format="dd MMM, yyyy HH:mm:ss" /></td>
                    <td>${artifact.downloadCount ?: 0}</td>
                    <td>${artifact.deprecated ?: "False"}</td>
                    <td>
                        <g:form controller="admin" action="deleteProjectArtifact" method="delete" name="delete-${artifact.id}" style="display:inline-block;" class="form-inline"><g:hiddenField name="artifactId" value="${artifact.id}"/><button type="submit" id="submit-delete-${artifact.id}" class="btnDelete btn btn-danger btn-xs"><i class="glyphicon glyphicon-remove glyphicon glyphicon-white"></i></button></g:form>
                        <link:artifactDetailsByName projectName="${artifact.project.name}" file="${artifact.name}" attrs="[class:'btn btn-default btn-xs']"><i class="glyphicon glyphicon-home"></i></link:artifactDetailsByName>
                        <link:downloadByFile projectName="${artifact.project.name}" file="${artifact.name}" attrs="[class:'btn btn-default btn-xs', rel: 'nofollow']"><i class="glyphicon glyphicon-download-alt"></i></link:downloadByFile>
                        <g:link controller="admin" action="editProjectArtifact" params="[artifactId: artifact.id]" class="btn btn-default btn-xs"><i class="glyphicon glyphicon-edit"></i></g:link>
                    </td>
                </tr>
            </g:each>
            </tbody>
        </table>
        <!-- Confirm Modal -->
        <div id="confirmationModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="confirmationModel" aria-hidden="true">
            <div class="modal-dialog">

                <!-- Modal content-->
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                        <h3 id="confirmModalLabel">Delete artifact…</h3>
                    </div>
                    <div class="modal-body">
                        <p id="confirmModelText">Are you sure you want to delete this artifact?</p>
                    </div>
                    <div class="modal-footer">
                        <button id="confirmationModalClose" class="btn btn-default " data-dismiss="modal" aria-hidden="true">Close</button>
                        <button id="confirmationModalSave" class="btn btn-danger">Delete Artifact</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>