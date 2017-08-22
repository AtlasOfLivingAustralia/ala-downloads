
<%@ page import="au.org.ala.downloads.RecordCount" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="ala-main">
		<g:set var="entityName" value="${message(code: 'recordCount.label', default: 'RecordCount')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
		<style type="text/css">
		.glyphicon-list {
			color: #333;
		}
		.glyphicon-home {
			color: #333;
		}
		.glyphicon-pencil {
			color: #333;
		}
		</style>
	</head>
	<body>
		<a href="#show-recordCount" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>

		<div class="nav" role="navigation">
			<div class="row">
				<div class="col-md-2"><a href="${createLink(uri: '/')}"><i class="glyphicon glyphicon-home"></i> <g:message code="default.home.label"/></a></div>
				<div class="col-md-2"><g:link action="list"><i class="glyphicon glyphicon-list"></i> <g:message code="default.list.label" args="[entityName]" /></g:link></div>
				<div class="col-md-2"><g:link action="create"><i class="glyphicon glyphicon-pencil"></i> <g:message code="default.new.label" args="[entityName]" /></g:link></div>
			</div>
		</div>
		<div style="margin-top:12px"></div>
		<div id="show-recordCount" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list recordCount">
			
				<g:if test="${recordCountInstance?.code}">
				<li class="fieldcontain">
					<span id="code-label" class="property-label"><g:message code="recordCount.code.label" default="Code" /></span>
					
						<span class="property-value" aria-labelledby="code-label"><g:fieldValue bean="${recordCountInstance}" field="code"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${recordCountInstance?.records}">
				<li class="fieldcontain">
					<span id="records-label" class="property-label"><g:message code="recordCount.records.label" default="Records" /></span>
					
						<span class="property-value" aria-labelledby="records-label"><g:fieldValue bean="${recordCountInstance}" field="records"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${recordCountInstance?.id}" />
					<g:link class="btn btn-primary" action="edit" id="${recordCountInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="btn btn-default" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
