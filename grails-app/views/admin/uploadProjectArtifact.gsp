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
            <a href="${createLink(controller:'admin', action:'projectArtifacts', params:[projectId: projectInstance.id])}" class="btn btn-small"><i class="icon-chevron-left"></i>&nbsp;Back to ${projectInstance.name} artifacts</a>
        </content>

        <content tag="pageTitle">Upload Artifact - ${projectInstance.name}</content>
        <g:uploadForm action="uploadArtifact" class="form-horizontal">

            <g:hiddenField name="projectId" value="${projectInstance?.id}" />

            <div class="control-group">
                <label class="control-label" for="file">Name</label>
                <div class="controls">
                    <input type="file" name="file" id="file" />
                </div>
            </div>

            %{--<div class="control-group">--}%
                %{--<label class="control-label" for="name">Name</label>--}%
                %{--<div class="controls">--}%
                    %{--<g:textField name="name" value=""/>--}%
                %{--</div>--}%
            %{--</div>--}%

            <div class="control-group">
                <label class="control-label" for="summary">Summary</label>
                <div class="controls">
                    <g:textField class="input-xxlarge" name="summary" value="" maxlength="100"/>
                </div>
            </div>

            <div class="control-group">
                <label class="control-label" for="description">Description</label>
                <div class="controls">
                    <g:textArea class="input-xxlarge" name="description" value="" maxlength="1000"/>
                </div>
            </div>

            <div class="control-group">
                <div class="controls">
                    <a class="btn" href="${createLink(action:'projectList')}">Cancel</a>
                    <button class="btn btn-primary" type="submit">Upload</button>
                </div>
            </div>
        </g:uploadForm>
    </body>
</html>
