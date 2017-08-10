<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>${artifact?.project?.name} - ${artifact?.name} details</title>
        <r:script>
                $(document).ready(function() {
                });
        </r:script>

        <style>

            .dl-property-value {
                font-weight: bold;
            }
        </style>

    </head>
    <body class="content">
        <g:set var="downloadUrl" value="${createLink(mapping: 'downloadByFile', params: [projectName: artifact?.project?.name, file: artifact.name], absolute: true)}" />
        <div class="container-fluid">
            <legend>
                <table style="width: 100%">
                    <tbody>
                    <tr>
                        <td><link:projects>Projects</link:projects>&nbsp;»&nbsp;<link:projectByName projectName="${artifact?.project?.name}">${artifact?.project?.name}</link:projectByName>&nbsp;»&nbsp;${artifact?.name} <small>details</small></td>
                        <td style="text-align: right">
                            <span>
                                <auth:ifAllGranted roles="${au.org.ala.web.CASRoles.ROLE_ADMIN}">
                                    <g:link controller="admin" action="editProjectArtifact" params="[artifactId:artifact.id]" class="btn btn-default"><i class="glyphicon glyphicon-edit"></i> Update</g:link>
                                </auth:ifAllGranted>
                                <a href="${downloadUrl}" rel="nofollow" class="btn btn-primary"><i class="glyphicon glyphicon-download-alt glyphicon glyphicon-white"></i>&nbsp;Download</a>
                            </span>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </legend>

            <div class="row">
                <div class="col-md-3" style="text-align: center">
                    QR Code
                    <div>
                        <img src="${createLink(controller:'qrcode', action:'url', params:[u:downloadUrl, size: 150])}" />
                    </div>
                </div>
                <div class="col-md-9">
                    <markdown:renderHtml text="${artifact.description}" />
                </div>
            </div>
        </div>



        <table class="table table-bordered table-condensed">
            <tr>
                <td>Filename</td>
                <td class="dl-property-value">${artifact.name}</td>
            </tr>
            <tr>
                <td>Summary</td>
                <td class="dl-property-value">${artifact.summary}</td>
            </tr>
            <tr>
                <td>Size</td>
                <td class="dl-property-value"><dl:sizeInBytes size="${artifact.fileSize}" /></td>
            </tr>
            <tr>
                <td>Project</td>
                <td class="dl-property-value"><link:projectByName projectName="${artifact?.project?.name}">${artifact.project.name}</link:projectByName></td>
            </tr>

            <tr>
                <td>Uploaded</td>
                <td class="dl-property-value"><g:formatDate date="${artifact.dateCreated}" format="dd MMMM, yyyy HH:mm:ss"/></td>
            </tr>
            <tr>
                <td>SHA1 Hash</td>
                <td class="dl-property-value">${artifact.sha1hash}</td>
            </tr>
            <tr>
                <td>MD5 Hash</td>
                <td class="dl-property-value">${artifact.md5hash}</td>
            </tr>
            <tr>
                <td>Content type</td>
                <td class="dl-property-value">${artifact.mimeType}</td>
            </tr>
            <tr>
                <td>Deprecated</td>
                <td class="dl-property-value">${artifact.deprecated ? "Yes" : "No"}</td>
            </tr>
            <tr>
                <td>Download count</td>
                <td class="dl-property-value">${artifact.downloadCount ?: 0}</td>
            </tr>
            <tr>
                <td>Download URL</td>
                <td class="dl-property-value"><a href="${downloadUrl}" rel="nofollow">${downloadUrl}</a></td>
            </tr>
            <g:if test="${artifact.tags}">
                <tr>
                    <td>Labels</td>
                    <td>
                        <g:each in="${artifact.tags}" var="tag">
                            <div class="label">${tag}</div>
                        </g:each>
                    </td>
                </tr>
            </g:if>

        </table>
    </body>
</html>