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
                $('#name').val(name2);
                $('#filePath').val(el);
                $('#mimetype').val(fileObj.mimetype);
                $('#fileSize').val(fileObj.size);
                $('#fileLastModified').val(fileObj.date);
            }
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

<div class="fieldcontain ${hasErrors(bean: downloadInstance, field: 'filePath', 'error')} required">
	<label for="filePath">
		<g:message code="download.filePath.label" default="File Path" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="filePath" id="filePath" required="" value="${downloadInstance?.filePath}"/>
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


