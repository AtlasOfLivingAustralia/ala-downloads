<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>${projectInstance.name} downloads</title>
    <r:script>
            $(document).ready(function() {

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
<div>
    <h1>${projectInstance.name} downloads</h1>
    <div class="well well-small">
        ${projectInstance.description}
    </div>

    <table class="table table-bordered table-striped">
        <thead>
        <tr>
            <g:sortableColumn id="${projectInstance.id}" property="name" title="Filename" />
            <g:sortableColumn id="${projectInstance.id}" property="fileSize" title="Size" />
            <g:sortableColumn id="${projectInstance.id}" property="description" title="Description" />
            <g:sortableColumn id="${projectInstance.id}" property="dateCreated" title="Date uploaded" />
            <g:sortableColumn id="${projectInstance.id}" property="downloadCount" title="Downloads" />
            <th></th>
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
                <td>
                    <button class="btnArtifactDetails btn btn-small"><i class="icon-info-sign"></i></button>
                    <button class="btnDownloadArtifact btn btn-small"><i class="icon-download-alt"></i></button>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>
</body>
</html>