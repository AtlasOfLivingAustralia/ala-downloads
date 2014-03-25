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
        <content tag="pageTitle">${artifact?.project?.name} - ${artifact?.name} details</content>
        <g:set var="downloadUrl" value="${createLink(controller:'project', action:'downloadArtifact', id: artifact.id, absolute: true)}" />
        <div>
            <h1>${artifact?.name}</h1>

            <div class="row-fluid">
                <div class="span3" style="text-align: center">
                    QR Code
                    <div>
                        <img src="${createLink(controller:'qrcode', action:'url', params:[u:downloadUrl, size: 150])}" />
                    </div>
                </div>
                <div class="span9">
                    ${artifact.description}
                    <hr />
                    <button class="btn btn-primary pull-right"><i class="icon-download-alt icon-white"></i>&nbsp;Download</button>
                </div>
            </div>
        </div>



        <table class="table table-bordered table-condensed">
            <tr>
                <td>Filename</td>
                <td class="dl-property-value">${artifact.name}</td>
            </tr>
            <tr>
                <td>Size</td>
                <td class="dl-property-value"><dl:sizeInBytes size="${artifact.fileSize}" /></td>
            </tr>
            <tr>
                <td>Project</td>
                <td class="dl-property-value"><g:link action="artifactList" id="${artifact.project.id}">${artifact.project.name}</g:link></td>
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
                <td class="dl-property-value"><a href="${downloadUrl}">${downloadUrl}</a></td>
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