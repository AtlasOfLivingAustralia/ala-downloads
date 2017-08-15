<%@ page import="au.org.ala.downloads.RecordCount" %>



<div class="fieldcontain ${hasErrors(bean: recordCountInstance, field: 'code', 'error')} ">
	<label for="code">
		<g:message code="recordCount.code.label" default="Code" />
		
	</label>
	<g:textField name="code" value="${recordCountInstance?.code}" class="form-control" />
</div>

<div class="fieldcontain ${hasErrors(bean: recordCountInstance, field: 'records', 'error')} required">
	<label for="records">
		<g:message code="recordCount.records.label" default="Records" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="records" type="number" value="${recordCountInstance.records}" required="" class="form-control" />
</div>

