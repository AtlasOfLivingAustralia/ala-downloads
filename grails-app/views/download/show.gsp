<%@ page import="au.org.ala.downloads.Download" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'download.label', default: 'Download')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-download" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-download" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list download">
			
				<g:if test="${downloadInstance?.name}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="download.name.label" default="Name" /></span>
					
						<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${downloadInstance}" field="name"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${downloadInstance?.description}">
				<li class="fieldcontain">
					<span id="description-label" class="property-label"><g:message code="download.description.label" default="Description" /></span>
					
						<span class="property-value" aria-labelledby="description-label"><g:fieldValue bean="${downloadInstance}" field="description"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${downloadInstance?.fileUri}">
				<li class="fieldcontain">
					<span id="fileUri-label" class="property-label"><g:message code="download.fileUri.label" default="File Uri" /></span>
					
						<span class="property-value" aria-labelledby="fileUri-label"><g:fieldValue bean="${downloadInstance}" field="fileUri"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${downloadInstance?.mimeType}">
				<li class="fieldcontain">
					<span id="mimeType-label" class="property-label"><g:message code="download.mimeType.label" default="Mime Type" /></span>
					
						<span class="property-value" aria-labelledby="mimeType-label"><g:fieldValue bean="${downloadInstance}" field="mimeType"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${downloadInstance?.fileSize}">
				<li class="fieldcontain">
					<span id="fileSize-label" class="property-label"><g:message code="download.fileSize.label" default="File Size" /></span>
					
						<span class="property-value" aria-labelledby="fileSize-label"><g:fieldValue bean="${downloadInstance}" field="fileSize"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${downloadInstance?.dateCreated}">
				<li class="fieldcontain">
					<span id="dateCreated-label" class="property-label"><g:message code="download.dateCreated.label" default="Date Created" /></span>
					
						<span class="property-value" aria-labelledby="dateCreated-label"><g:formatDate date="${downloadInstance?.dateCreated}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${downloadInstance?.lastUpdated}">
				<li class="fieldcontain">
					<span id="lastUpdated-label" class="property-label"><g:message code="download.lastUpdated.label" default="Last Updated" /></span>
					
						<span class="property-value" aria-labelledby="lastUpdated-label"><g:formatDate date="${downloadInstance?.lastUpdated}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${downloadInstance?.recordCount}">
				<li class="fieldcontain">
					<span id="recordCount-label" class="property-label"><g:message code="download.recordCount.label" default="Record Count" /></span>
					
						<g:each in="${downloadInstance.recordCount}" var="r">
						<span class="property-value" aria-labelledby="recordCount-label"><g:link controller="recordCount" action="show" id="${r.id}">${r?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${downloadInstance?.id}" />
					<g:link class="edit" action="edit" id="${downloadInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
