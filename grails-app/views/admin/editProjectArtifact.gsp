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

    <form class="form-horizontal">
        <div class="form-group">
        <label class="control-label col-md-2" for="name">Name</label>
        <div class="col-md-10"">
            <g:textField name="name" class="form-control" readonly="true" value="${artifactInstance?.name}" />
    </div>
        </div>



    <div class="form-group ${hasErrors(bean: artifactInstance, field: 'summary', 'error')}">
        <label class="control-label col-md-2" for="summary">Summary</label>
        <div class="col-md-10"">
            <g:textField class="form-control" maxlength="100" name="summary" value="${artifactInstance?.summary}" />
        </div>
    </div>

    <div class="form-group ${hasErrors(bean: artifactInstance, field: 'description', 'error')}">
        <label class="control-label col-md-2" for="description">Description</label>
        <div class="col-md-10"">
            <g:textArea class="form-control" maxlength="1000" name="description" value="${artifactInstance?.description}" />
        </div>
    </div>
    
    <div class="form-group">
        <label class="control-label col-md-2" for="deprecated">Deprecated</label>
        <div class="col-md-10"">
            <g:checkBox class="checkbox" name="deprecated" value="${artifactInstance.deprecated}" />
        </div>
    </div>

    <div class="form-group">
        <div class="col-md-offset-2 col-md-10">
            <a class="btn btn-default" href="${createLink(action:'projectList')}">Cancel</a>
            <button class="btn btn-primary" type="submit">Save</button>
        </div>
    </div>
    </form>
</g:form>
</body>
</html>