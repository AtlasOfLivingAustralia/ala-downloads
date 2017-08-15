<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Downloads | Atlas of Living Australia</title>
    <asset:script type="text/javascript">
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
                    reasonTypeId: $('#reasonTypeId').val()
                };
                var link = "${g.createLink(controller:"proxy", action:"download")}/" + $('#downloadForm').data("id") + "?" + $.param(params);
                    window.location.href = link;
                });

                if ("${flash.message}") {
                    $('#flashMessage').show();
                }
            });
    </asset:script>

    <style type="text/css">
    .glyphicon-list {
        color: #333;
    }
    </style>

</head>
<body class="nav-datasets">
<auth:ifAllGranted roles="ROLE_ADMIN">
    <div class="nav" role="navigation">
        <div class="row">
            <div class="col-md-2"><g:link controller="admin" action="projectList"><i class="glyphicon glyphicon-list"></i> <g:message code="default.list.label" args="['Project']" /></g:link></div>
            <div class="col-md-2"><g:link controller="download" action="list"><i class="glyphicon glyphicon-list"></i> <g:message code="default.list.label" args="['Download']" /></g:link></div>
        </div>
    </div>

    <div style="margin-top:12px"></div>
</auth:ifAllGranted>
<div id="page-body" role="main">
    <h1>Downloads</h1>
    <p class="lead">
        This page provides access to desktop software and large data exports.
        For developers interested in installing server components,
        please using the scripts in this <a href="https://github.com/gbif/ala-install">project</a>.
    </p>

    <h2>Software packages for desktop computers</h2>
    <g:render template="../project/table" />

    <h2>File downloads</h2>

    <div id="list-download" class="row-fluid content scaffold-list" role="main">
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
        <auth:ifNotLoggedIn>
            <div id="not-logged-in" class="message alert alert-error">
                These downloads require you be logged in to access them.
            </div>
        </auth:ifNotLoggedIn>
        <table class="table table-bordered table-striped">
            <thead>
            <tr>
                <g:sortableColumn property="name" title="${message(code: 'download.name.label', default: 'Name')}" />
                <g:sortableColumn property="description" title="${message(code: 'download.description.label', default: 'Description')}" />
                <g:sortableColumn property="dataLastModified" title="${message(code: 'download.dataLastModified.label', default: 'Last Updated')}" />
                <th></th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${downloadInstanceList}" status="i" var="downloadInstance">
                <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                    <td>
                        ${fieldValue(bean: downloadInstance, field: "name")}<br/>
                        File size: <dl:sizeInBytes size="${downloadInstance.fileSize}" />
                    </td>
                    <td class="description">
                        <a href="javascript:void(0);" class="showHideDescription pull-right">More details</a>
                        <g:set var="descLines" value="${(fieldValue(bean: downloadInstance, field: "description")).readLines()}"/>
                        <div class="intro">
                            <markdown:renderHtml text="${descLines.take(1).join("\n")}"/>
                        </div>
                        <div class="therest" style="display:none;">
                            <markdown:renderHtml text="${descLines.drop(1).join("\n")}"/>
                        </div>
                    </td>
                    <td><prettytime:display date="${downloadInstance.dataLastModified}" /></td>
                    <td>
                        <a href="#" class="btn btn-default downloadLink" data-id="${downloadInstance.id}"><i class="glyphicon glyphicon-download-alt"></i> Download</a>
                    </td>
                </tr>
            </g:each>
            </tbody>
        </table>
        <div class="pagination">
            <g:paginate total="${downloadInstanceTotal}" />
        </div>
    </div>

    <div class="modal fade" id="downloadModal">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>

                        <h3 id="myModalLabel">Download Confirmation</h3>
                    </div>

                    <div class="modal-body">
                        <auth:ifLoggedIn>
                            <p id="termsOfUseDownload">
                                By downloading this content you are agreeing to use it in accordance with the Atlas of Living Australia
                                <a href="http://www.ala.org.au/about/terms-of-use/#TOUusingcontent">Terms of Use</a> and any Data Provider
                            Terms associated with the data download.
                                <br><br>
                                Please provide the following details before downloading (* required):
                            </p>

                            <form id="downloadForm" class="form-horizontal" data-id="">
                                <div class="form-group">
                                    <label class="control-label col-md-3" for="reasonTypeId">Download reason *</label>

                                    <div class="col-md-9">
                                        <select name="reasonTypeId" id="reasonTypeId">
                                            <option value="">-- select a reason --</option>
                                            <g:each in="${reasons}">
                                                <option value="${it.id}">${it.name}</option>
                                            </g:each>
                                        </select>
                                    </div>
                                </div>

                                <div class="form-group">
                                    %{--<label class="control-label" for="email"></label>--}%
                                    <div class="col-md-offset-3 col-md-10">
                                        <input type="submit" value="Start Download" id="downloadStart"
                                               class="btn btn-default tooltips">
                                    </div>
                                </div>

                                <div id="statusMsg" style="text-align: center; font-weight: bold; "></div>
                            </form>
                        </auth:ifLoggedIn>
                        <auth:ifNotLoggedIn>
                            <div id="not-logged-in" class="message alert alert-error">
                                Please <a href="${hf.createLoginUrl()}">log in</a> to access this download.
                            </div>
                        </auth:ifNotLoggedIn>
                    %{--<p><img src="${resource(dir:'images',file:'spinner.gif')}" alt="spinner icon"/></p>--}%
                    </div>

                    <div class="modal-footer">
                        <button class="btn btn-default" data-dismiss="modal" aria-hidden="true">Cancel</button>
                        %{--<button class="btn btn-primary" id="saveEditors">Save changes</button>--}%
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>

<asset:script type="text/javascript">
    $( ".showHideDescription" ).click(function() {
        $(this).parent().children('.therest').toggle( "slow", function() {
            // Animation complete.
        });
    });
</asset:script>
</html>
