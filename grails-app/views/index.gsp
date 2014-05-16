<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<title>ALA Downloads</title>
        <r:script>
            $(window).load(function() {
                // download link in table
                $('.downloadLink').click(function(e) {
                    e.preventDefault();
                    $('#downloadForm').data("id", $(this).data("id")); // store the ID in the form dom el
                    $('#downloadModal').modal('show');
                });

                // start Download button
                $('#downloadStart').click(function(e) {
                    e.preventDefault();
                    $('#flashMessage span').empty();
                    $('#flashMessage span').html("Download has commenced");
                    $('#flashMessage').fadeIn();
                    $('#downloadModal').modal('hide');
                    var params = {
                        userEmail: $('#email').val(),
                        reasonTypeId: $('#reasonTypeId').val()
                    };
                    var link = "${g.createLink(controller:"proxy", action:"download")}/" + $('#downloadForm').data("id") + "?" + $.param(params);
                    window.location.href = link;
                });

                if ("${flash.message}") {
                    $('#flashMessage').show();
                }
            });
        </r:script>
	</head>
	<body class="nav-datasets">
        <auth:ifAllGranted roles="ROLE_ADMIN">
            <div class="nav" role="navigation">
                <ul>
                    <li><g:link class="list" controller="admin" action="projectList"><g:message code="default.list.label" args="[project]" /></g:link></li>
                    <li><g:link class="list" controller="download" action="list"><g:message code="default.list.label" args="[download]" /></g:link></li>
                </ul>
            </div>
        </auth:ifAllGranted>
		<div id="page-body" role="main">
			<h1>ALA Downloads</h1>
            <h2>Projects</h2>
            <g:render template="../project/table" />

            <h2>Record downloads</h2>

            <div id="list-download" class="row-fluid scaffold-list" role="main">
                <div id="flashMessage" class="hide message alert alert-info span6">
                    <button type="button" class="close" onclick="$(this).parent().hide()">×</button>
                    <span>${flash.message}</span>
                </div>
                <g:hasErrors bean="${flash.errors}">
                    <div id="flashMessage" class="message alert alert-error span6">
                        <button type="button" class="close" onclick="$(this).parent().hide()">×</button>
                        <g:renderErrors bean="${flash.errors}" as="list" />
                    </div>
                </g:hasErrors>
                <table>
                    <thead>
                    <tr>
                        <g:sortableColumn property="name" title="${message(code: 'download.name.label', default: 'Name')}" />

                        <g:sortableColumn property="description" title="${message(code: 'download.description.label', default: 'Description')}" />

                        %{--<g:sortableColumn property="fileUri" title="${message(code: 'download.fileUri.label', default: 'File Path')}" />--}%

                        <g:sortableColumn property="mimeType" title="${message(code: 'download.mimeType.label', default: 'File Type')}" />

                        <g:sortableColumn property="fileSize" title="${message(code: 'download.fileSize.label', default: 'File Size')}" />

                        <g:sortableColumn property="dataLastModified" title="${message(code: 'download.dataLastModified.label', default: 'Last Updated')}" />

                        %{--<g:sortableColumn property="dateCreated" title="${message(code: 'download.dateCreated.label', default: 'Date Created')}" />--}%

                    </tr>
                    </thead>
                    <tbody>
                    <g:each in="${downloadInstanceList}" status="i" var="downloadInstance">
                        <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                            <td>
                                %{--<g:link class="downloadLink" controller="proxy" action="readFile" id="${downloadInstance.id}">${fieldValue(bean: downloadInstance, field: "name")}</g:link>--}%
                                <a href="#" class="downloadLink" data-id="${downloadInstance.id}">${fieldValue(bean: downloadInstance, field: "name")}</a>
                            </td>
                            <td>${fieldValue(bean: downloadInstance, field: "description")}</td>
                            %{--<td>${fieldValue(bean: downloadInstance, field: "fileUri")}</td>--}%
                            <td>${fieldValue(bean: downloadInstance, field: "mimeType")}</td>
                            <td><dl:sizeInBytes size="${downloadInstance.fileSize}" /></td>
                            <td><prettytime:display date="${downloadInstance.dataLastModified}" /></td>
                            %{--<td><g:formatDate date="${downloadInstance.dateCreated}" /></td>--}%

                        </tr>
                    </g:each>
                    </tbody>
                </table>
                <div class="pagination">
                    <g:paginate total="${downloadInstanceTotal}" />
                </div>
            </div>
            <div class="modal hide fade" id="downloadModal">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                    <h3 id="myModalLabel">Download Confirmation</h3>
                </div>
                <div class="modal-body">
                    <p id="termsOfUseDownload">
                        By downloading this content you are agreeing to use it in accordance with the Atlas of Living Australia
                        <a href="http://www.ala.org.au/about/terms-of-use/#TOUusingcontent">Terms of Use</a> and any Data Provider
                        Terms associated with the data download.
                        <br><br>
                        Please provide the following details before downloading (* required):
                    </p>
                    <form id="downloadForm" class="form-horizontal" data-id="">
                        <div class="control-group">
                            <label class="control-label" for="email">Email *</label>
                            <div class="controls">
                                <input type="text" id="email" placeholder="Email">
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label" for="reasonTypeId">Download reason *</label>
                            <div class="controls">
                                <select name="reasonTypeId" id="reasonTypeId">
                                    <option value="">-- select a reason --</option>
                                    <option value="0">conservation management/planning</option>
                                    <option value="1">biosecurity management, planning</option>
                                    <option value="2">environmental impact, site assessment</option>
                                    <option value="3">education</option>
                                    <option value="4">scientific research</option>
                                    <option value="5">collection management</option>
                                    <option value="6">other</option>
                                    <option value="7">ecological research</option>
                                    <option value="8">systematic research</option>
                                    <option value="9">other scientific research</option>
                                    <option value="10">testing</option>
                                </select>
                            </div>
                        </div>

                        <div class="control-group">
                            <label class="control-label" for="email"></label>
                            <div class="controls">
                                <input type="submit" value="Start Download" id="downloadStart" class="btn tooltips">
                            </div>
                        </div>

                        <div id="statusMsg" style="text-align: center; font-weight: bold; "></div>
                    </form>
                    %{--<p><img src="${resource(dir:'images',file:'spinner.gif')}" alt="spinner icon"/></p>--}%
                </div>
                <div class="modal-footer">
                    <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
                    %{--<button class="btn btn-primary" id="saveEditors">Save changes</button>--}%
                </div>
            </div>
		</div>
	</body>
</html>
