
<%@ page import="au.org.ala.downloads.RecordCount" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'recordCount.label', default: 'RecordCount')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
		<style type="text/css">
		.glyphicon-home {
			color: #333;
		}
		.glyphicon-pencil {
			color: #333;
		}
		</style>
	</head>
	<body>
		<a href="#list-recordCount" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>

		<div class="nav" role="navigation">
			<div class="row">
				<div class="col-md-2"><a href="${createLink(uri: '/')}"><i class="glyphicon glyphicon-home"></i> <g:message code="default.home.label"/></a></div>
				<div class="col-md-2"><g:link action="create"><i class="glyphicon glyphicon-pencil"></i> <g:message code="default.new.label" args="[entityName]" /></g:link></div>
			</div>
		</div>
		<div style="margin-top:12px"></div>
		<div id="list-recordCount" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<g:sortableColumn property="code" title="${message(code: 'recordCount.code.label', default: 'Code')}" />
					
						<g:sortableColumn property="records" title="${message(code: 'recordCount.records.label', default: 'Records')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${recordCountInstanceList}" status="i" var="recordCountInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${recordCountInstance.id}">${fieldValue(bean: recordCountInstance, field: "code")}</g:link></td>
					
						<td>${fieldValue(bean: recordCountInstance, field: "records")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${recordCountInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
