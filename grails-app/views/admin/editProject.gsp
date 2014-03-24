<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="adminLayout"/>
        <title>ALA Downloads - Administration - ${projectInstance ? 'Edit Project' : 'New Project'}</title>
        <r:script>
        </r:script>
    </head>
    <body class="content">
        <content tag="pageTitle">${projectInstance ? 'Edit Project' : 'New Project'}</content>
        <g:form action="saveProject" class="form-horizontal">
            <g:hiddenField name="projectId" value="${projectInstance?.id}" />

            <div class="control-group">
                <label class="control-label" for="name">Name</label>
                <div class="controls">
                    <g:textField name="name" value="${projectInstance?.name}"/>
                </div>
            </div>

            <div class="control-group">
                <label class="control-label" for="description">Description</label>
                <div class="controls">
                    <g:textArea class="input-xxlarge" name="description" value="${projectInstance?.description}" />
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
