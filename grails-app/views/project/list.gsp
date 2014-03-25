<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>ALA Downloads - Projects</title>
        <r:script>
            $(document).ready(function() {
            });
        </r:script>

        <style>
        </style>

    </head>
    <body class="content">
        <div>
            <h1>Projects</h1>
            <table class="table table-bordered table-condensed">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Description</th>
                    <th>Files</th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${projects}" var="project">
                    <tr projectId="${project.id}">
                        <td><g:link controller="project" action="artifactList" id="${project.id}">${project.name}</g:link></td>
                        <td><small>${project.description}</small></td>
                        <td>${project.artifacts.size()}</td>
                    </tr>
                </g:each>
                </tbody>

            </table>
        </div>
    </body>
</html>