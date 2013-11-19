<%@ page import="au.org.ala.downloads.Download" %>
<r:require modules="bootstrapCombobox"/>
<r:script>
    $(window).load(function() {
        var fileListingMap = {
            <g:each in="${fileListing}" var="file" status="i">
                "${file.path}": { size: ${file.size}, date: ${file.date}, mimetype: "${file.mimetype}" }${(i < fileListing.size() - 1) ? ',':''}
            </g:each>
        }
        $('.combobox').combobox({
            onSelect: function(el) {
                //$('#name').css("border","1px solid red");
                console.log("el",el, fileListingMap[el]);
                var fileObj = fileListingMap[el];
                var name = el.split("/").pop(); // get last el
                var name2 = name.split(".").shift(); // get first el
                console.log("name",name,name2,fileObj);
                $('#name').val(name2.replace(/[_\-]/g," "));
                $('#fileUri').val(el);
                $('#mimetype').val(fileObj.mimetype);
                $('#fileSize').val(fileObj.size);
                $('#fileLastModified').val(fileObj.date);
            }
        });

        $('#addRecordCount').click(function(e) {
            e.preventDefault();
            var lastItemIndex = $('.recordCountInput').length;
            var input = "Code: <input type='text' name='recordCount[" + lastItemIndex + "].code' class='input-small'/>";
            input += " Count: <input type='text' name='recordCount[" + lastItemIndex + "].records'  class='input-small'/>";
            $('.recordCountFields').append(input);
        });
    });
</r:script>

<div class="required">
    <label for="fileListing">
        <g:message code="download.fileListing.label" default="Available files" />
        <span class="required-indicator">*</span>
    </label>
    <select name="fileListing" id="fileListing" class="combobox input-xlarge">
        <option></option>
        <g:each in="${fileListing}" var="file">
            <option data-size="${file.size}" data-mimetype="${file.mimetype}" data-date="${file.date}">${file.path}</option>
        </g:each>
    </select>
</div>
<div class="fieldcontain ${hasErrors(bean: downloadInstance, field: 'name', 'error')} required">
	<label for="name">
		<g:message code="download.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="name" id="name" required="" value="${downloadInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: downloadInstance, field: 'description', 'error')} ">
	<label for="description">
		<g:message code="download.description.label" default="Description" />
		
	</label>
	<g:textArea name="description" id="description" cols="40" rows="5" value="${downloadInstance?.description}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: downloadInstance, field: 'fileUri', 'error')} required">
	<label for="fileUri">
		<g:message code="download.fileUri.label" default="File Uri" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="fileUri" id="fileUri" required="" value="${downloadInstance?.fileUri}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: downloadInstance, field: 'mimeType', 'error')} ">
	<label for="mimeType">
		<g:message code="download.mimeType.label" default="Mime Type" />
		
	</label>
	<g:textField name="mimeType" id="mimetype" value="${downloadInstance?.mimeType}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: downloadInstance, field: 'fileSize', 'error')} required">
	<label for="fileSize">
		<g:message code="download.fileSize.label" default="File Size" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="fileSize" id="fileSize" type="number" min="1" value="${downloadInstance.fileSize}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: downloadInstance, field: 'recordCount', 'error')} ">
	<label for="recordCount">
		<g:message code="download.recordCount.label" default="Record Count" />
		
	</label>
    %{--<div class="recordCountFields">--}%
        %{--<g:each in="${downloadInstance.recordCount}" var="rc" status="i">--}%
            %{--<g:field name="recordCount[${i}]" id="recordCount[${i}]" class="recordCountInput" type="text" value="${rc}"/><br/>--}%
        %{--</g:each>--}%
    %{--</div>--}%
    %{--<button class="btn btn-small" id="addRecordCount"><!--i class="icon-plus"></i--> Add row</button>--}%
	<g:select name="recordCount" from="${au.org.ala.downloads.RecordCount.list()}" multiple="multiple" optionKey="id" size="5" value="${downloadInstance?.recordCount*.id}" class="many-to-many"/>
</div>
<div>&nbsp;</div>
