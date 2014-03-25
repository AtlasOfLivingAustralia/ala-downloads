<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="adminLayout"/>
    <title>ALA Downloads - Administration - Project Downloads</title>
    <r:script>
            $(document).ready(function() {

                $(".btnDelete").click(function(e) {
                    e.preventDefault();
                    var artifactId = $(this).closest("[artifactId").attr("artifactId");
                    if (artifactId) {
                        window.location = "${createLink(controller:'admin', action:'deleteProjectArtifact')}?artifactId=" + artifactId;
                    }
                });

                $(".btnArtifactDetails").click(function(e) {
                    e.preventDefault();
                    var artifactId = $(this).closest("[artifactId").attr("artifactId");
                    if (artifactId) {
                        window.location ="${createLink(controller:'project', action:'artifactDetails')}/" + artifactId;
                    }
                });

                $(".btnDownloadArtifact").click(function(e) {
                    e.preventDefault();
                    var artifactId = $(this).closest("[artifactId").attr("artifactId");
                    if (artifactId) {
                        window.location ="${createLink(controller:'project', action:'downloadArtifact')}/" + artifactId;
                    }
                });

            });
    </r:script>
</head>
<body class="content">
<content tag="pageTitle">${projectInstance.name} artifacts</content>
<content tag="adminButtonBar">
    <a href="${createLink(controller:'admin', action:'projectList')}" class="btn btn-small"><i class="icon-chevron-left"></i>&nbsp;Back to project list</a>
    <a href="${createLink(controller:'admin', action:'uploadProjectArtifact', params:[projectId: projectInstance.id])}" class="btn btn-small btn-primary"><i class="icon-plus icon-white"></i>&nbsp;Upload a new artifact</a>
</content>

<div>
    <h4>${projectInstance.name} Artifacts</h4>
    <div class="well well-small">
        ${projectInstance.description}
    </div>

    <table class="table table-bordered table-striped">
        <thead>
        <tr>
            <g:sortableColumn action="projectArtifacts" params="${[projectId: projectInstance.id]}" property="name" title="Filename" />
            <g:sortableColumn action="projectArtifacts" params="${[projectId: projectInstance.id]}" property="fileSize" title="Size" />
            <g:sortableColumn action="projectArtifacts" params="${[projectId: projectInstance.id]}" property="description" title="Description" />
            <g:sortableColumn action="projectArtifacts" params="${[projectId: projectInstance.id]}" property="dateCreated" title="Date uploaded" />
            <g:sortableColumn action="projectArtifacts" params="${[projectId: projectInstance.id]}" property="downloadCount" title="Downloads" />
            <g:sortableColumn action="projectArtifacts" params="${[projectId: projectInstance.id]}" property="deprecated" title="Deprecated" />
            <th style="width: 100px"></th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${artifacts}" var="artifact">
            <tr artifactId="${artifact.id}">
                <td>${artifact.name}</td>
                <td><dl:sizeInBytes size="${artifact.fileSize}" /></td>
                <td><small>${artifact.description}</small></td>
                <td><g:formatDate date="${artifact.dateCreated}" format="dd MMM, yyyy HH:mm:ss" /></td>
                <td>${artifact.downloadCount ?: 0}</td>
                <td>${artifact.deprecated ?: "False"}</td>
                <td>
                    <button class="btnDelete btn btn-danger btn-mini"><i class="icon-remove icon-white"></i></button>
                    <button class="btnArtifactDetails btn btn-mini"><i class="icon-home"></i></button>
                    <button class="btnDownloadArtifact btn btn-mini"><i class="icon-download-alt"></i></button>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>
</body>
</html>