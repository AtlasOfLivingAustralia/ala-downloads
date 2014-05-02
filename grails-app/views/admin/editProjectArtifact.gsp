<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="adminLayout"/>
    <title>ALA Downloads - Administration - Edit Artifact</title>
</head>
<body class="content">
<content tag="pageTitle">Edit Artifact</content>
<g:hasErrors bean="${artifactInstance}">
    <ul class="errors" role="alert">
        <g:eachError bean="${artifactInstance}" var="error">
            <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
        </g:eachError>
    </ul>
</g:hasErrors>
<g:form action="updateProjectArtifact" method="PUT" class="form-horizontal">
    <g:hiddenField name="artifactId" value="${artifactInstance?.id}" />
    <g:hiddenField name="version" value="${artifactInstance?.version}" />

    <div class="control-group">
        <label class="control-label" for="name">Name</label>
        <div class="controls">
            <g:textField name="name" readonly="true" value="${artifactInstance?.name}" />
        </div>
    </div>

    <div class="control-group ${hasErrors(bean: artifactInstance, field: 'summary', 'error')}">
        <label class="control-label" for="summary">Summary</label>
        <div class="controls">
            <g:textField class="input-xxlarge" maxlength="100" name="summary" value="${artifactInstance?.summary}" />
        </div>
    </div>

    <div class="control-group ${hasErrors(bean: artifactInstance, field: 'description', 'error')}">
        <label class="control-label" for="description">Description</label>
        <div class="controls">
            <g:textArea class="input-xxlarge" maxlength="1000" name="description" value="${artifactInstance?.description}" />
        </div>
    </div>
    
    <div class="control-group">
        <label class="control-label" for="deprecated">Deprecated</label>
        <div class="controls">
            <g:checkBox class="checkbox" name="deprecated" value="${artifactInstance.deprecated}" />
        </div>
    </div>

    <div class="control-group">
        <div class="controls">
            <a class="btn" href="${createLink(action:'projectList')}">Cancel</a>
            <button class="btn btn-primary" type="submit">Save</button>
        </div>
    </div>
</g:form>
</body>
</html>