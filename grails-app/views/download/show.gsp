<%@ page import="au.org.ala.downloads.Download" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'download.label', default: 'Download')}" />
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

        .row-grid  [class*="col-"] {
               margin-bottom: 15px;         
         }


		</style>
	</head>
	<body>
		<a href="#show-download" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<div class="row">
				<div class="col-md-2"><a href="${createLink(uri: '/')}"><i class="glyphicon glyphicon-home"></i> <g:message code="default.home.label"/></a></div>
				<div class="col-md-2"><g:link action="list"><i class="glyphicon glyphicon-list"></i> <g:message code="default.list.label" args="[entityName]" /></g:link></div>
				<div class="col-md-2"><g:link action="create"><i class="glyphicon glyphicon-pencil"></i> <g:message code="default.new.label" args="[entityName]" /></g:link></div>
			</div>
		</div>

		<div style="margin-top:12px"></div>

		<div id="show-download" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>

			<div class="row row-grid">

			<g:if test="${downloadInstance?.name}">
				<div class="col-md-2 text-right">
					<span id="name-label" class="property-label"><g:message code="download.name.label" default="Name" /></span></div>
				<div class="col-md-10">
					<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${downloadInstance}" field="name"/></span></div>
			</g:if>

			<g:if test="${downloadInstance?.description}">
				<div class="col-md-2 text-right">
					<span id="description-label" class="property-label"><g:message code="download.description.label" default="Description" /></div>
				<div class="col-md-10">
					<span class="property-value" aria-labelledby="description-label">
						<markdown:renderHtml text="${downloadInstance.description}"/>
					</span></div>
			</g:if>

			<g:if test="${downloadInstance?.fileUri}">
				<div class="col-md-2 text-right">
					<span id="fileUri-label" class="property-label"><g:message code="download.fileUri.label" default="File Uri" /></span></div>
				<div class="col-md-10">
					<span class="property-value" aria-labelledby="fileUri-label"><g:fieldValue bean="${downloadInstance}" field="fileUri"/></span>
				</div>
			</g:if>

			<g:if test="${downloadInstance?.mimeType}">
				<div class="col-md-2 text-right">
					<span id="mimeType-label" class="property-label"><g:message code="download.mimeType.label" default="Mime Type" /></span></div>
				<div class="col-md-10">
					<span class="property-value" aria-labelledby="mimeType-label"><g:fieldValue bean="${downloadInstance}" field="mimeType"/></span>
				</div>
			</g:if>

			<g:if test="${downloadInstance?.fileSize}">
				<div class="col-md-2 text-right">
					<span id="fileSize-label" class="property-label"><g:message code="download.fileSize.label" default="File Size" /></span></div>
				<div class="col-md-10">
					<span class="property-value" aria-labelledby="fileSize-label"><g:fieldValue bean="${downloadInstance}" field="fileSize"/></span>
				</div>
			</g:if>

			<g:if test="${downloadInstance?.dateCreated}">
				<div class="col-md-2 text-right">
					<span id="dateCreated-label" class="property-label"><g:message code="download.dateCreated.label" default="Date Created" /></span></div>
				<div class="col-md-10">
					<span class="property-value" aria-labelledby="dateCreated-label"><g:formatDate date="${downloadInstance?.dateCreated}" /></span>
				</div>
			</g:if>

			<g:if test="${downloadInstance?.lastUpdated}">
				<div class="col-md-2 text-right">
					<span id="lastUpdated-label" class="property-label"><g:message code="download.lastUpdated.label" default="Last Updated" /></span></div>
				<div class="col-md-10">
					<span class="property-value" aria-labelledby="lastUpdated-label"><g:formatDate date="${downloadInstance?.lastUpdated}" /></span>
				</div>
			</g:if>

			<g:if test="${downloadInstance?.recordCount}">
				<div class="col-md-2 text-right">
					<span id="recordCount-label" class="property-label"><g:message code="download.recordCount.label" default="Record Count" /></span></div>

					<g:each in="${downloadInstance.recordCount}" var="r">
						<div class="col-md-10"><span class="property-value" aria-labelledby="recordCount-label"><g:link controller="recordCount" action="show" id="${r.id}">${r?.encodeAsHTML()}</g:link></span> </div>
					</g:each>

			</div>
			</g:if>

			</div>

			<g:form>
				<div class="form-group">
				    <div class="col-md-10">
					<g:link class="btn btn-default" action="edit" id="${downloadInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="btn btn-default" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
                    </div>
                </div>
			</g:form>
		</div>
	</body>
</html>
