<%@ page import="grails.converters.JSON; au.org.ala.downloads.Download" %>
<r:require modules="bootstrapCombobox,jqueryTmpl"/>
<r:style>
#parent{
  position: relative
}

#right-sidebar{
  position: absolute;
  right: 0;
  height: 100%;
}

.main-content{
}
</r:style>
<r:script>

    $(window).load(function() {
        var initialRecords = <dl:json value="${metadataRecords}" />;
        var fileUri = $('#fileUri');
        fileUri.on('input propertychange', function(e) {
            fileUriChangeEventHandler(e,
            function (data, textStatus, jqXHR) {
                onResponse(data, null);
            });
        });
        fileUri.change(function(e) {
            fileUriChangeEventHandler(e,
            function (data, textStatus, jqXHR) {
                onResponse(data, function() { alert("The Data URI returned status code: " + data.statusCode); } );
            },
            function (jqXHR, textStatus, errorThrown) {
                alert("The Data URI request failed!  Status: " + textStatus + ", Error: " + errorThrown);
            });
        });
        $('#metadataUri').change(function (e) {
            var metadataChecker = '<g:createLink controller="download" action="checkMetadataUri" />';
            var uri = e.target.value;
            if (validateURL(uri)) {
                $.ajax({
                    url: metadataChecker,
                    data: {uri: uri},
                    dataType: 'json'
                }).done(function(data, textStatus, jqXHR) {
                    if (data.length == 0) {
                        alert("The Metadata URI returned no data!");
                        $("#metadataTable").empty();
                    } else if (jqXHR.status === 200) {
                        updateMetadataUriFields(data)
                    } else {
                        alert("The Metadata URI returned status code: " + jqXHR.status);
                        $("#metadataTable").empty();
                    }
                }).fail(function(jqXHR, textStatus, errorThrown) {
                    alert("The Metadata URI request failed!  Status: " + textStatus + ", Error: " + errorThrown);
                    $("#metadataTable").empty();
                })
            } else {
                alert ("Metadata URI is invalid!");
                $("#metadataTable").empty();
            }
        });
        function validateURL(textval) {
            var urlregex = new RegExp(
                "^(http|https|)\://([a-zA-Z0-9\.\-]+(\:[a-zA-Z0-9\.&amp;%\$\-]+)*@)*((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0-9\-]+\.)*[a-zA-Z0-9\-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(\:[0-9]+)*(/($|[a-zA-Z0-9\.\,\?\'\\\+&amp;%\$#\=~_\-]+))*$");
          return urlregex.test(textval);
        }
        function fileUriChangeEventHandler(e, done, fail) {
            var val = e.target.value;
            if (validateURL(val)) {
                var ajax = checkHead(val);
                if (done) ajax.done(done);
                if (fail) ajax.fail(fail);
            }
        }
        function checkHead(uri) {
            var headChecker = '<g:createLink controller="download" action="checkHead" />';
            return $.ajax({
                    url: headChecker,
                    data: {uri : uri},
                    dataType: 'json'
                })
        }
        function updateDataUriFields(data) {
            if (!$('#name').val()) $('#name').val(data.filename.replace(/[_\-]/g," "));
            $('#mimetype').val(data.contentType);
            $('#fileSize').val(data.contentLength);
            $('#dataLastModified').val(data.lastModified);
            $('#dataEtag').val(data.etag);
            $('#dataMd5').val(data.contentMd5);
        }
        function updateMetadataUriFields(data) {
            $("#metadataTable").loadTemplate("#recordCountTmpl", data);
        }
        function onResponse(data, nonSuccessCodeFn) {
                if (data.statusCode >= 200 && data.statusCode < 300) {
                    updateDataUriFields(data);
                } else if (nonSuccessCodeFn) {
                    nonSuccessCodeFn();
                }
            }
        if (initialRecords) updateMetadataUriFields(initialRecords);
    });

</r:script>

