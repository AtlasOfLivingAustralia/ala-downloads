<!DOCTYPE html>
<html>
<g:set var="includeDeprecated" value="${params.boolean('includeDeprecated')}" />
%{-- Calculate params for the show/hide deprecated and sort table links --}%
<g:set var="hideDeprecatedParams" value="[projectName: projectInstance.name]" />
<g:set var="showDeprecatedParams" value="[projectName: projectInstance.name, includeDeprecated: true]" />
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
    <meta name="layout" content="main"/>
    <feed:meta kind="atom" version="1.0" mapping="feedByProjectName" params="${linkparams}" />
    <title>${projectInstance.name} downloads</title>
    <style>
    .sr-only {
        position: absolute;
        width: 1px;
        height: 1px;
        margin: -1px;
        padding: 0;
        overflow: hidden;
        clip: rect(0,0,0,0);
        border: 0
    }
    </style>
    </head>
<body class="content">
<div class="container-fluid">
    <legend>
        <table style="width: 100%">
            <tbody>
                <tr>
                    <td><link:projects>Projects</link:projects>&nbsp;»&nbsp;${projectInstance.name} <small>downloads</small></td>
                    <td style="text-align: right">
                        <span>
                            <auth:ifAnyGranted roles="${au.org.ala.web.CASRoles.ROLE_ADMIN}">
                                <g:link controller="admin" action="projectArtifacts" params="[projectId:projectInstance.id]" class="btn btn-sm">Project Admin</g:link>
                                <g:link controller="admin" action="uploadProjectArtifact" params="[projectId: projectInstance.id]" class="btn btn-sm btn-primary"><i class="glyphicon glyphicon-plus glyphicon glyphicon-white"></i> Upload a new artifact</g:link>
                            </auth:ifAnyGranted>
                        </span>
                    </td>
                </tr>
            </tbody>
        </table>
    </legend>
    <div class="well well-small">
        <markdown:renderHtml text="${projectInstance.description}" />
    </div>

    <auth:ifLoggedIn>
        <div>
            <g:link mapping="projectByName" params="${showHideParams}"><g:if test="${includeDeprecated}">Hide</g:if><g:else>Show</g:else> deprecated downloads</g:link>
        </div>
    </auth:ifLoggedIn>

    <auth:ifAnyGranted roles="${au.org.ala.web.CASRoles.ROLE_ADMIN}">
        <g:set var="buttonColWidth" value="116px" />
    </auth:ifAnyGranted>
    <auth:ifNotGranted roles="${au.org.ala.web.CASRoles.ROLE_ADMIN}">
        <g:set var="buttonColWidth" value="76px" />
    </auth:ifNotGranted>

    <table class="table table-bordered table-striped">
        <thead>
            <tr>
                <g:sortableColumn mapping="projectByName" params="${linkparams}" property="name" title="Filename"  />
                <g:sortableColumn mapping="projectByName" params="${linkparams}" property="fileSize" title="Size" />
                <g:sortableColumn mapping="projectByName" params="${linkparams}" property="summary" title="Summary"  />
                <g:sortableColumn mapping="projectByName" params="${linkparams}" property="dateCreated" title="Date uploaded" />
                <g:sortableColumn mapping="projectByName" params="${linkparams}" property="downloadCount" title="Downloads" />
                <th style="width:${buttonColWidth}"></th>
            </tr>
        </thead>
        <tbody>
        <g:each in="${artifacts}" var="artifact">
            <tr data-artifactId="${artifact.id}">
                <td><link:artifactDetailsByName projectName="${projectInstance.name}" file="${artifact.name}">${artifact.name}</link:artifactDetailsByName></td>
                <td><dl:sizeInBytes size="${artifact.fileSize}" /></td>
                <td><small>${artifact.summary}</small></td>
                <td><g:formatDate date="${artifact.dateCreated}" format="dd MMM, yyyy HH:mm:ss" /></td>
                <td>${artifact.downloadCount ?: 0}</td>
                <td>
                    <link:artifactDetailsByName projectName="${projectInstance.name}" file="${artifact.name}" attrs="[class:'btn btn-default btn-sm']"><i class="glyphicon glyphicon-info-sign"><span class="sr-only">File details</span></i></link:artifactDetailsByName>
                    <link:downloadByFile projectName="${projectInstance.name}" file="${artifact.name}" attrs="[class:'btn btn-default btn-sm', rel:'nofollow']"><i class="glyphicon glyphicon-download-alt"><span class="sr-only">Download file</span></i></link:downloadByFile>
                    <auth:ifAnyGranted roles="${au.org.ala.web.CASRoles.ROLE_ADMIN}"><g:link controller="admin" action="editProjectArtifact" params="[projectId: projectInstance.id, artifactId: artifact.id]" class="btn btn-default btn-sm"><i class="glyphicon glyphicon-edit"><span class="sr-only">Edit artifact</span></i></g:link></auth:ifAnyGranted>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>
</body>
</html>