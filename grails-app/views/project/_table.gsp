<g:if test="${!projects}">
    <p>No projects are currently available.</p>
</g:if>
<g:else>
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
            <td><g:link mapping="projectByName" params="[projectName:project.name]">${project.name}</g:link></td>
            <td><small>${project.summary}</small></td>
            <td>${project.artifacts.size()}</td>
        </tr>
    </g:each>
    </tbody>
</table>
</g:else>