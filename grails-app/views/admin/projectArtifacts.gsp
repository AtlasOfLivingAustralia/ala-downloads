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
                        alert("Delete artifact " + artifactId);
                    }
                });

                $(".btnEdit").click(function(e) {
                    e.preventDefault();
                    var artifactId = $(this).closest("[artifactId").attr("artifactId");
                    if (artifactId) {
                        alert("Edit artifact " + artifactId);
                    }
                });

            });
    </r:script>
</head>
<body class="content">
<content tag="pageTitle">${projectInstance.name} artifacts</content>
<content tag="adminButtonBar">
    <a href="${createLink(controller:'admin', action:'projectList')}" class="btn btn-small"><i class="icon-chevron-left"></i>&nbsp;Back to project list</a>
    <a href="${createLink(controller:'admin', action:'uploadProjectArtifact')}" class="btn btn-small btn-primary"><i class="icon-plus icon-white"></i>&nbsp;Upload a new artifact</a>
</content>

<div>
    <h4>${projectInstance.name} Artifacts</h4>
    <div class="well well-small">
        ${projectInstance.description}
    </div>

    <table class="table table-bordered table-striped">
        <thead>
        <tr>
            <th>Name</th>
            <th>Description</th>
            <th>Date uploaded</th>
            <th>Download count</th>
            <th>Deprecated</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${projectInstance.artifacts}" var="artifact">
            <tr artifactId="${artifact.id}">
                <td>${artifact.name}</td>
                <td><g:formatDate date="${artifact.dateCreated}" format="dd MMM, yyyy" /></td>
                <td><small>${artifact.description}</small></td>
                <td>${artifact.downloadCount}</td>
                <td>
                    <button class="btnDelete btn btn-danger btn-small"><i class="icon-remove icon-white"></i></button>
                    <button class="btnEdit btn btn-small"><i class="icon-edit"></i></button>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>
</body>
</html>