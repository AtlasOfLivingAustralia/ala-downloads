
<%@ page import="au.org.ala.downloads.Download" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'download.label', default: 'Download')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-download" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-download" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<g:sortableColumn property="name" title="${message(code: 'download.name.label', default: 'Name')}" />
					
						<g:sortableColumn property="description" title="${message(code: 'download.description.label', default: 'Description')}" />
					
						<g:sortableColumn property="filePath" title="${message(code: 'download.fileUri.label', default: 'File Path')}" />
					
						<g:sortableColumn property="mimeType" title="${message(code: 'download.mimeType.label', default: 'Mime Type')}" />
					
						<g:sortableColumn property="fileSize" title="${message(code: 'download.fileSize.label', default: 'File Size')}" />
					
						<g:sortableColumn property="dateCreated" title="${message(code: 'download.dateCreated.label', default: 'Date Created')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${downloadInstanceList}" status="i" var="downloadInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${downloadInstance.id}">${fieldValue(bean: downloadInstance, field: "name")}</g:link></td>
					
						<td>${fieldValue(bean: downloadInstance, field: "description")}</td>
					
						<td>${fieldValue(bean: downloadInstance, field: "fileUri")}</td>
					
						<td>${fieldValue(bean: downloadInstance, field: "mimeType")}</td>
					
						<td>${fieldValue(bean: downloadInstance, field: "fileSize")}</td>
					
						<td><g:formatDate date="${downloadInstance.dateCreated}" /></td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${downloadInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
