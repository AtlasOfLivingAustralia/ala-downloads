<%@ page import="au.org.ala.downloads.RecordCount" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'recordCount.label', default: 'RecordCount')}" />
		<title><g:message code="default.edit.label" args="[entityName]" /></title>

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
		<a href="#edit-recordCount" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>

		<div class="nav" role="navigation">
			<div class="row">
				<div class="col-md-2"><a href="${createLink(uri: '/')}"><i class="glyphicon glyphicon-home"></i> <g:message code="default.home.label"/></a></div>
				<div class="col-md-2"><g:link action="list"><i class="glyphicon glyphicon-list"></i> <g:message code="default.list.label" args="[entityName]" /></g:link></div>
				<div class="col-md-2"><g:link action="create"><i class="glyphicon glyphicon-pencil"></i> <g:message code="default.new.label" args="[entityName]" /></g:link></div>
			</div>
		</div>
		<div style="margin-top:12px"></div>
		<div id="edit-recordCount" class="content scaffold-edit" role="main">
			<h1><g:message code="default.edit.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors bean="${recordCountInstance}">
			<ul class="errors" role="alert">
				<g:eachError bean="${recordCountInstance}" var="error">
				<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
			</g:hasErrors>
			<g:form method="post" >
				<g:hiddenField name="id" value="${recordCountInstance?.id}" />
				<g:hiddenField name="version" value="${recordCountInstance?.version}" />
				<fieldset class="form">
					<g:render template="form"/>
				</fieldset>
				<fieldset class="buttons">
					<g:actionSubmit class="btn btn-primary"  action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" />
					<g:actionSubmit class="btn btn-default" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" formnovalidate="" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
