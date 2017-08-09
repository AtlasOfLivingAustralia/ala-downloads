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

            <form class="form-horizontal">
                <div class="form-group">
                    <label class="control-label col-md-2" for="name">Name</label>
                    <div class="col-md-10">
                        <g:textField class="form-control" name="name" value="${projectInstance?.name}"/>
                    </div>
                </div>

                <div class="form-group">
                    <label class="control-label col-md-2" for="summary">Summary</label>
                    <div class="col-md-10">
                        <g:textField class="form-control" name="summary" value="${projectInstance?.summary}" />
                    </div>
                </div>

                <div class="form-group">
                    <label class="control-label col-md-2" or="description">Description</label>
                    <div class="col-md-10">
                        <g:textArea class="form-control" row="5" name="description" value="${projectInstance?.description}" />
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
