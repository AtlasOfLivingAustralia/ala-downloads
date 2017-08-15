<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>ALA Downloads - Projects</title>
        <feed:meta kind="atom" version="1.0" controller="feed" action="projects"/>
    </head>
    <body class="content">
        <div class="container-fluid">
            <legend>
                <table style="width: 100%">
                    <tbody>
                    <tr>
                        <td>Projects</td>
                        <td style="text-align: right">
                            <span>
                                <auth:ifAllGranted roles="${au.org.ala.web.CASRoles.ROLE_ADMIN}">
                                    <g:link controller="admin" action="projectList" class="btn btn-sm">Projects Admin</g:link>
                                </auth:ifAllGranted>
                            </span>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </legend>
            <g:render template="table" />
        </div>
    </body>
</html>