<div id="parent" class="row">
    <div class="col-md-8 main-content">

        <div class="fieldcontain ${hasErrors(bean: downloadInstance, field: 'fileUri', 'has-error')} required">
            <div class="form-group row">
                <div class="col-md-8">
                    <label for="fileUri">
                        <g:message code="download.fileUri.label" default="File Uri" />
                        <span class="required-indicator">*</span>
                    </label>
                    <g:field name="fileUri" id="fileUri" required="" type="url" value="${downloadInstance?.fileUri}" class="form-control" />
                </div>
            </div>
        </div>

        <div class="fieldcontain ${hasErrors(bean: downloadInstance, field: 'metadataUri', 'error')} required">
            <div class="form-group row">
                <div class="col-md-8">
                    <label for="metadataUri">
                        <g:message code="download.metadataUri.label" default="Metadata Uri" />
                        <span class="required-indicator">*</span>
                    </label>
                    <g:field name="metadataUri" id="metadataUri" type="url" value="${downloadInstance?.metadataUri}" class="form-control" />
                </div>
            </div>
        </div>

        <div class="fieldcontain ${hasErrors(bean: downloadInstance, field: 'name', 'error')} required">
            <div class="form-group row">
                <div class="col-md-8">
                    <label for="name">
                        <g:message code="download.name.label" default="Name" />
                        <span class="required-indicator">*</span>
                    </label>
                    <g:textField name="name" id="name" required="" class="form-control" value="${downloadInstance?.name}"/>
                </div>
            </div>
        </div>

        <div class="fieldcontain ${hasErrors(bean: downloadInstance, field: 'description', 'error')} ">
            <div class="form-group row">
                <div class="col-md-8">
                <label for="description">
                    <g:message code="download.description.label" default="Description (use markdown if formatting is required)" />

                </label>
                <g:textArea name="description" id="description" cols="40" rows="5" value="${downloadInstance?.description}" class="form-control"/>
                </div>
            </div>
        </div>

        <div class="fieldcontain ${hasErrors(bean: downloadInstance, field: 'mimeType', 'error')} ">
            <div class="form-group row">
                <div class="col-md-4">
                    <label for="mimeType">
                        <g:message code="download.mimeType.label" default="Mime Type" />

                    </label>
                    <g:textField name="mimeType" id="mimetype" readonly="true" class="form-control" value="${downloadInstance?.mimeType}"/>
                </div>
            </div>
        </div>

        <div class="fieldcontain ${hasErrors(bean: downloadInstance, field: 'fileSize', 'error')} required">
            <div class="form-group row">
                <div class="col-md-4">
                    <label for="fileSize">
                        <g:message code="download.fileSize.label" default="File Size" />
                        <span class="required-indicator">*</span>
                    </label>
                    <g:field name="fileSize" id="fileSize" type="number" min="1" class="form-control" readonly="true" value="${downloadInstance.fileSize}"/>
                </div>
            </div>
        </div>

        <div class="fieldcontain">
            <div class="form-group row">
                <div class="col-md-4">
                    <label for="dataLastModified">
                        <g:message code="download.dataLastModified.label" default="Last Modified" />
                    </label>
                    <g:textField name="dataLastModified" id="dataLastModified" class="form-control" readonly="true" value="${g.formatDate(format:"yyyy-MM-dd'T'HH:mm:ss'Z'", date:downloadInstance.dataLastModified)}" />

                </div>
            </div>
        </div>

        <div class="fieldcontain">
            <div class="form-group row">
                <div class="col-md-4">
                    <label for="dataEtag">
                        <g:message code="download.dataEtag.label" default="ETag" />
                    </label>
                    <g:textField name="dataEtag" id="dataEtag" class="form-control" readonly="true" value="${downloadInstance.dataEtag}"/>
                </div>
            </div>
        </div>

        <div class="fieldcontain">
            <div class="form-group row">
                <div class="col-md-4">
                    <label for="dataMd5">
                        <g:message code="download.dataMd5.label" default="MD5" />
                    </label>
                    <g:textField name="dataMd5" id="dataMd5" class="form-control" readonly="true" value="${downloadInstance.dataMd5}"/>
                </div>
            </div>
        </div>

    </div>
    <div id="right-sidebar" class="col-md-4" style="overflow-y: scroll;">
        <div id="metadataResults">
            <h4>Current record count from metadata URI</h4>
            <table class="table table-striped table-condensed">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Count</th>
                </tr>
                </thead>
                <tbody id="metadataTable">
                </tbody>
            </table>
        </div>
    </div>
</div>
<script type="text/html" id="recordCountTmpl">
    <tr><td data-content="id"></td><td data-content="count"></td></tr>
</script>