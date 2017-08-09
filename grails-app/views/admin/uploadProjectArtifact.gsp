<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="adminLayout"/>
        <title>Upload Artifact for ${projectInstance.name}</title>
        <r:script>
        </r:script>
    </head>
    <body class="content">
        <content tag="adminButtonBar">
            <a href="${createLink(controller:'admin', action:'projectArtifacts', params:[projectId: projectInstance.id])}" class="btn btn-default btn-sm"><i class="glyphicon glyphicon-chevron-left"></i>&nbsp;Back to ${projectInstance.name} artifacts</a>
        </content>

        <content tag="pageTitle">Upload Artifact - ${projectInstance.name}</content>
        <g:uploadForm action="uploadArtifact" class="form-horizontal">

            <g:hiddenField name="projectId" value="${projectInstance?.id}" />
            <form class="form-horizontal">

                <div class="form-group">
                    <label class="control-label col-md-2" for="file">Name</label>
                    <div class="col-md-10">
                        <input type="file" name="file" id="file" />
                    </div>
                </div>

                %{--<div class="form-group">--}%
                    %{--<label class="control-label col-md-2" for="name">Name</label>--}%
                    %{--<div class="col-md-10">--}%
                        %{--<g:textField name="name" value=""/>--}%
                    %{--</div>--}%
                %{--</div>--}%

                <div class="form-group">
                    <label class="control-label col-md-2" for="summary">Summary</label>
                    <div class="col-md-10">
                        <g:textField class="form-control" name="summary" value="" maxlength="100"/>
                    </div>
                </div>

                <div class="form-group">
                    <label class="control-label col-md-2" for="description">Description</label>
                    <div class="col-md-10">
                        <g:textArea class="form-control" name="description" value="" maxlength="1000"/>
                    </div>
                </div>

                <div class="form-group">
                    <div class="col-md-offset-2 col-md-10">
                        <a class="btn btn-default" href="${createLink(action:'projectList')}">Cancel</a>
                        <button class="btn btn-primary" type="submit">Upload</button>
                    </div>
                </div>
            </form>
        </g:uploadForm>
    </body>
</html>
