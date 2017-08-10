<%@ page import="au.org.ala.downloads.Download" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'download.label', default: 'Download')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>

		<style type="text/css">
			.glyphicon-list {
				color: #333;
			}
			.glyphicon-home {
				color: #333;
			}
		</style>

	</head>
	<body>
		<a href="#create-download" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<div class="row">
				<div class="row">
					<div class="col-md-2"><a href="${createLink(uri: '/')}"><i class="glyphicon glyphicon-home"></i> <g:message code="default.home.label"/></a></div>
					<div class="col-md-2"><g:link action="list"><i class="glyphicon glyphicon-list"></i> <g:message code="default.list.label" args="[entityName]" /></g:link></div>
				</div>
			</div>
		</div>

		<div style="margin-top:12px"></div>

		<div id="create-download" class="content scaffold-create" role="main">
			<h1><g:message code="default.create.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message alert-info" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors bean="${downloadInstance}">
            <div class="alert alert-warning alert-danger">
                <ul class="errors" role="alert">
                    <g:eachError bean="${downloadInstance}" var="error">
                    <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                    </g:eachError>
                </ul>
            </div>
			</g:hasErrors>
			<g:form action="save" >
				<fieldset class="form">
					<g:render template="form"/>
				</fieldset>
				<fieldset class="buttons">
					<g:submitButton name="create" class="btn btn-primary"  value="${message(code: 'default.button.create.label', default: 'Create')}" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
