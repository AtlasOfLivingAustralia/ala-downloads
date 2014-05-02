import org.codehaus.gant.GantBinding

eventCompileStart = { GantBinding kind ->

}

eventWarStart = { GantBinding kind ->
    if (kind.getVariable('grailsEnv') == 'production') {
        def buildNumber = metadata.'app.buildNumber'

        if (!buildNumber)
            buildNumber = 1
        else
            buildNumber = Integer.valueOf(buildNumber) + 1

        metadata.'app.buildNumber' = buildNumber.toString()

        metadata.persist()

        //println "${kind.variables}"
        println "**** WAR Starting on Build #${buildNumber}"
    }
}